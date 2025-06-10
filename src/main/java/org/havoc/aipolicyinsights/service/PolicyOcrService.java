package org.havoc.aipolicyinsights.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.havoc.aipolicyinsights.exception.DocumentProcessingException;
import org.havoc.aipolicyinsights.model.PolicyDocument;
import org.springframework.beans.factory.annotation.Value;
import org.havoc.aipolicyinsights.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.havoc.aipolicyinsights.enums.Sources.IMAGE;
import static org.havoc.aipolicyinsights.enums.TesseractEnums.ENG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyOcrService {

    private final PolicyRepository policyRepository;

    @Value("${policy.ttl.days}")
    private int ttlDays;

    @Value("${tesseract.data-path}")
    private String tesseractDataPath;

    public PolicyDocument processImages(List<MultipartFile> images) {
        log.info("📸 Starting OCR process for {} image(s)", images.size());

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractDataPath);
        tesseract.setLanguage(ENG.getValue());

        StringBuilder fullText = new StringBuilder();

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            try {
                File tempFile = File.createTempFile("ocr-", image.getOriginalFilename());
                image.transferTo(tempFile);

                log.info("📸 Starting OCR process for image number {}", i + 1);
                String text = tesseract.doOCR(tempFile);

                // Attempt delete and log failure
                if (!tempFile.delete()) {
                    log.warn("⚠️ Failed to delete temporary file: {}", tempFile.getAbsolutePath());
                }

                log.info("✅ OCR complete for page {} ({} chars)", i + 1, text.length());
                fullText.append("\n--- Page").append(i + 1).append(" ---\n").append(text).append("\n");

                log.debug("📝 Text preview (first 100 chars): {}", text.length() > 100 ? text.substring(0, 100) + "..." : text);


            } catch (IOException | TesseractException e) {
                log.error("❌ OCR failed for image: {} with exception {} ", image.getOriginalFilename(), e.getMessage(), e);
                throw new DocumentProcessingException("OCR failed for image: " + image.getOriginalFilename());
            }
        }

        ZonedDateTime nowIST = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        Instant createdAt = nowIST.toInstant();

        long ttlSeconds = ttlDays * 24 * 60 * 60L;

        PolicyDocument document = PolicyDocument.builder()
                                    .title(images.get(0).getOriginalFilename())
                                    .text(fullText.toString())
                                    .source(IMAGE.getName())
                                    .createdAt(createdAt)
                                    .ttlInSeconds(ttlSeconds)
                                    .build();

        PolicyDocument saved = policyRepository.save(document);
        log.info("📦 PolicyDocument saved with UUID: {}", saved.getUuid());

        return saved;
    }


}

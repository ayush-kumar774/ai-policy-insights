package org.havoc.aipolicyinsights.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.havoc.aipolicyinsights.client.LLMClient;
import org.havoc.aipolicyinsights.dto.UploadTextRequest;
import org.havoc.aipolicyinsights.enums.Sources;
import org.havoc.aipolicyinsights.exception.DocumentProcessingException;
import org.havoc.aipolicyinsights.model.PolicyDocument;
import org.havoc.aipolicyinsights.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Objects;

import static org.havoc.aipolicyinsights.enums.Sources.TEXT;


@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    private final LLMClient llmClient;

    @Value("${policy.ttl.days}")
    private int ttlDays;

    public PolicyDocument uploadPdfAndExtractText(MultipartFile file) {
        String filename = file.getOriginalFilename();
        log.info("🔍 Starting PDF upload for file: {}", filename);

        String extractedText;

        try (
                InputStream inputStream = file.getInputStream();
                PDDocument document = PDDocument.load(inputStream)
        ) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            extractedText = pdfTextStripper.getText(document);

            log.info("✅ Successfully extracted text from PDF: {}", filename);
            log.debug("📝 Extracted text preview (first 100 chars): {}", extractedText.length() > 100 ? extractedText.substring(0, 100) + "..." : extractedText);

        } catch (Exception e) {
            log.error("❌ Failed to extract text from PDF: {}", filename, e);
            throw new DocumentProcessingException("Failed to extract text from PDF: " + filename, e);
        }

        ZonedDateTime nowIst = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        Instant createdAt = nowIst.toInstant();

        long ttlSeconds = ttlDays * 24 * 60 * 60L;
        log.info("📦 Creating policy document with TTL {} seconds", ttlSeconds);

        PolicyDocument document = PolicyDocument.builder()
                .title(filename)
                .text(extractedText)
                .source(Sources.PDF.getName())
                .createdAt(createdAt)
                .ttlInSeconds(ttlSeconds)
                .build();

        log.info("📦 Saving policy document to MongoDB with TTL {} seconds", ttlSeconds);

        PolicyDocument saved = policyRepository.save(document);

        log.info("🎉 Document saved successfully with UUID: {}", saved.getUuid());

        return saved;
    }

    public PolicyDocument uploadPlainText(UploadTextRequest uploadTextRequest) {
        log.info("📝 Uploading plain text policy with title: {}", uploadTextRequest.getTitle());

        ZonedDateTime nowIST = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        Instant createdAt = nowIST.toInstant();
        long ttlSeconds = ttlDays * 24 * 60 * 60L;

        PolicyDocument document = PolicyDocument.builder()
                .title(uploadTextRequest.getTitle())
                .text(uploadTextRequest.getContent())
                .source(TEXT.getName())
                .createdAt(createdAt)
                .ttlInSeconds(ttlSeconds)
                .tags(Objects.isNull(uploadTextRequest.getTags()) ? Collections.singletonList("null") : uploadTextRequest.getTags())
                .build();

        log.info("📦 Saving plain text policy to MongoDB with TTL: {} seconds", ttlSeconds);

        PolicyDocument saved = policyRepository.save(document);

        log.info("🎉 Plain text document saved successfully with UUID: {}", saved.getUuid());

        return saved;

    }

    public String answerQuestion(String policyId, String question) throws Exception {
        PolicyDocument policyDocument = policyRepository.findById(policyId)
                .orElseThrow(() -> new Exception("Policy not found with ID: " + policyId));

        String context = policyDocument.getText();

        log.info("🤖 Asking LLM: {}", question);
        log.debug("📄 Using context (first 500 chars): {}", context.length() > 500 ? context.substring(0, 500) + "..." : context);

        // Ask HuggingFace
        return llmClient.askQuestion(context, question);
    }
}

package org.havoc.aipolicyinsights.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.havoc.aipolicyinsights.dto.UploadTextRequest;
import org.havoc.aipolicyinsights.model.PolicyDocument;
import org.havoc.aipolicyinsights.service.PolicyOcrService;
import org.havoc.aipolicyinsights.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    private final PolicyOcrService policyOcrService;

    @PostMapping("/upload/pdf")
    public ResponseEntity<?> uploadPdf(@RequestParam("file")MultipartFile file) {
        try {
            PolicyDocument document = policyService.uploadPdfAndExtractText(file);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process PDF: " + e.getMessage());
        }
    }

    @PostMapping("/upload/images")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        try {
            PolicyDocument document = policyOcrService.processImages(files);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process Images: " + e.getMessage());
        }
    }

    @PostMapping("/upload/text")
    public ResponseEntity<?> uploadPlainText(@Valid @RequestBody UploadTextRequest uploadTextRequest) {
        try {
            PolicyDocument document = policyService.uploadPlainText(uploadTextRequest);
            return ResponseEntity.ok(document);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process text input: " + e.getMessage());
        }
    }



}
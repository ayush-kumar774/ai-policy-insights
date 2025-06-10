package org.havoc.aipolicyinsights.controller;

import lombok.RequiredArgsConstructor;
import org.havoc.aipolicyinsights.dto.AskRequest;
import org.havoc.aipolicyinsights.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AIController {

    private final PolicyService policyService;

    @PostMapping("/ask")
    public ResponseEntity<?> askPolicyQuestion(@RequestBody AskRequest request) {
        try {
            String answer = policyService.answerQuestion(request.getPolicyId(), request.getQuestion());
            return ResponseEntity.ok(Map.of("answer", answer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Failed to answer question: " + e.getMessage());
        }
    }
}

package org.havoc.aipolicyinsights.controller;


import lombok.RequiredArgsConstructor;
import org.havoc.aipolicyinsights.model.PolicyDocument;
import org.havoc.aipolicyinsights.service.PolicyQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyQueryController {

    private final PolicyQueryService queryService;

    @GetMapping("/search")
    public ResponseEntity<List<PolicyDocument>> searchPolicies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate
    ) {
        List<PolicyDocument> results = queryService.searchPolicies(keyword, source, tags, fromDate, toDate);
        return ResponseEntity.ok(results);
    }
}

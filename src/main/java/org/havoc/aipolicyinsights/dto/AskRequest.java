package org.havoc.aipolicyinsights.dto;

import lombok.Data;

@Data
public class AskRequest {
    private String policyId;
    private String question;
}

package org.havoc.aipolicyinsights.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ZonedDateTime timestamp;
    private String error;
    private String exception;
    private String file;
    private String method;
    private int line;
    private String message;
}

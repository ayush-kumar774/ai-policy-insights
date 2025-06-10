package org.havoc.aipolicyinsights.exception;

import org.havoc.aipolicyinsights.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<?> handleDocumentError(DocumentProcessingException ex) {
        StackTraceElement origin = ex.getStackTrace()[0];

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now().atZone(ZoneId.of("Asia/Kolkata")))
                .error("Failed to read or process uploaded document. Please check the file format and clarity.")
                .exception(ex.getClass().getSimpleName())
                .file(Objects.nonNull(origin.getFileName()) ?origin.getFileName() : "UnknownFile")
                .method(origin.getMethodName() + "()")
                .line(origin.getLineNumber())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



    // Optional fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        // Get where the error originated
        StackTraceElement origin = ex.getStackTrace()[0];

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now().atZone(ZoneId.of("Asia/Kolkata")))
                .error("Internal Server Error")
                .exception(ex.getClass().getSimpleName())
                .file(Objects.nonNull(origin.getFileName()) ? origin.getFileName() : "UnknownFile")
                .method(origin.getMethodName() + "()")
                .line(origin.getLineNumber())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", Instant.now().atZone(ZoneId.of("Asia/Kolkata")));
        response.put("error", "Validation failed");
        response.put("details", errors);

        return ResponseEntity.badRequest().body(response);
    }

}

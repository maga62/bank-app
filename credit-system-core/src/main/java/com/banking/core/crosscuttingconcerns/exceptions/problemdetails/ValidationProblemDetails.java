package com.banking.core.crosscuttingconcerns.exceptions.problemdetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Validasyon hatalarını detaylandırmak için kullanılan problem detayları sınıfı.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationProblemDetails extends ProblemDetails {
    private Map<String, String> validationErrors;
    private LocalDateTime timestamp;

    public ValidationProblemDetails() {
        setType("https://example.com/probs/validation");
        setTitle("Validation Error");
        setStatus(HttpStatus.BAD_REQUEST.value());
        setDetail("Validation errors occurred");
        this.timestamp = LocalDateTime.now();
    }

    public ValidationProblemDetails(String detail) {
        setType("https://example.com/probs/validation");
        setTitle("Validation Error");
        setStatus(HttpStatus.BAD_REQUEST.value());
        setDetail(detail);
        this.timestamp = LocalDateTime.now();
    }

    public ValidationProblemDetails(Map<String, String> validationErrors) {
        setType("https://example.com/probs/validation");
        setTitle("Validation Error");
        setStatus(HttpStatus.BAD_REQUEST.value());
        setDetail("Validation errors occurred");
        this.validationErrors = validationErrors;
        this.timestamp = LocalDateTime.now();
    }
} 
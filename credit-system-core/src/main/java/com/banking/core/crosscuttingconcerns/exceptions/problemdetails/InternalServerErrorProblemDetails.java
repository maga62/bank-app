package com.banking.core.crosscuttingconcerns.exceptions.problemdetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class InternalServerErrorProblemDetails extends ProblemDetails {
    public InternalServerErrorProblemDetails() {
        setType("https://example.com/probs/internal");
        setTitle("Internal Server Error");
        setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        setDetail("An unexpected error occurred");
    }
} 
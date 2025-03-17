package com.banking.core.crosscuttingconcerns.exceptions.problemdetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class NotFoundProblemDetails extends ProblemDetails {
    public NotFoundProblemDetails(String detail) {
        setType("https://example.com/probs/not-found");
        setTitle("Resource Not Found");
        setStatus(HttpStatus.NOT_FOUND.value());
        setDetail(detail);
    }
} 
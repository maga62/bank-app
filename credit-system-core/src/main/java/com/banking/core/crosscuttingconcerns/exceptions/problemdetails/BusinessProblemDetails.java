package com.banking.core.crosscuttingconcerns.exceptions.problemdetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessProblemDetails extends ProblemDetails {
    public BusinessProblemDetails(String detail) {
        setType("https://example.com/probs/business");
        setTitle("Business Rule Violation");
        setStatus(HttpStatus.BAD_REQUEST.value());
        setDetail(detail);
    }
} 
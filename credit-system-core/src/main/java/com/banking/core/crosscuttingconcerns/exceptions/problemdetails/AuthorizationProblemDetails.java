package com.banking.core.crosscuttingconcerns.exceptions.problemdetails;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorizationProblemDetails extends ProblemDetails {
    public AuthorizationProblemDetails() {
        setType("https://example.com/probs/authorization");
        setTitle("Authorization Error");
        setStatus(HttpStatus.UNAUTHORIZED.value());
        setDetail("You are not authorized to perform this action");
    }
} 
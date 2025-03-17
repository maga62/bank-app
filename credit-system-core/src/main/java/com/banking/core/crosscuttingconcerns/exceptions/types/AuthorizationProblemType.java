package com.banking.core.crosscuttingconcerns.exceptions.types;

import org.springframework.http.HttpStatus;

public class AuthorizationProblemType extends ProblemType {
    public AuthorizationProblemType() {
        super(
            "AUTHORIZATION_VIOLATION",
            "Authorization Error",
            HttpStatus.UNAUTHORIZED
        );
    }
} 
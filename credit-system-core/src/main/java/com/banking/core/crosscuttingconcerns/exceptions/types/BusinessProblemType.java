package com.banking.core.crosscuttingconcerns.exceptions.types;

import org.springframework.http.HttpStatus;

public class BusinessProblemType extends ProblemType {
    public BusinessProblemType() {
        super(
            "BUSINESS_RULE_VIOLATION",
            "Business Rule Violation",
            HttpStatus.BAD_REQUEST
        );
    }
} 
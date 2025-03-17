package com.banking.core.crosscuttingconcerns.exceptions.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public abstract class ProblemType {
    private final String type;
    private final String title;
    private final HttpStatus status;
} 
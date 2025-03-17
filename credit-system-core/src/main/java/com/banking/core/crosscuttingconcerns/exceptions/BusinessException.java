package com.banking.core.crosscuttingconcerns.exceptions;

/**
 * İş kuralı ihlallerinde fırlatılan istisna sınıfı.
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 
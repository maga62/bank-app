package com.banking.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnauthorizedAccessException(String resource, String action) {
        super(String.format("You don't have permission to %s on %s", action, resource));
    }
    
    public UnauthorizedAccessException(Long userId, String resource, String action) {
        super(String.format("User %d doesn't have permission to %s on %s", userId, action, resource));
    }
} 
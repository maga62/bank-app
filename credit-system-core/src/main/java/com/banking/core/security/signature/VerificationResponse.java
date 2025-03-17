package com.banking.core.security.signature;

import lombok.Data;

/**
 * e-İmza API'sinden gelen doğrulama yanıtı.
 */
@Data
public class VerificationResponse {
    private boolean valid;
    private String resultMessage;
} 
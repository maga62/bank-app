package com.banking.core.security.signature;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * e-İmza API'sinden gelen imza yanıtı.
 */
@Data
public class SignatureResponse {
    private boolean success;
    private String errorMessage;
    private String signatureData;
    private String certificateSerialNumber;
    private String certificateIssuer;
    private String certificateSubject;
    private LocalDateTime certificateValidFrom;
    private LocalDateTime certificateValidTo;
} 
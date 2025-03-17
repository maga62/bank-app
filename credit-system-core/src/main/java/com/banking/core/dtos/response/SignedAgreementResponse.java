package com.banking.core.dtos.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SignedAgreementResponse {
    private Long id;
    private Long customerId;
    private String agreementText;
    private String signedBy;
    private LocalDateTime signedAt;
    private String signatureHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Şüpheli işlem yanıtı için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousTransactionResponse {
    
    private Long id;
    private Long customerId;
    private String transactionId;
    private BigDecimal amount;
    private String transactionType;
    private LocalDateTime transactionDate;
    private String riskLevel;
    private String riskReason;
    private String status;
    private LocalDateTime reportDate;
    private LocalDateTime resolutionDate;
    private boolean isFalsePositive;
    private String ipAddress;
    private String location;
} 
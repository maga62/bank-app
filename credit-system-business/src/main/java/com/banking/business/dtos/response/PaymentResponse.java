package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ödeme yanıtı için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private String paymentId;
    private Long customerId;
    private String customerName;
    private Long applicationId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentType;
    private String paymentProvider;
    private LocalDateTime paymentDate;
    private String transactionId;
    private String receiptUrl;
    private String description;
    private String errorCode;
    private String errorMessage;
    private String redirectUrl;
    private String provider;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
} 
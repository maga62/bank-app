package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Ödeme durumu yanıtı için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    
    private String paymentId;
    private String status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paymentDate;
    private LocalDateTime lastUpdated;
    private String paymentProvider;
    private String paymentMethod;
    private String transactionId;
    private String receiptUrl;
    private Map<String, Object> additionalInfo;
    private String errorCode;
    private String errorMessage;
} 
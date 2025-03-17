package com.banking.webapi.models.responses;

import com.banking.entities.enums.PaymentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentStatusResponse {
    private Long id;
    private Long loanId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String paymentMethod;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
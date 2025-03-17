package com.banking.webapi.models.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProcessPaymentRequest {
    @NotNull(message = "Loan ID cannot be null")
    private Long loanId;
    
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment method cannot be null")
    private String paymentMethod;
    
    private String description;
} 
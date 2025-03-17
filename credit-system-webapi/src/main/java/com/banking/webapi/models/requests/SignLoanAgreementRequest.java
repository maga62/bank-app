package com.banking.webapi.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignLoanAgreementRequest {
    @NotNull(message = "Loan agreement ID cannot be null")
    private Long loanAgreementId;
    
    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;
    
    @NotNull(message = "Signature data cannot be null")
    private String signatureData;
} 
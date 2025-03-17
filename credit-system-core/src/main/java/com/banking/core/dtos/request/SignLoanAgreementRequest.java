package com.banking.core.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignLoanAgreementRequest {
    @NotNull
    private Long loanAgreementId;

    @NotNull
    private Long customerId;

    @NotNull
    private String signatureData;
} 
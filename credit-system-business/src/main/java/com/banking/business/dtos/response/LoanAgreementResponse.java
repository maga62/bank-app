package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kredi sözleşmesi yanıtı için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAgreementResponse {
    private Long applicationId;
    private Long customerId;
    private String agreementContent;
    private LocalDateTime createdDate;
    private LocalDateTime signedDate;
    private boolean isSigned;
} 
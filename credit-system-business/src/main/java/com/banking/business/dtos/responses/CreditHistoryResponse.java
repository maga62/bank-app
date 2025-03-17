package com.banking.business.dtos.responses;

import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditHistoryResponse {
    private Long id;
    private Long customerId;
    private CreditType creditType;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer term;
    private LocalDate startDate;
    private LocalDate endDate;
    private CreditHistoryStatus status;
    private BigDecimal outstandingAmount;
    private BigDecimal totalPaid;
    private Integer remainingInstallments;
    private LocalDate lastPaymentDate;
    private LocalDate nextPaymentDate;
    private BigDecimal nextPaymentAmount;
    private Boolean isOverdue;
    private Integer daysOverdue;
} 
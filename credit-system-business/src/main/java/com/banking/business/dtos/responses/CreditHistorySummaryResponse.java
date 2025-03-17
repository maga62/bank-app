package com.banking.business.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditHistorySummaryResponse {
    private Long customerId;
    private Integer totalCredits;
    private Integer activeCredits;
    private Integer completedCredits;
    private Integer defaultedCredits;
    private BigDecimal totalCreditAmount;
    private BigDecimal totalOutstandingAmount;
    private BigDecimal totalPaidAmount;
    private Map<String, Integer> creditTypeDistribution;
    private Double averageInterestRate;
    private Integer averageTerm;
    private Boolean hasOverduePayments;
    private Integer creditScore;
} 
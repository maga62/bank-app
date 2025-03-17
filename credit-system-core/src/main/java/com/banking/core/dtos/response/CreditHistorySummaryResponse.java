package com.banking.core.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditHistorySummaryResponse {
    private String identityNumber;
    private Integer totalCredits;
    private Integer activeCredits;
    private Integer closedCredits;
    private Double totalCreditAmount;
    private Double remainingDebt;
    private List<String> creditTypes;
    private LocalDateTime lastCreditDate;
    private LocalDateTime reportDate;
} 
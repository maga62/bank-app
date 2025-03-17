package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Merkez Bankası raporu yanıtı için veri transfer nesnesi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentralBankReportResponse {
    private Long customerId;
    private String customerName;
    private String identityNumber;
    private String fullName;
    private Double creditScore;
    private Integer totalLoans;
    private Double totalDebt;
    private Boolean hasDefaultedLoan;
    private LocalDateTime reportDate;
    private String reportId;
    private boolean cached;
    private LocalDateTime cacheDate;
} 
package com.banking.business.services.refinancing;

import com.banking.entities.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Kredi refinansman fırsatı.
 * Mevcut bir kredinin daha uygun koşullarla yeniden yapılandırılması için teklif bilgilerini içerir.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefinancingOpportunity {
    
    private Long id;
    private Long creditId;
    private Long customerId;
    private CreditType creditType;
    
    private BigDecimal currentInterestRate;
    private BigDecimal newInterestRate;
    private double interestRateReduction;
    
    private BigDecimal remainingAmount;
    private int remainingTerm;
    
    private BigDecimal currentMonthlyPayment;
    private BigDecimal newMonthlyPayment;
    private BigDecimal monthlySavings;
    private BigDecimal totalSavings;
    
    private boolean applied;
    private String status;
} 
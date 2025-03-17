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
public class PaymentPerformanceResponse {
    private Long customerId;
    private Integer totalPayments;
    private Integer onTimePayments;
    private Integer latePayments;
    private Integer missedPayments;
    private Double onTimePaymentPercentage;
    private Integer averageDaysLate;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalPenaltyAmount;
    private Map<String, Integer> monthlyPaymentTrend;
    private String paymentReliabilityRating; // EXCELLENT, GOOD, FAIR, POOR
    private Integer consecutiveOnTimePayments;
    private Integer longestStreak;
} 
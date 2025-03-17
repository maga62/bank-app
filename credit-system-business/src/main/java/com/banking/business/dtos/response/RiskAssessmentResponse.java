package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Risk değerlendirme yanıtı için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResponse {
    private String transactionId;
    private Long customerId;
    private String riskLevel;
    private String riskReason;
    private LocalDateTime assessmentDate;
    private boolean requiresManualReview;
    private boolean isBlocked;
} 
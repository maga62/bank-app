package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Risk skoru yanıtı için veri transfer nesnesi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskScoreResponse {
    private String identityNumber;
    private Integer riskScore;
    private String riskLevel;
    private Map<String, Double> scoreComponents;
    private String recommendation;
    private LocalDateTime calculationDate;
    private LocalDateTime validUntil;
    private String reportId;
} 
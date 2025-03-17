package com.banking.core.dtos.response;

import lombok.Data;

@Data
public class RiskScoreResponse {
    private Long customerId;
    private Double score;
    private String scoreCategory;
    private String calculationDate;
    private String validUntil;
} 
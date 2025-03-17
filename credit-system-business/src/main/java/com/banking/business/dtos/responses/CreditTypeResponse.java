package com.banking.business.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditTypeResponse {
    private String name;
    private String description;
    private double minInterestRate;
    private double maxInterestRate;
    private int minTermMonths;
    private int maxTermMonths;
    private boolean isActive;
}
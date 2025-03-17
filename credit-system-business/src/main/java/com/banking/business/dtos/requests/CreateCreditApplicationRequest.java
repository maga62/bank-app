package com.banking.business.dtos.requests;

import com.banking.entities.enums.CreditType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCreditApplicationRequest {
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotNull
    @Min(1)
    private Integer termMonths;
    
    @NotNull
    private CreditType creditType;
    
    @NotNull
    private Long customerId;
    
    @NotNull
    @Positive
    private BigDecimal monthlyIncome;
    
    private String notes;
} 
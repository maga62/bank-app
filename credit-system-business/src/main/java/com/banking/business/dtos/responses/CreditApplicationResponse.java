package com.banking.business.dtos.responses;

import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.entities.enums.CreditType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationResponse {
    private Long id;
    private BigDecimal amount;
    private Integer termMonths;
    private CreditApplicationStatus status;
    private CreditType creditType;
    private Long customerId;
    private String customerNumber;
    private BigDecimal monthlyIncome;
    private String notes;
    private LocalDateTime createdDate;
} 
package com.banking.entities;

import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.entities.enums.IndividualCreditType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "individual_credit_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class IndividualCreditApplication extends BaseEntity<Long> {
    
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @NotNull(message = "Term months cannot be null")
    @Min(value = 1, message = "Term months must be at least 1")
    @Column(name = "term_months", nullable = false)
    private Integer termMonths;
    
    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CreditApplicationStatus status;
    
    @NotNull(message = "Credit type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false)
    private IndividualCreditType creditType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private IndividualCustomer customer;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Monthly income must be non-negative")
    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    @Column(name = "rejection_date")
    private LocalDateTime rejectionDate;
} 
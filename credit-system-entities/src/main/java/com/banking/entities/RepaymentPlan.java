package com.banking.entities;

import com.banking.entities.enums.RepaymentFrequency;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Müşteri kredi geri ödeme planı tablosu.
 * Kredi taksit yapısını ve ödeme planını detaylandırır.
 */
@Entity
@Table(name = "repayment_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class RepaymentPlan extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", nullable = false)
    private CreditApplication creditApplication;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @NotNull(message = "End date cannot be null")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @NotNull(message = "Total amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    
    @NotNull(message = "Principal amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Principal amount must be greater than 0")
    @Column(name = "principal_amount", nullable = false)
    private BigDecimal principalAmount;
    
    @NotNull(message = "Interest amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Interest amount must be non-negative")
    @Column(name = "interest_amount", nullable = false)
    private BigDecimal interestAmount;
    
    @NotNull(message = "Interest rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Interest rate must be non-negative")
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;
    
    @NotNull(message = "Number of installments cannot be null")
    @Min(value = 1, message = "Number of installments must be at least 1")
    @Column(name = "number_of_installments", nullable = false)
    private Integer numberOfInstallments;
    
    @NotNull(message = "Installment amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Installment amount must be greater than 0")
    @Column(name = "installment_amount", nullable = false)
    private BigDecimal installmentAmount;
    
    @NotNull(message = "Repayment frequency cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "repayment_frequency", nullable = false)
    private RepaymentFrequency repaymentFrequency;
    
    @Column(name = "grace_period_months")
    private Integer gracePeriodMonths;
    
    @Column(name = "early_payment_allowed")
    private Boolean earlyPaymentAllowed;
    
    @Column(name = "early_payment_penalty_rate")
    private BigDecimal earlyPaymentPenaltyRate;
    
    @Column(name = "late_payment_penalty_rate")
    private BigDecimal latePaymentPenaltyRate;
    
    @Column(name = "notes")
    private String notes;
    
    @OneToMany(mappedBy = "repaymentPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentHistory> paymentHistories = new ArrayList<>();
} 
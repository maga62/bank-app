package com.banking.entities;

import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.CreditType;
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

/**
 * Müşteri kredi kullanım geçmişi sınıfı.
 * Müşterilerin önceden kullandığı kredileri, ödeme düzenini ve gecikmeleri temsil eder.
 */
@Entity
@Table(name = "credit_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class CreditHistory extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Credit type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false)
    private CreditType creditType;
    
    @Column(name = "credit_provider")
    private String creditProvider;
    
    @NotNull(message = "Original amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Original amount must be greater than 0")
    @Column(name = "original_amount", nullable = false)
    private BigDecimal originalAmount;
    
    @Column(name = "outstanding_amount")
    private BigDecimal outstandingAmount;
    
    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @NotNull(message = "Term months cannot be null")
    @Min(value = 1, message = "Term months must be at least 1")
    @Column(name = "term_months", nullable = false)
    private Integer termMonths;
    
    @Column(name = "interest_rate")
    private BigDecimal interestRate;
    
    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;
    
    @NotNull(message = "Credit history status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CreditHistoryStatus status;
    
    @Column(name = "total_payments_made")
    private Integer totalPaymentsMade;
    
    @Column(name = "total_payments_due")
    private Integer totalPaymentsDue;
    
    @Column(name = "on_time_payments")
    private Integer onTimePayments;
    
    @Column(name = "late_payments")
    private Integer latePayments;
    
    @Column(name = "missed_payments")
    private Integer missedPayments;
    
    @Column(name = "max_days_late")
    private Integer maxDaysLate;
    
    @Column(name = "average_days_late")
    private Double averageDaysLate;
    
    @Column(name = "total_late_fees")
    private BigDecimal totalLateFees;
    
    @Column(name = "is_closed")
    private Boolean isClosed;
    
    @Column(name = "closure_date")
    private LocalDate closureDate;
    
    @Column(name = "closure_reason")
    private String closureReason;
    
    @Column(name = "notes")
    private String notes;
} 
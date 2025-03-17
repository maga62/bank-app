package com.banking.entities;

import com.banking.entities.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Müşteri kredi ödeme geçmişi tablosu.
 * Müşterinin kredi ödemelerini ve ödeme durumlarını takip eder.
 */
@Entity
@Table(name = "payment_histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class PaymentHistory extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", nullable = false)
    private CreditApplication creditApplication;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repayment_plan_id")
    private RepaymentPlan repaymentPlan;
    
    @NotNull(message = "Due date cannot be null")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @NotNull(message = "Amount due cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount due must be greater than 0")
    @Column(name = "amount_due", nullable = false)
    private BigDecimal amountDue;
    
    @Column(name = "amount_paid")
    private BigDecimal amountPaid;
    
    @NotNull(message = "Payment status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;
    
    @Column(name = "days_late")
    private Integer daysLate;
    
    @Column(name = "late_fee")
    private BigDecimal lateFee;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "transaction_reference")
    private String transactionReference;
    
    @Column(name = "notes")
    private String notes;
} 
package com.banking.entities;

import com.banking.entities.enums.ExpenseFrequency;
import com.banking.entities.enums.ExpenseType;
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
 * Müşteri gider sınıfı.
 * Müşterilerin giderlerini ve detaylarını temsil eder.
 */
@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class Expense extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Expense type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type", nullable = false)
    private ExpenseType expenseType;
    
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @NotNull(message = "Expense frequency cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "expense_frequency", nullable = false)
    private ExpenseFrequency expenseFrequency;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "payee")
    private String payee;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "is_mandatory")
    private Boolean isMandatory;
    
    @Column(name = "is_verified")
    private Boolean isVerified;
    
    @Column(name = "verification_document")
    private String verificationDocument;
    
    @Column(name = "verification_date")
    private LocalDate verificationDate;
    
    @Column(name = "notes")
    private String notes;
} 
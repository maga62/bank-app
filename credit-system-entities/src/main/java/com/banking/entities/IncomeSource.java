package com.banking.entities;

import com.banking.entities.enums.IncomeFrequency;
import com.banking.entities.enums.IncomeType;
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
 * Müşteri gelir kaynağı sınıfı.
 * Müşterilerin gelir kaynaklarını ve detaylarını temsil eder.
 */
@Entity
@Table(name = "income_sources")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class IncomeSource extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Income type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "income_type", nullable = false)
    private IncomeType incomeType;
    
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @NotNull(message = "Income frequency cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "income_frequency", nullable = false)
    private IncomeFrequency incomeFrequency;
    
    @Column(name = "source_name")
    private String sourceName;
    
    @Column(name = "employer_name")
    private String employerName;
    
    @Column(name = "employer_address")
    private String employerAddress;
    
    @Column(name = "employer_phone")
    private String employerPhone;
    
    @Column(name = "position")
    private String position;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "is_verified")
    private Boolean isVerified;
    
    @Column(name = "verification_document")
    private String verificationDocument;
    
    @Column(name = "verification_date")
    private LocalDate verificationDate;
    
    @Column(name = "notes")
    private String notes;
} 
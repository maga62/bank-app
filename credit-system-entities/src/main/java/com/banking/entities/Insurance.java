package com.banking.entities;

import com.banking.entities.enums.InsuranceStatus;
import com.banking.entities.enums.InsuranceType;
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
 * Kredi sigortası sınıfı.
 * Kredi başvurularına eklenen sigorta poliçelerini temsil eder.
 */
@Entity
@Table(name = "insurances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class Insurance extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", nullable = false)
    private CreditApplication creditApplication;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Insurance type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", nullable = false)
    private InsuranceType insuranceType;
    
    @NotNull(message = "Policy number cannot be null")
    @Column(name = "policy_number", nullable = false, unique = true)
    private String policyNumber;
    
    @NotNull(message = "Insurance company cannot be null")
    @Column(name = "insurance_company", nullable = false)
    private String insuranceCompany;
    
    @NotNull(message = "Start date cannot be null")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @NotNull(message = "End date cannot be null")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @NotNull(message = "Premium amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Premium amount must be greater than 0")
    @Column(name = "premium_amount", nullable = false)
    private BigDecimal premiumAmount;
    
    @NotNull(message = "Coverage amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage amount must be greater than 0")
    @Column(name = "coverage_amount", nullable = false)
    private BigDecimal coverageAmount;
    
    @NotNull(message = "Insurance status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_status", nullable = false)
    private InsuranceStatus insuranceStatus;
    
    @Column(name = "beneficiary")
    private String beneficiary;
    
    @Column(name = "payment_frequency")
    private String paymentFrequency;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "agent_name")
    private String agentName;
    
    @Column(name = "agent_contact")
    private String agentContact;
    
    @Column(name = "notes")
    private String notes;
} 
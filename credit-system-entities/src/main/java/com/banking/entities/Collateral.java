package com.banking.entities;

import com.banking.entities.enums.CollateralStatus;
import com.banking.entities.enums.CollateralType;
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
 * Kredi teminatı ana sınıfı.
 * Kredi başvurularında teminat olarak gösterilen varlıkları temsil eder.
 */
@Entity
@Table(name = "collaterals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
@Inheritance(strategy = InheritanceType.JOINED)
public class Collateral extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_application_id", nullable = false)
    private CreditApplication creditApplication;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotNull(message = "Collateral type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "collateral_type", nullable = false)
    private CollateralType collateralType;
    
    @NotNull(message = "Collateral value cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Collateral value must be greater than 0")
    @Column(name = "collateral_value", nullable = false)
    private BigDecimal collateralValue;
    
    @NotNull(message = "Valuation date cannot be null")
    @Column(name = "valuation_date", nullable = false)
    private LocalDate valuationDate;
    
    @Column(name = "description")
    private String description;
    
    @NotNull(message = "Collateral status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "collateral_status", nullable = false)
    private CollateralStatus collateralStatus;
    
    @Column(name = "appraiser_name")
    private String appraiserName;
    
    @Column(name = "appraisal_report_reference")
    private String appraisalReportReference;
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "notes")
    private String notes;
} 
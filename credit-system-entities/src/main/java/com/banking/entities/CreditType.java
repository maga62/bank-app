package com.banking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "credit_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class CreditType extends BaseEntity<Long> {
    
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(name = "description")
    private String description;
    
    @NotNull(message = "Minimum interest rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum interest rate must be non-negative")
    @Column(name = "min_interest_rate", nullable = false)
    private double minInterestRate;
    
    @NotNull(message = "Maximum interest rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum interest rate must be non-negative")
    @Column(name = "max_interest_rate", nullable = false)
    private double maxInterestRate;
    
    @NotNull(message = "Minimum term months cannot be null")
    @Min(value = 1, message = "Minimum term months must be at least 1")
    @Column(name = "min_term_months", nullable = false)
    private int minTermMonths;
    
    @NotNull(message = "Maximum term months cannot be null")
    @Min(value = 1, message = "Maximum term months must be at least 1")
    @Column(name = "max_term_months", nullable = false)
    private int maxTermMonths;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    
    @Column(name = "is_individual", nullable = false)
    private boolean isIndividual;
} 
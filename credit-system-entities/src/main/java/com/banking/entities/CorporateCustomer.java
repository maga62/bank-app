package com.banking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "corporate_customers")
@SQLRestriction("deleted_date IS NULL")
public class CorporateCustomer extends Customer {
    
    @NotBlank(message = "Tax number cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Tax number must be 10 digits")
    @Column(name = "tax_number", unique = true, nullable = false)
    private String taxNumber;
    
    @NotBlank(message = "Company name cannot be blank")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    @Column(name = "company_name", nullable = false)
    private String companyName;
    
    @NotBlank(message = "Trade register number cannot be blank")
    @Column(name = "trade_register_number", nullable = false, unique = true)
    private String tradeRegisterNumber;
    
    @Size(max = 50, message = "Company type must be less than 50 characters")
    @Column(name = "company_type")
    private String companyType;
    
    @Size(max = 100, message = "Authorized person must be less than 100 characters")
    @Column(name = "authorized_person")
    private String authorizedPerson;
} 
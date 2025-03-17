package com.banking.entities;

// import com.banking.core.entities.BaseEntity;
import com.banking.entities.enums.CustomerCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import com.banking.core.security.encryption.CryptoConverter;

import java.time.LocalDate;

/**
 * Müşteri bilgilerini saklayan entity sınıfı.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customers")
@Inheritance(strategy = InheritanceType.JOINED)
@SQLRestriction("deleted_date IS NULL")
public abstract class Customer extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_number", nullable = false, unique = true)
    private String customerNumber;
    
    @Convert(converter = CryptoConverter.class)
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Convert(converter = CryptoConverter.class)
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "status")
    private boolean status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_category")
    private CustomerCategory customerCategory = CustomerCategory.STANDARD; // Varsayılan değer

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "identity_number", nullable = false, unique = true)
    private String identityNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "customer_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "monthly_income")
    private Double monthlyIncome;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "employer")
    private String employer;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "education_level")
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    @Column(name = "has_existing_loan")
    private boolean hasExistingLoan;

    @Column(name = "has_existing_credit_card")
    private boolean hasExistingCreditCard;

    @Column(name = "is_blacklisted")
    private boolean isBlacklisted;

    @Column(name = "blacklist_reason")
    private String blacklistReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_verified")
    private boolean verified;

    @Convert(converter = CryptoConverter.class)
    @Column(name = "iban")
    private String iban;

    /**
     * Müşteri türü enum'u.
     */
    public enum CustomerType {
        INDIVIDUAL,
        CORPORATE,
        VIP,
        STUDENT
    }

    /**
     * Medeni durum enum'u.
     */
    public enum MaritalStatus {
        SINGLE,
        MARRIED,
        DIVORCED,
        WIDOWED
    }

    /**
     * Eğitim seviyesi enum'u.
     */
    public enum EducationLevel {
        PRIMARY,
        SECONDARY,
        HIGH_SCHOOL,
        ASSOCIATE,
        BACHELOR,
        MASTER,
        DOCTORATE
    }

    /**
     * Müşterinin kimlik doğrulamasının yapılıp yapılmadığını kontrol eder.
     * 
     * @return Kimlik doğrulaması yapılmışsa true, değilse false
     */
    public boolean isVerified() {
        return verified;
    }
} 
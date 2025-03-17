package com.banking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "suspicious_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class SuspiciousTransaction extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "detection_date", nullable = false)
    private LocalDateTime detectionDate;
    
    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;
    
    @Column(name = "risk_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    
    @Column(name = "risk_reason")
    private String riskReason;
    
    @Column(name = "detection_rule", nullable = false)
    private String detectionRule;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "resolution_date")
    private LocalDateTime resolutionDate;
    
    @Column(name = "resolution_notes")
    private String resolutionNotes;
    
    @Column(name = "report_date")
    private LocalDateTime reportDate;
    
    @Column(name = "report_notes")
    private String reportNotes;
    
    @Column(name = "resolved_by")
    private String resolvedBy;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "is_false_positive")
    private boolean isFalsePositive;

    public Long getCustomerId() {
        return customer != null ? customer.getId() : null;
    }

    public void setCustomerId(Long customerId) {
        this.customer = null;
    }
    
    /**
     * İşlem türü.
     */
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER,
        LOAN_APPLICATION,
        CREDIT_APPLICATION,
        LOAN_PAYMENT,
        ACCOUNT_OPENING,
        ACCOUNT_CLOSING,
        PROFILE_UPDATE,
        CUSTOMER_INFO_UPDATE,
        PASSWORD_CHANGE,
        OTHER
    }
    
    /**
     * Risk seviyesi.
     */
    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    /**
     * İşlem durumu.
     */
    public enum Status {
        DETECTED,
        PENDING,
        PENDING_REVIEW,
        UNDER_REVIEW,
        CONFIRMED_FRAUD,
        FALSE_POSITIVE,
        RESOLVED,
        REPORTED
    }
} 
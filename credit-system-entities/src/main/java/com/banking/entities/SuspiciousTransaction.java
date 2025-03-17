package com.banking.entities;

import com.banking.entities.enums.RiskLevel;
import com.banking.entities.enums.TransactionStatus;
import com.banking.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Şüpheli işlemleri saklayan entity sınıfı.
 */
@Entity
@Table(name = "suspicious_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name = "detection_date")
    private LocalDateTime detectionDate;
    
    @Column(name = "detection_rule")
    private String detectionRule;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "reason", nullable = false)
    private String reason;
    
    @Column(name = "risk_score", nullable = false)
    private Double riskScore;
    
    @Column(name = "flagged_at", nullable = false)
    private LocalDateTime flaggedAt;
    
    @Column(name = "risk_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(name = "resolution_notes")
    private String resolutionNotes;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolved_by")
    private String resolvedBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (flaggedAt == null) {
            flaggedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
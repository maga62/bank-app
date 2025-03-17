package com.banking.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_reports", indexes = {
    @Index(name = "idx_customer_report_date", columnList = "customerId,reportDate"),
    @Index(name = "idx_risk_score", columnList = "riskScore"),
    @Index(name = "idx_blacklist_status", columnList = "blacklistStatus")
})
@Data
public class RiskReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long customerId;
    
    @Column(nullable = false)
    private LocalDateTime reportDate;
    
    @Column(nullable = false)
    private Double riskScore;
    
    @Column(nullable = false)
    private boolean blacklistStatus;
    
    @Column(nullable = false, length = 50)
    private String reportSource;
    
    @Column(length = 100)
    private String reportReference;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
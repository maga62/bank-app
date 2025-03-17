package com.banking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_agreements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanAgreement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "agreement_text", columnDefinition = "TEXT", nullable = false)
    private String agreementText;
    
    @Column(name = "signed_by", nullable = false)
    private String signedBy;
    
    @Column(name = "signed_at", nullable = false)
    private LocalDateTime signedAt;
    
    @Column(name = "signature_hash", nullable = false)
    private String signatureHash;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
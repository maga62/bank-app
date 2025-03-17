package com.banking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * Dijital imza bilgilerini saklayan entity sınıfı.
 */
@Entity
@Table(name = "digital_signatures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class DigitalSignature extends BaseEntity<Long> {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    
    @Column(name = "document_id")
    private Long documentId;
    
    @Column(name = "document_hash", nullable = false)
    private String documentHash;
    
    @Column(name = "signature_data", nullable = false, columnDefinition = "TEXT")
    private String signatureData;
    
    @Column(name = "signature_date", nullable = false)
    private LocalDateTime signatureDate;
    
    @Column(name = "verification_date")
    private LocalDateTime verificationDate;
    
    @Column(name = "is_verified")
    private boolean verified;
    
    @Column(name = "verification_result")
    private String verificationResult;
    
    @Column(name = "certificate_serial_number")
    private String certificateSerialNumber;
    
    @Column(name = "certificate_issuer")
    private String certificateIssuer;
    
    @Column(name = "certificate_subject")
    private String certificateSubject;
    
    @Column(name = "certificate_valid_from")
    private LocalDateTime certificateValidFrom;
    
    @Column(name = "certificate_valid_to")
    private LocalDateTime certificateValidTo;
    
    /**
     * Belge türleri için enum sınıfı.
     */
    public enum DocumentType {
        LOAN_AGREEMENT,
        CREDIT_APPLICATION,
        CUSTOMER_CONSENT,
        IDENTITY_VERIFICATION,
        TERMS_AND_CONDITIONS,
        PRIVACY_POLICY,
        OTHER
    }
} 
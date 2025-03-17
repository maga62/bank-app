package com.banking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "digital_signatures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("deleted_date IS NULL")
public class DigitalSignature extends BaseEntity<Long> {
    
    @Column(name = "application_id", nullable = false)
    private Long applicationId;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "signature_hash", nullable = false, length = 512)
    private String signatureHash;
    
    @Column(name = "signature_date", nullable = false)
    private LocalDateTime signatureDate;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "device_info")
    private String deviceInfo;
    
    @Column(name = "is_verified")
    private boolean verified;
    
    @Column(name = "is_archived")
    private boolean archived;
    
    @Column(name = "archived_date")
    private LocalDateTime archivedDate;
    
    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    
    @Column(name = "document_id", nullable = false)
    private Long documentId;

    public enum DocumentType {
        LOAN_AGREEMENT,
        CREDIT_APPLICATION,
        CUSTOMER_AGREEMENT,
        TERMS_AND_CONDITIONS
    }
} 
package com.banking.entities.enums;

/**
 * Kredi başvurusu durumları
 */
public enum CreditApplicationStatus {
    /**
     * Başvuru alındı, ilk değerlendirme aşamasında
     */
    PENDING,
    
    /**
     * Başvuru onaylandı
     */
    APPROVED,
    
    /**
     * Başvuru reddedildi
     */
    REJECTED,
    
    /**
     * Başvuru inceleme aşamasında
     */
    IN_REVIEW,
    
    /**
     * Başvuru iptal edildi
     */
    CANCELLED,
    
    /**
     * Başvuru otomatik değerlendirmeden geçti, manuel onay bekliyor
     */
    PENDING_APPROVAL
} 
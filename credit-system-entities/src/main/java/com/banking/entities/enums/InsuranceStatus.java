package com.banking.entities.enums;

/**
 * Sigorta durumunu belirten enum.
 */
public enum InsuranceStatus {
    /**
     * Sigorta başvurusu yapıldı
     */
    APPLIED,
    
    /**
     * Sigorta başvurusu değerlendiriliyor
     */
    UNDER_REVIEW,
    
    /**
     * Sigorta poliçesi aktif
     */
    ACTIVE,
    
    /**
     * Sigorta poliçesi yenilendi
     */
    RENEWED,
    
    /**
     * Sigorta poliçesi askıya alındı
     */
    SUSPENDED,
    
    /**
     * Sigorta poliçesi iptal edildi
     */
    CANCELLED,
    
    /**
     * Sigorta poliçesi sona erdi
     */
    EXPIRED,
    
    /**
     * Sigorta talebi işleniyor
     */
    CLAIM_PROCESSING,
    
    /**
     * Sigorta talebi ödendi
     */
    CLAIM_PAID,
    
    /**
     * Sigorta talebi reddedildi
     */
    CLAIM_REJECTED
} 
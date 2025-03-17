package com.banking.entities.enums;

/**
 * Teminat durumunu belirten enum.
 */
public enum CollateralStatus {
    /**
     * Teminat değerlendirme aşamasında
     */
    PENDING_EVALUATION,
    
    /**
     * Teminat onaylandı
     */
    APPROVED,
    
    /**
     * Teminat reddedildi
     */
    REJECTED,
    
    /**
     * Teminat aktif olarak kullanılıyor
     */
    ACTIVE,
    
    /**
     * Teminat serbest bırakıldı
     */
    RELEASED,
    
    /**
     * Teminat nakde çevrildi
     */
    LIQUIDATED,
    
    /**
     * Teminat değeri düştü
     */
    DEVALUED,
    
    /**
     * Teminat iptal edildi
     */
    CANCELLED
} 
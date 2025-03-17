package com.banking.entities.enums;

/**
 * Kredi geçmişi durumunu belirten enum.
 */
public enum CreditHistoryStatus {
    /**
     * Kredi aktif olarak kullanılıyor
     */
    ACTIVE,
    
    /**
     * Kredi tamamen ödendi
     */
    PAID_OFF,
    
    /**
     * Kredi yeniden yapılandırıldı
     */
    RESTRUCTURED,
    
    /**
     * Kredi erken kapatıldı
     */
    CLOSED_EARLY,
    
    /**
     * Kredi ödemelerinde gecikme var
     */
    DELINQUENT,
    
    /**
     * Kredi temerrüde düştü
     */
    DEFAULT,
    
    /**
     * Kredi tahsil edilemez durumda
     */
    CHARGED_OFF,
    
    /**
     * Kredi takibe alındı
     */
    COLLECTION,
    
    /**
     * Kredi iptal edildi
     */
    CANCELLED,
    
    /**
     * Kredi transferi yapıldı
     */
    TRANSFERRED
} 
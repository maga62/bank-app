package com.banking.entities.enums;

/**
 * Ödeme durumları için enum.
 */
public enum PaymentStatus {
    /**
     * Ödeme işlemi beklemede
     */
    PENDING,
    
    /**
     * Ödeme işlemi yetkilendirildi
     */
    AUTHORIZED,
    
    /**
     * Ödeme işlemi tamamlandı
     */
    COMPLETED,
    
    /**
     * Ödeme işlemi başarısız oldu
     */
    FAILED,
    
    /**
     * Ödeme işlemi iptal edildi
     */
    CANCELLED,
    
    /**
     * Ödeme işlemi iade edildi
     */
    REFUNDED,
    
    /**
     * Ödeme işlemi kısmen iade edildi
     */
    PARTIALLY_REFUNDED, UNKNOWN
} 
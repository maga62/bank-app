package com.banking.entities.enums;

/**
 * Ödeme durumları için enum sınıfı.
 */
public enum PaymentStatus {
    /**
     * Ödeme henüz vadesi gelmemiş
     */
    PENDING,
    
    /**
     * Ödeme işleniyor
     */
    PROCESSING,
    
    /**
     * Ödeme tamamlandı
     */
    COMPLETED,
    
    /**
     * Ödeme başarısız oldu
     */
    FAILED,
    
    /**
     * Ödeme iptal edildi
     */
    CANCELLED,
    
    /**
     * Ödeme iade edildi
     */
    REFUNDED,
    
    /**
     * Ödeme kısmen iade edildi
     */
    PARTIALLY_REFUNDED,
    
    /**
     * Ödeme yetkilendirildi
     */
    AUTHORIZED,
    
    /**
     * Ödeme yakalandı
     */
    CAPTURED,
    
    /**
     * Ödeme süresi doldu
     */
    EXPIRED,
    
    /**
     * Ödeme reddedildi
     */
    DECLINED,
    
    /**
     * Ödeme reddedildi
     */
    REJECTED,
    
    /**
     * Ödeme onay bekliyor
     */
    WAITING_FOR_APPROVAL,
    
    /**
     * 3DS onayı bekliyor
     */
    WAITING_FOR_3DS,
    
    /**
     * Banka onayı bekliyor
     */
    WAITING_FOR_BANK_APPROVAL,
    
    /**
     * Ödeme vadesi geçmiş
     */
    OVERDUE,
    
    /**
     * Bilinmeyen durum
     */
    UNKNOWN
} 
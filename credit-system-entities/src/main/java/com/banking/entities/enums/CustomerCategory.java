package com.banking.entities.enums;

/**
 * Müşteri kategorileri.
 * Kredi skoruna göre müşteriler farklı kategorilere ayrılır.
 */
public enum CustomerCategory {
    /**
     * Yüksek kredi skoruna sahip, düşük riskli müşteriler
     */
    VIP,
    
    /**
     * Orta düzeyde kredi skoruna sahip, normal riskli müşteriler
     */
    STANDARD,
    
    /**
     * Düşük kredi skoruna sahip, yüksek riskli müşteriler
     */
    RISKY
} 
package com.banking.core.security.fraud;

import com.banking.entities.Customer;

import java.math.BigDecimal;

/**
 * Dolandırıcılık tespit kuralı arayüzü.
 * Farklı dolandırıcılık tespit kuralları bu arayüzü uygular.
 */
public interface FraudDetectionRule {
    
    /**
     * Kuralı değerlendirir.
     * 
     * @param customer Müşteri
     * @param amount İşlem tutarı
     * @param ipAddress IP adresi
     * @param userAgent Kullanıcı ajanı
     * @return Kural tetiklendiyse true, değilse false
     */
    boolean evaluate(Customer customer, BigDecimal amount, String ipAddress, String userAgent);
    
    /**
     * Kuralın adını döndürür.
     * 
     * @return Kural adı
     */
    String getName();
    
    /**
     * Kuralın açıklamasını döndürür.
     * 
     * @return Kural açıklaması
     */
    String getDescription();
    
    /**
     * Kuralın risk skorunu döndürür.
     * 
     * @return Risk skoru
     */
    int getRiskScore();
} 
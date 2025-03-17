package com.banking.entities.enums;

/**
 * Ödeme sağlayıcıları için enum.
 */
public enum PaymentProvider {
    /**
     * Stripe ödeme sağlayıcısı
     */
    STRIPE,
    
    /**
     * PayPal ödeme sağlayıcısı
     */
    PAYPAL,
    
    /**
     * iyzico ödeme sağlayıcısı
     */
    IYZICO
} 
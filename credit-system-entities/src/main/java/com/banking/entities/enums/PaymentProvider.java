package com.banking.entities.enums;

/**
 * Ödeme sağlayıcıları için enum sınıfı.
 */
public enum PaymentProvider {
    PAYPAL,
    STRIPE,
    IYZICO,
    MASTERPASS,
    BANK_TRANSFER,
    CREDIT_CARD,
    DEBIT_CARD,
    CASH,
    OTHER
} 
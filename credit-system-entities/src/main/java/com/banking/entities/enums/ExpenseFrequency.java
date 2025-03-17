package com.banking.entities.enums;

/**
 * Gider sıklığını belirten enum.
 */
public enum ExpenseFrequency {
    /**
     * Günlük gider
     */
    DAILY,
    
    /**
     * Haftalık gider
     */
    WEEKLY,
    
    /**
     * İki haftada bir gider
     */
    BI_WEEKLY,
    
    /**
     * Aylık gider
     */
    MONTHLY,
    
    /**
     * Üç ayda bir gider
     */
    QUARTERLY,
    
    /**
     * Altı ayda bir gider
     */
    SEMI_ANNUALLY,
    
    /**
     * Yıllık gider
     */
    ANNUALLY,
    
    /**
     * Tek seferlik gider
     */
    ONE_TIME,
    
    /**
     * Düzensiz gider
     */
    IRREGULAR
} 
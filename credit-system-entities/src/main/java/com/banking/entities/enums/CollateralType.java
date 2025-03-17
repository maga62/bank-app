package com.banking.entities.enums;

/**
 * Teminat türlerini belirten enum.
 */
public enum CollateralType {
    /**
     * Gayrimenkul teminatı (ev, arsa, vb.)
     */
    REAL_ESTATE,
    
    /**
     * Araç teminatı
     */
    VEHICLE,
    
    /**
     * Mevduat teminatı
     */
    DEPOSIT,
    
    /**
     * Menkul kıymet teminatı (hisse senedi, tahvil, vb.)
     */
    SECURITIES,
    
    /**
     * Makine ve ekipman teminatı
     */
    MACHINERY_EQUIPMENT,
    
    /**
     * Kefalet teminatı
     */
    GUARANTOR,
    
    /**
     * Alacak teminatı
     */
    RECEIVABLES,
    
    /**
     * Stok/envanter teminatı
     */
    INVENTORY,
    
    /**
     * Diğer teminat türleri
     */
    OTHER
} 
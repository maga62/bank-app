package com.banking.business.fraud;

import com.banking.business.dtos.request.TransactionMonitorRequest;
import com.banking.business.enums.RiskLevel;

/**
 * Dolandırıcılık tespit kuralları için arayüz.
 * Her kural, belirli bir şüpheli işlem desenini tespit etmek için kullanılır.
 */
public interface FraudDetectionRule {
    
    /**
     * Kuralın belirli bir işlem için uygulanabilir olup olmadığını kontrol eder.
     * 
     * @param request İşlem izleme isteği
     * @return Kural uygulanabilirse true, değilse false
     */
    boolean isApplicable(TransactionMonitorRequest request);
    
    /**
     * İşlemin risk seviyesini değerlendirir.
     * 
     * @param request İşlem izleme isteği
     * @return Risk seviyesi
     */
    RiskLevel evaluateRisk(TransactionMonitorRequest request);
    
    /**
     * Risk nedeni açıklamasını döndürür.
     * 
     * @return Risk nedeni açıklaması
     */
    String getRiskReason();
} 
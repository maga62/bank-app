package com.banking.business.abstracts;


import com.banking.business.dtos.request.TransactionMonitorRequest;
import com.banking.business.dtos.response.RiskAssessmentResponse;
import com.banking.business.dtos.response.SuspiciousTransactionResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Anti-Money Laundering (AML) servisi arayüzü.
 * Şüpheli işlemlerin tespiti, risk değerlendirmesi ve raporlama işlemlerini içerir.
 */
public interface AMLService {
    
    /**
     * İşlemi gerçek zamanlı olarak izler ve şüpheli aktivite olup olmadığını kontrol eder.
     * 
     * @param request İzleme isteği
     * @return Risk değerlendirme sonucu
     */
    RiskAssessmentResponse monitorTransaction(TransactionMonitorRequest request);
    
    /**
     * Müşteri için risk değerlendirmesi yapar.
     * 
     * @param customerId Müşteri ID
     * @return Risk değerlendirme sonucu
     */
    RiskAssessmentResponse assessCustomerRisk(Long customerId);
    
    /**
     * Belirli bir müşteriye ait şüpheli işlemleri getirir.
     * 
     * @param customerId Müşteri ID
     * @return Şüpheli işlemler listesi
     */
    List<SuspiciousTransactionResponse> getSuspiciousTransactionsByCustomerId(Long customerId);
    
    /**
     * Belirli bir risk seviyesine sahip tüm şüpheli işlemleri getirir.
     * 
     * @param riskLevel Risk seviyesi
     * @return Şüpheli işlemler listesi
     */
    List<SuspiciousTransactionResponse> getSuspiciousTransactionsByRiskLevel(String riskLevel);
    
    /**
     * Belirli bir eşik değerinin üzerindeki işlemleri getirir.
     * 
     * @param threshold Eşik değeri
     * @return Eşik değerinin üzerindeki işlemler
     */
    List<SuspiciousTransactionResponse> getTransactionsAboveThreshold(BigDecimal threshold);
    
    /**
     * Şüpheli işlemi raporlar.
     * 
     * @param transactionId İşlem ID
     * @param reportNotes Rapor notları
     * @return Raporlama başarılıysa true, değilse false
     */
    boolean reportSuspiciousTransaction(Long transactionId, String reportNotes);
    
    /**
     * Şüpheli işlemi çözümler.
     * 
     * @param transactionId İşlem ID
     * @param resolutionNotes Çözüm notları
     * @param isFalsePositive Yanlış pozitif mi?
     * @return Çözümleme başarılıysa true, değilse false
     */
    boolean resolveSuspiciousTransaction(Long transactionId, String resolutionNotes, boolean isFalsePositive);
    
    /**
     * Makine öğrenmesi modelini eğitir.
     * 
     * @return Eğitim başarılıysa true, değilse false
     */
    boolean trainMachineLearningModel();
} 
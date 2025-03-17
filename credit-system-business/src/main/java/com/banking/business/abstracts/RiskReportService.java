package com.banking.business.abstracts;

import com.banking.core.dtos.response.*;
import com.banking.core.utilities.results.DataResult;
import com.banking.entities.RiskReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Merkez Bankası API'sinden risk raporları almak için servis arayüzü.
 * Kredi geçmişi, kara liste durumu ve ödeme gecikmeleri gibi bilgileri sağlar.
 */
public interface RiskReportService {
    
    /**
     * Müşteri için Merkez Bankası'ndan tam risk raporu alır.
     * 
     * @param customerId Müşteri ID
     * @param identityNumber Kimlik numarası
     * @return Merkez Bankası raporu
     */
    CentralBankReportResponse getCentralBankReport(Long customerId, String identityNumber);
    
    /**
     * Müşterinin kredi geçmişi özetini getirir.
     * 
     * @param customerId Müşteri ID
     * @return Kredi geçmişi özeti
     */
    CreditHistorySummaryResponse getCreditHistorySummary(Long customerId);
    
    /**
     * Müşterinin kara liste durumunu kontrol eder.
     * 
     * @param customerId Müşteri ID
     * @return Kara liste durumu
     */
    BlacklistStatusResponse getBlacklistStatus(Long customerId);
    
    /**
     * Müşterinin ödeme gecikmelerini getirir.
     * 
     * @param customerId Müşteri ID
     * @return Ödeme gecikmeleri listesi
     */
    List<PaymentDelayResponse> getPaymentDelays(Long customerId);
    
    /**
     * Müşterinin risk skorunu hesaplar.
     * 
     * @param customerId Müşteri ID
     * @return Risk skoru
     */
    RiskScoreResponse calculateRiskScore(Long customerId);
    
    /**
     * Merkez Bankası'ndan alınan verileri önbelleğe alır.
     * 
     * @param customerId Müşteri ID
     * @param report Merkez Bankası raporu
     * @return Önbelleğe alma başarılıysa true, değilse false
     */
    boolean cacheReportData(Long customerId, CentralBankReportResponse report);
    
    /**
     * Önbellekteki rapor verisini getirir.
     * 
     * @param customerId Müşteri ID
     * @return Önbellekteki rapor, yoksa boş Optional
     */
    Optional<CentralBankReportResponse> getCachedReportData(Long customerId);
    
    /**
     * Önbellekteki verilerin geçerlilik süresini kontrol eder.
     * 
     * @param customerId Müşteri ID
     * @return Veriler geçerliyse true, değilse false
     */
    boolean isCachedDataValid(Long customerId);
    
    /**
     * Önbellekteki verileri temizler.
     * 
     * @param customerId Müşteri ID
     * @return Temizleme başarılıysa true, değilse false
     */
    boolean clearCachedData(Long customerId);

    DataResult<RiskReport> getReport(Long customerId);
    DataResult<Page<RiskReport>> getHistoricalReports(Long customerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    DataResult<List<RiskReport>> getHighRiskCustomers(Double riskScoreThreshold);
    DataResult<List<RiskReport>> getBlacklistedCustomers();
} 
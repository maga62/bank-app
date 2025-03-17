package com.banking.business.services.analysis;

import com.banking.entities.CreditApplication;
import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.entities.enums.CreditType;
import com.banking.repositories.abstracts.CreditApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kredi risk analizi servisi.
 * Bankanın finansal durumu ve mevcut kredi riskini hesaplar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditRiskAnalysisService {

    private final CreditApplicationRepository creditApplicationRepository;
    
    /**
     * Bankanın genel kredi risk analizini yapar
     * 
     * @return Risk analiz sonuçları
     */
    public Map<String, Object> analyzeBankCreditRisk() {
        log.info("Analyzing bank credit risk");
        
        Map<String, Object> results = new HashMap<>();
        
        // Toplam aktif kredi miktarı
        BigDecimal totalActiveCredit = calculateTotalActiveCredit();
        results.put("totalActiveCredit", totalActiveCredit);
        
        // Kredi tiplerine göre dağılım
        Map<CreditType, BigDecimal> distributionByCreditType = calculateDistributionByCreditType();
        results.put("distributionByCreditType", distributionByCreditType);
        
        // Ortalama kredi miktarı
        BigDecimal averageCreditAmount = calculateAverageCreditAmount();
        results.put("averageCreditAmount", averageCreditAmount);
        
        // Son 30 gündeki kredi başvuruları
        int applicationsLast30Days = calculateApplicationsInLastDays(30);
        results.put("applicationsLast30Days", applicationsLast30Days);
        
        // Son 30 gündeki onaylanan kredi başvuruları
        int approvedApplicationsLast30Days = calculateApprovedApplicationsInLastDays(30);
        results.put("approvedApplicationsLast30Days", approvedApplicationsLast30Days);
        
        // Onay oranı
        BigDecimal approvalRate = calculateApprovalRate(30);
        results.put("approvalRate", approvalRate);
        
        // Risk skoru (0-100 arası, 100 en riskli)
        int riskScore = calculateRiskScore(totalActiveCredit, approvalRate, distributionByCreditType);
        results.put("riskScore", riskScore);
        
        log.info("Bank credit risk analysis completed. Risk score: {}", riskScore);
        
        return results;
    }
    
    /**
     * Belirli bir kredi başvurusunun risk analizini yapar
     * 
     * @param application Kredi başvurusu
     * @return Risk analiz sonuçları
     */
    public Map<String, Object> analyzeApplicationRisk(CreditApplication application) {
        log.info("Analyzing risk for credit application: {}", application.getId());
        
        Map<String, Object> results = new HashMap<>();
        
        // Başvuru miktarının ortalama kredi miktarına oranı
        BigDecimal averageCreditAmount = calculateAverageCreditAmount();
        BigDecimal amountToAverageRatio = application.getAmount().divide(averageCreditAmount, 2, RoundingMode.HALF_UP);
        results.put("amountToAverageRatio", amountToAverageRatio);
        
        // Kredi tipine göre risk faktörü
        double creditTypeRiskFactor = getCreditTypeRiskFactor(application.getCreditType());
        results.put("creditTypeRiskFactor", creditTypeRiskFactor);
        
        // Vade uzunluğu risk faktörü (uzun vadeler daha riskli)
        double termRiskFactor = getTermRiskFactor(application.getTermMonths());
        results.put("termRiskFactor", termRiskFactor);
        
        // Gelir-kredi oranı (düşük oran daha riskli)
        BigDecimal incomeToCreditRatio = application.getMonthlyIncome()
                .multiply(new BigDecimal(application.getTermMonths()))
                .divide(application.getAmount(), 2, RoundingMode.HALF_UP);
        results.put("incomeToCreditRatio", incomeToCreditRatio);
        
        // Toplam risk skoru (0-100 arası, 100 en riskli)
        int applicationRiskScore = calculateApplicationRiskScore(
                amountToAverageRatio, 
                creditTypeRiskFactor, 
                termRiskFactor, 
                incomeToCreditRatio);
        results.put("applicationRiskScore", applicationRiskScore);
        
        log.info("Credit application risk analysis completed. Risk score: {}", applicationRiskScore);
        
        return results;
    }
    
    /**
     * Toplam aktif kredi miktarını hesaplar
     */
    private BigDecimal calculateTotalActiveCredit() {
        List<CreditApplication> activeApplications = creditApplicationRepository.findAll().stream()
                .filter(app -> convertStatus(app.getStatus()) == CreditApplicationStatus.APPROVED && app.getDeletedDate() == null)
                .collect(Collectors.toList());
        
        return activeApplications.stream()
                .map(CreditApplication::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Kredi tiplerine göre dağılımı hesaplar
     */
    private Map<CreditType, BigDecimal> calculateDistributionByCreditType() {
        List<CreditApplication> activeApplications = creditApplicationRepository.findAll().stream()
                .filter(app -> convertStatus(app.getStatus()) == CreditApplicationStatus.APPROVED && app.getDeletedDate() == null)
                .collect(Collectors.toList());
        
        return activeApplications.stream()
                .collect(Collectors.groupingBy(
                        CreditApplication::getCreditType,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                CreditApplication::getAmount,
                                BigDecimal::add)));
    }
    
    /**
     * Ortalama kredi miktarını hesaplar
     */
    private BigDecimal calculateAverageCreditAmount() {
        List<CreditApplication> activeApplications = creditApplicationRepository.findAll().stream()
                .filter(app -> convertStatus(app.getStatus()) == CreditApplicationStatus.APPROVED && app.getDeletedDate() == null)
                .collect(Collectors.toList());
        
        if (activeApplications.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalAmount = activeApplications.stream()
                .map(CreditApplication::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalAmount.divide(new BigDecimal(activeApplications.size()), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Son n gündeki kredi başvurularını hesaplar
     */
    private int calculateApplicationsInLastDays(int days) {
        LocalDateTime startDate = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
        
        return (int) creditApplicationRepository.findAll().stream()
                .filter(app -> app.getCreatedDate() != null && app.getCreatedDate().isAfter(startDate) && app.getDeletedDate() == null)
                .count();
    }
    
    /**
     * Son n gündeki onaylanan kredi başvurularını hesaplar
     */
    private int calculateApprovedApplicationsInLastDays(int days) {
        LocalDateTime startDate = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
        
        return (int) creditApplicationRepository.findAll().stream()
                .filter(app -> convertStatus(app.getStatus()) == CreditApplicationStatus.APPROVED 
                        && app.getApprovalDate() != null 
                        && app.getApprovalDate().isAfter(startDate)
                        && app.getDeletedDate() == null)
                .count();
    }
    
    /**
     * Son n gündeki onay oranını hesaplar
     */
    private BigDecimal calculateApprovalRate(int days) {
        int totalApplications = calculateApplicationsInLastDays(days);
        if (totalApplications == 0) {
            return BigDecimal.ZERO;
        }
        
        int approvedApplications = calculateApprovedApplicationsInLastDays(days);
        return new BigDecimal(approvedApplications)
                .divide(new BigDecimal(totalApplications), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)); // Yüzde olarak
    }
    
    /**
     * Banka risk skorunu hesaplar
     */
    private int calculateRiskScore(
            BigDecimal totalActiveCredit, 
            BigDecimal approvalRate, 
            Map<CreditType, BigDecimal> distributionByCreditType) {
        
        // Basit bir risk skoru hesaplama algoritması
        // Gerçek uygulamada daha karmaşık faktörler ve ağırlıklar kullanılır
        
        // Toplam kredi miktarı faktörü (yüksek miktar daha riskli)
        double totalCreditFactor = Math.min(totalActiveCredit.doubleValue() / 10000000.0, 1.0) * 40;
        
        // Onay oranı faktörü (yüksek onay oranı daha riskli)
        double approvalRateFactor = (approvalRate.doubleValue() / 100.0) * 30;
        
        // Kredi tipi dağılımı faktörü (riskli kredi tiplerinin yüksek oranı daha riskli)
        double creditTypeDistributionFactor = calculateCreditTypeDistributionRisk(distributionByCreditType, totalActiveCredit) * 30;
        
        // Toplam risk skoru
        return (int) Math.min(totalCreditFactor + approvalRateFactor + creditTypeDistributionFactor, 100);
    }
    
    /**
     * Kredi tipi dağılımı risk faktörünü hesaplar
     */
    private double calculateCreditTypeDistributionRisk(
            Map<CreditType, BigDecimal> distribution, 
            BigDecimal totalAmount) {
        
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        // Her kredi tipinin risk faktörü
        double totalRisk = 0.0;
        for (Map.Entry<CreditType, BigDecimal> entry : distribution.entrySet()) {
            double typeRiskFactor = getCreditTypeRiskFactor(entry.getKey());
            double typePercentage = entry.getValue().divide(totalAmount, 4, RoundingMode.HALF_UP).doubleValue();
            totalRisk += typeRiskFactor * typePercentage;
        }
        
        return totalRisk;
    }
    
    /**
     * Kredi başvurusu risk skorunu hesaplar
     */
    private int calculateApplicationRiskScore(
            BigDecimal amountToAverageRatio,
            double creditTypeRiskFactor,
            double termRiskFactor,
            BigDecimal incomeToCreditRatio) {
        
        // Miktar faktörü (ortalamadan yüksek miktarlar daha riskli)
        double amountFactor = Math.min(amountToAverageRatio.doubleValue(), 3.0) / 3.0 * 30;
        
        // Kredi tipi risk faktörü
        double typeFactor = creditTypeRiskFactor * 25;
        
        // Vade risk faktörü
        double termFactor = termRiskFactor * 20;
        
        // Gelir-kredi oranı faktörü (düşük oran daha riskli)
        double incomeRatioFactor = Math.max(0, 1.0 - incomeToCreditRatio.doubleValue()) * 25;
        
        // Toplam risk skoru
        return (int) Math.min(amountFactor + typeFactor + termFactor + incomeRatioFactor, 100);
    }
    
    /**
     * Kredi tipine göre risk faktörünü belirler
     * 0.0 (en düşük risk) ile 1.0 (en yüksek risk) arasında
     */
    private double getCreditTypeRiskFactor(CreditType creditType) {
        switch (creditType) {
            case MORTGAGE:
                return 0.3; // Düşük risk (teminatlı)
            case AUTO_LOAN:
                return 0.5; // Orta risk (teminatlı ama değer kaybı hızlı)
            case PERSONAL_FINANCE:
                return 0.8; // Yüksek risk (teminatsız)
            case BUSINESS_LOAN:
                return 0.6; // Orta-yüksek risk
            case EDUCATION_LOAN:
                return 0.4; // Orta-düşük risk
            case COMMERCIAL_MORTGAGE:
                return 0.3; // Düşük risk (teminatlı)
            case EQUIPMENT_FINANCING:
                return 0.5; // Orta risk
            case WORKING_CAPITAL:
                return 0.7; // Orta-yüksek risk
            default:
                return 0.7; // Varsayılan risk
        }
    }
    
    /**
     * Vade uzunluğuna göre risk faktörünü belirler
     * 0.0 (en düşük risk) ile 1.0 (en yüksek risk) arasında
     */
    private double getTermRiskFactor(int termMonths) {
        if (termMonths <= 12) {
            return 0.2; // Kısa vade, düşük risk
        } else if (termMonths <= 36) {
            return 0.4; // Orta vade, orta risk
        } else if (termMonths <= 60) {
            return 0.6; // Orta-uzun vade, orta-yüksek risk
        } else if (termMonths <= 120) {
            return 0.8; // Uzun vade, yüksek risk
        } else {
            return 1.0; // Çok uzun vade, çok yüksek risk
        }
    }

    private CreditApplicationStatus convertStatus(CreditApplication.Status status) {
        return CreditApplicationStatus.valueOf(status.name());
    }
} 
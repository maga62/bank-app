package com.banking.business.services.refinance;

import com.banking.entities.CreditApplication;
import com.banking.entities.Customer;
import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.entities.enums.CustomerCategory;
import com.banking.repositories.abstracts.CreditApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kredi yenileme servisi.
 * Mevcut kredilerin yenilenmesi ve güncel faiz oranlarıyla güncellenmesini sağlar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditRefinanceService {

    private final CreditApplicationRepository creditApplicationRepository;
    
    /**
     * Müşterinin mevcut kredisini yeniler
     * 
     * @param existingApplicationId Mevcut kredi başvurusu ID
     * @param customer Müşteri bilgileri
     * @param customerCategory Müşteri kategorisi
     * @param newInterestRate Yeni faiz oranı
     * @param extendTermMonths Ek süre (ay cinsinden, opsiyonel)
     * @return Yenilenen kredi başvurusu
     */
    public CreditApplication refinanceCredit(
            Long existingApplicationId, 
            Customer customer, 
            CustomerCategory customerCategory,
            BigDecimal newInterestRate,
            Integer extendTermMonths) {
        
        // Mevcut kredi başvurusunu bul
        CreditApplication existingApplication = creditApplicationRepository.findById(existingApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found: " + existingApplicationId));
        
        // Kredi başvurusunun onaylanmış olduğunu kontrol et
        if (existingApplication.getStatus() != CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name())) {
            throw new IllegalStateException("Only approved credit applications can be refinanced");
        }
        
        // Müşteri bilgilerini kontrol et
        if (!existingApplication.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Credit application does not belong to this customer");
        }
        
        log.info("Refinancing credit application: {} for customer: {}", 
                existingApplicationId, customer.getCustomerNumber());
        
        // Yeni kredi başvurusu oluştur
        CreditApplication refinancedApplication = new CreditApplication();
        refinancedApplication.setCustomer(customer);
        refinancedApplication.setCreditType(existingApplication.getCreditType());
        refinancedApplication.setAmount(existingApplication.getAmount());
        refinancedApplication.setMonthlyIncome(existingApplication.getMonthlyIncome());
        refinancedApplication.setNotes("Refinanced from application ID: " + existingApplicationId);
        
        // Ek süre ekle (varsa)
        int newTermMonths = existingApplication.getTermMonths();
        if (extendTermMonths != null && extendTermMonths > 0) {
            newTermMonths += extendTermMonths;
        }
        refinancedApplication.setTermMonths(newTermMonths);
        
        // Müşteri kategorisine göre onay durumunu belirle
        setApprovalStatusByCategory(refinancedApplication, customerCategory);
        
        // Yeni başvuruyu kaydet
        CreditApplication savedApplication = creditApplicationRepository.save(refinancedApplication);
        
        // Eski başvuruyu güncelle (refinance edildi olarak işaretle)
        existingApplication.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.CANCELLED.name()));
        existingApplication.setNotes("Refinanced to application ID: " + savedApplication.getId());
        creditApplicationRepository.save(existingApplication);
        
        log.info("Credit application successfully refinanced. Old ID: {}, New ID: {}", 
                existingApplicationId, savedApplication.getId());
        
        return savedApplication;
    }
    
    /**
     * Müşterinin yenileme için uygun kredilerini bulur
     * 
     * @param customerId Müşteri ID
     * @return Yenileme için uygun kredi başvuruları listesi
     */
    public List<CreditApplication> findRefinanceEligibleCredits(Long customerId) {
        // Müşterinin onaylanmış kredilerini bul
        return creditApplicationRepository.findAllByCustomerIdAndStatus(
                customerId, CreditApplicationStatus.APPROVED);
    }
    
    /**
     * Kredi yenileme tekliflerini hesaplar
     * 
     * @param application Mevcut kredi başvurusu
     * @param currentMarketRate Güncel piyasa faiz oranı
     * @param customerCategory Müşteri kategorisi
     * @return Hesaplanan yeni faiz oranı
     */
    public BigDecimal calculateRefinanceOffer(
            CreditApplication application, 
            BigDecimal currentMarketRate,
            CustomerCategory customerCategory) {
        
        // Müşteri kategorisine göre indirim oranı belirle
        BigDecimal discountRate;
        switch (customerCategory) {
            case VIP:
                discountRate = new BigDecimal("0.20"); // %20 indirim
                break;
            case STANDARD:
                discountRate = new BigDecimal("0.10"); // %10 indirim
                break;
            case RISKY:
                discountRate = new BigDecimal("0.05"); // %5 indirim
                break;
            default:
                discountRate = BigDecimal.ZERO;
        }
        
        // İndirimli faiz oranını hesapla
        BigDecimal discountAmount = currentMarketRate.multiply(discountRate);
        BigDecimal newRate = currentMarketRate.subtract(discountAmount);
        
        // En az 2 basamak hassasiyetle yuvarla
        return newRate.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Müşteri kategorisine göre onay durumunu belirler
     */
    private void setApprovalStatusByCategory(CreditApplication application, CustomerCategory customerCategory) {
        switch (customerCategory) {
            case VIP:
                // VIP müşteriler için otomatik onay
                application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name()));
                application.setApprovalDate(LocalDateTime.now());
                break;
            case STANDARD:
                // Standart müşteriler için hızlı onay
                application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name()));
                break;
            case RISKY:
                // Riskli müşteriler için manuel değerlendirme
                application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.IN_REVIEW.name()));
                break;
            default:
                application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING.name()));
        }
    }
} 
package com.banking.business.services.approval;

import com.banking.entities.CreditApplication;
import com.banking.entities.Customer;
import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.entities.enums.CustomerCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Kredi onay servisi.
 * Müşteri kategorilerine göre farklı onay mekanizmaları uygular.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditApprovalService {

    /**
     * Kredi başvurusunu değerlendirir ve onay durumunu günceller
     * 
     * @param application Kredi başvurusu
     * @param customer Müşteri bilgileri
     * @param creditScore Hesaplanan kredi skoru
     * @param customerCategory Müşteri kategorisi
     * @return Güncellenmiş kredi başvurusu
     */
    public CreditApplication evaluateApplication(
            CreditApplication application, 
            Customer customer, 
            int creditScore, 
            CustomerCategory customerCategory) {
        
        log.info("Evaluating credit application for customer: {}, category: {}, score: {}", 
                customer.getCustomerNumber(), customerCategory, creditScore);
        
        // Müşteri kategorisine göre farklı onay stratejileri uygula
        switch (customerCategory) {
            case VIP:
                return applyVipApprovalStrategy(application, creditScore);
            case STANDARD:
                return applyStandardApprovalStrategy(application, creditScore);
            case RISKY:
                return applyRiskyApprovalStrategy(application, creditScore);
            default:
                throw new IllegalArgumentException("Unknown customer category: " + customerCategory);
        }
    }
    
    /**
     * VIP müşteriler için onay stratejisi
     * - Yüksek kredi limitleri
     * - Hızlı onay süreci
     * - Düşük faiz oranları
     */
    private CreditApplication applyVipApprovalStrategy(CreditApplication application, int creditScore) {
        // VIP müşteriler için kredi tutarı kontrolü
        BigDecimal requestedAmount = application.getAmount();
        
        // VIP müşteriler için yüksek limitler (örn: 1,000,000 TL'ye kadar)
        if (requestedAmount.compareTo(new BigDecimal("1000000")) > 0) {
            // Çok yüksek tutarlar için manuel onay gerekebilir
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name()));
            log.info("VIP application requires manual approval due to high amount: {}", requestedAmount);
        } else {
            // Otomatik onay
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name()));
            application.setApprovalDate(LocalDateTime.now());
            log.info("VIP application automatically approved: {}", application.getId());
        }
        
        return application;
    }
    
    /**
     * Standart müşteriler için onay stratejisi
     * - Orta düzey kredi limitleri
     * - Normal onay süreci
     * - Standart faiz oranları
     */
    private CreditApplication applyStandardApprovalStrategy(CreditApplication application, int creditScore) {
        BigDecimal requestedAmount = application.getAmount();
        
        // Standart müşteriler için orta düzey limitler (örn: 500,000 TL'ye kadar)
        if (requestedAmount.compareTo(new BigDecimal("500000")) > 0) {
            // Yüksek tutarlar için manuel onay
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name()));
            log.info("Standard application requires manual approval due to high amount: {}", requestedAmount);
        } else if (creditScore < 650) {
            // Sınırda kredi skoru için ek kontrol
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name()));
            log.info("Standard application requires manual approval due to borderline credit score: {}", creditScore);
        } else {
            // Otomatik onay
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name()));
            application.setApprovalDate(LocalDateTime.now());
            log.info("Standard application automatically approved: {}", application.getId());
        }
        
        return application;
    }
    
    /**
     * Riskli müşteriler için onay stratejisi
     * - Düşük kredi limitleri
     * - Detaylı onay süreci
     * - Yüksek faiz oranları veya ek teminat gereksinimleri
     */
    private CreditApplication applyRiskyApprovalStrategy(CreditApplication application, int creditScore) {
        BigDecimal requestedAmount = application.getAmount();
        
        // Riskli müşteriler için düşük limitler (örn: 100,000 TL'ye kadar)
        if (requestedAmount.compareTo(new BigDecimal("100000")) > 0) {
            // Yüksek tutarlar için otomatik red
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.REJECTED.name()));
            application.setRejectionDate(LocalDateTime.now());
            log.info("Risky application automatically rejected due to high amount: {}", requestedAmount);
        } else if (creditScore < 500) {
            // Çok düşük kredi skoru için otomatik red
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.REJECTED.name()));
            application.setRejectionDate(LocalDateTime.now());
            log.info("Risky application automatically rejected due to very low credit score: {}", creditScore);
        } else {
            // Manuel değerlendirme
            application.setStatus(CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name()));
            log.info("Risky application requires manual approval: {}", application.getId());
        }
        
        return application;
    }
} 
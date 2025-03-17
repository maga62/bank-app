package com.banking.business.services.refinancing;

import com.banking.core.events.EventService;
import com.banking.core.notifications.NotificationService;
import com.banking.entities.CreditHistory;
import com.banking.entities.Customer;
import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.CustomerCategory;
import com.banking.repositories.abstracts.CreditHistoryRepository;
import com.banking.repositories.abstracts.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Otomatik kredi refinansman servisi.
 * Müşterilerin mevcut kredilerini daha uygun koşullarla yeniden yapılandırmak için kullanılır.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditRefinancingService {

    private final CreditHistoryRepository creditHistoryRepository;
    private final CustomerRepository<Customer> customerRepository;
    private final NotificationService notificationService;
    private final EventService eventService;
    
    @Value("${refinancing.enabled:true}")
    private boolean refinancingEnabled;
    
    @Value("${refinancing.min.interest.rate.difference:0.5}")
    private double minInterestRateDifference;
    
    @Value("${refinancing.min.remaining.amount:10000}")
    private double minRemainingAmount;
    
    @Value("${refinancing.min.remaining.term:6}")
    private int minRemainingTerm;
    
    /**
     * Belirli bir müşteri için refinansman fırsatlarını kontrol eder.
     * 
     * @param customerId Müşteri ID
     * @return Refinansman fırsatları listesi
     */
    public List<RefinancingOpportunity> checkRefinancingOpportunities(Long customerId) {
        if (!refinancingEnabled) {
            log.info("Refinancing is disabled. No opportunities checked for customer: {}", customerId);
            return List.of();
        }
        
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
        
        Customer customer = customerOptional.get();
        
        List<CreditHistory> activeCredits = creditHistoryRepository.findByCustomerIdAndStatus(customerId, CreditHistoryStatus.ACTIVE);
        
        return activeCredits.stream()
                .filter(this::isEligibleForRefinancing)
                .map(credit -> calculateRefinancingOffer(credit, customer))
                .filter(opportunity -> opportunity.getInterestRateReduction() >= minInterestRateDifference)
                .collect(Collectors.toList());
    }
    
    /**
     * Belirli bir kredi için refinansman uygulanabilir mi kontrol eder.
     * 
     * @param credit Kredi
     * @return Refinansman uygulanabilir ise true, değilse false
     */
    private boolean isEligibleForRefinancing(CreditHistory credit) {
        // Kalan tutar minimum tutardan büyük olmalı
        if (credit.getOutstandingAmount().compareTo(BigDecimal.valueOf(minRemainingAmount)) < 0) {
            return false;
        }
        
        // Kalan vade minimum vadeden büyük olmalı
        LocalDate today = LocalDate.now();
        if (credit.getEndDate() == null || credit.getEndDate().isBefore(today.plusMonths(minRemainingTerm))) {
            return false;
        }
        
        // Kredi aktif olmalı
        if (credit.getStatus() != CreditHistoryStatus.ACTIVE) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Belirli bir kredi için refinansman teklifi hesaplar.
     * 
     * @param credit Kredi
     * @param customer Müşteri
     * @return Refinansman fırsatı
     */
    private RefinancingOpportunity calculateRefinancingOffer(CreditHistory credit, Customer customer) {
        // Mevcut faiz oranı
        BigDecimal currentRate = credit.getInterestRate();
        
        // Müşteri kategorisine göre yeni faiz oranı hesapla
        BigDecimal newRate = calculateNewInterestRate(currentRate, customer);
        
        // Faiz oranı indirimi
        BigDecimal rateReduction = currentRate.subtract(newRate);
        
        // Kalan tutar
        BigDecimal remainingAmount = credit.getOutstandingAmount();
        
        // Kalan vade (ay cinsinden)
        LocalDate today = LocalDate.now();
        int remainingMonths = (credit.getEndDate().getYear() - today.getYear()) * 12 + 
                              (credit.getEndDate().getMonthValue() - today.getMonthValue());
        
        // Mevcut aylık ödeme
        BigDecimal currentMonthlyPayment = credit.getMonthlyPayment();
        
        // Yeni aylık ödeme
        BigDecimal newMonthlyPayment = calculateMonthlyPayment(remainingAmount, newRate, remainingMonths);
        
        // Aylık tasarruf
        BigDecimal monthlySavings = currentMonthlyPayment.subtract(newMonthlyPayment);
        
        // Toplam tasarruf
        BigDecimal totalSavings = monthlySavings.multiply(BigDecimal.valueOf(remainingMonths));
        
        return RefinancingOpportunity.builder()
                .creditId(credit.getId())
                .customerId(customer.getId())
                .creditType(credit.getCreditType())
                .currentInterestRate(currentRate)
                .newInterestRate(newRate)
                .interestRateReduction(rateReduction.doubleValue())
                .remainingAmount(remainingAmount)
                .remainingTerm(remainingMonths)
                .currentMonthlyPayment(currentMonthlyPayment)
                .newMonthlyPayment(newMonthlyPayment)
                .monthlySavings(monthlySavings)
                .totalSavings(totalSavings)
                .build();
    }
    
    /**
     * Müşteri kategorisine göre yeni faiz oranı hesaplar.
     * 
     * @param currentRate Mevcut faiz oranı
     * @param customer Müşteri
     * @return Yeni faiz oranı
     */
    private BigDecimal calculateNewInterestRate(BigDecimal currentRate, Customer customer) {
        CustomerCategory category = customer.getCustomerCategory();
        
        // Müşteri kategorisine göre indirim oranı
        double discountRate;
        switch (category) {
            case VIP:
                discountRate = 0.25; // %25 indirim
                break;
            case STANDARD:
                discountRate = 0.15; // %15 indirim
                break;
            case RISKY:
                discountRate = 0.05; // %5 indirim
                break;
            default:
                discountRate = 0.10; // %10 indirim
                break;
        }
        
        // Yeni faiz oranı hesapla
        BigDecimal discount = currentRate.multiply(BigDecimal.valueOf(discountRate));
        return currentRate.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Aylık ödeme tutarını hesaplar.
     * 
     * @param principal Anapara
     * @param annualInterestRate Yıllık faiz oranı
     * @param termMonths Vade (ay cinsinden)
     * @return Aylık ödeme tutarı
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal annualInterestRate, int termMonths) {
        // Aylık faiz oranı
        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12 * 100), RoundingMode.HALF_UP).setScale(10, RoundingMode.HALF_UP);
        
        // Aylık ödeme formülü: P * r * (1 + r)^n / ((1 + r)^n - 1)
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal powFactor = onePlusRate.pow(termMonths);
        
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(powFactor);
        BigDecimal denominator = powFactor.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Refinansman teklifini uygular.
     * 
     * @param opportunityId Refinansman fırsatı ID
     * @param customerId Müşteri ID
     * @return İşlem başarılı ise true, değilse false
     */
    @Transactional
    public boolean applyRefinancing(Long opportunityId, Long customerId) {
        if (!refinancingEnabled) {
            log.info("Refinancing is disabled. Cannot apply refinancing for customer: {}", customerId);
            return false;
        }
        
        // Gerçek uygulamada burada refinansman fırsatı veritabanından alınır
        // Şimdilik basit bir kontrol yapıyoruz
        
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
        
        Customer customer = customerOptional.get();
        
        // Refinansman işlemi gerçekleştir
        log.info("Applying refinancing for customer: {}, opportunity: {}", customerId, opportunityId);
        
        // Olayı yayınla
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("opportunityId", opportunityId);
        eventData.put("customerId", customerId);
        eventData.put("timestamp", LocalDate.now());
        
        eventService.publishSystemEvent("REFINANCING_APPLIED", "CreditRefinancingService", eventData);
        
        // Bildirim gönder
        notificationService.sendEmail(
                customer.getEmail(),
                "Kredi Refinansman İşleminiz Onaylandı",
                "Sayın " + customer.getFirstName() + " " + customer.getLastName() + ", kredi refinansman işleminiz başarıyla gerçekleştirilmiştir.",
                Map.of("customerName", customer.getFirstName() + " " + customer.getLastName())
        );
        
        return true;
    }
    
    /**
     * Periyodik olarak tüm müşteriler için refinansman fırsatlarını kontrol eder.
     */
    @Scheduled(cron = "0 0 1 * * ?") // Her gün saat 01:00'de çalışır
    public void checkRefinancingOpportunitiesForAllCustomers() {
        if (!refinancingEnabled) {
            log.info("Refinancing is disabled. Skipping scheduled check.");
            return;
        }
        
        log.info("Starting scheduled refinancing opportunity check for all customers");
        
        // Pageable kullanarak tüm müşterileri getir
        Page<Customer> customerPage = customerRepository.findAll(PageRequest.of(0, 1000));
        List<Customer> eligibleCustomers = customerPage.getContent().stream()
                .filter(customer -> customer.getCustomerCategory() != CustomerCategory.RISKY)
                .collect(Collectors.toList());
        
        int totalOpportunities = 0;
        
        for (Customer customer : eligibleCustomers) {
            try {
                List<RefinancingOpportunity> opportunities = checkRefinancingOpportunities(customer.getId());
                totalOpportunities += opportunities.size();
                
                if (!opportunities.isEmpty()) {
                    // Müşteriye bildirim gönder
                    notificationService.sendEmail(
                            customer.getEmail(),
                            "Size Özel Kredi Refinansman Fırsatları",
                            "Sayın " + customer.getFirstName() + " " + customer.getLastName() + ", size özel " + 
                                    opportunities.size() + " adet kredi refinansman fırsatı bulunmaktadır.",
                            Map.of(
                                    "customerName", customer.getFirstName() + " " + customer.getLastName(),
                                    "opportunityCount", opportunities.size()
                            )
                    );
                }
            } catch (Exception e) {
                log.error("Error checking refinancing opportunities for customer: {}", customer.getId(), e);
            }
        }
        
        log.info("Completed scheduled refinancing opportunity check. Found {} opportunities for {} customers", 
                totalOpportunities, eligibleCustomers.size());
    }
} 
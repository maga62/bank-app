package com.banking.core.security.fraud;

import com.banking.core.events.EventService;
import com.banking.entities.Customer;
import com.banking.entities.SuspiciousTransaction;
import com.banking.entities.enums.RiskLevel;
import com.banking.entities.enums.TransactionStatus;
import com.banking.entities.enums.TransactionType;
import com.banking.repositories.abstracts.CustomerRepository;
import com.banking.repositories.abstracts.SuspiciousTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dolandırıcılık tespit servisi.
 * İşlem desenlerini analiz ederek şüpheli aktiviteleri tespit eder.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final SuspiciousTransactionRepository suspiciousTransactionRepository;
    private final CustomerRepository customerRepository;
    private final EventService eventService;
    private final List<FraudDetectionRule> fraudDetectionRules;
    
    // İşlem sayılarını izlemek için
    private final Map<String, TransactionCounter> transactionCounters = new ConcurrentHashMap<>();
    
    @Value("${fraud.detection.enabled:true}")
    private boolean fraudDetectionEnabled;
    
    @Value("${fraud.detection.high.amount.threshold:50000}")
    private BigDecimal highAmountThreshold;
    
    @Value("${fraud.detection.medium.amount.threshold:10000}")
    private BigDecimal mediumAmountThreshold;
    
    @Value("${fraud.detection.frequency.threshold:5}")
    private int frequencyThreshold;
    
    @Value("${fraud.detection.time.window.minutes:60}")
    private int timeWindowMinutes;
    
    /**
     * Kredi başvurusunu dolandırıcılık açısından kontrol eder.
     * 
     * @param customerId Müşteri ID
     * @param applicationId Başvuru ID
     * @param amount Başvuru tutarı
     * @param ipAddress IP adresi
     * @param userAgent Kullanıcı ajanı
     * @return Dolandırıcılık şüphesi varsa true, yoksa false
     */
    @Transactional
    public boolean checkCreditApplicationForFraud(Long customerId, Long applicationId, BigDecimal amount, 
                                               String ipAddress, String userAgent) throws Exception {
        if (!fraudDetectionEnabled) {
            log.info("Fraud detection is disabled. Skipping check for application: {}", applicationId);
            return false;
        }
        
        Customer customer = null;
        try {
            customer = (Customer) customerRepository.findById(customerId).get();
        } catch (Exception e) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
        
        // İşlem sayacını güncelle
        String key = "credit_application:" + customerId;
        TransactionCounter counter = transactionCounters.computeIfAbsent(key, k -> new TransactionCounter());
        counter.incrementCount(amount);
        
        // Kural tabanlı kontroller
        int riskScore = 0;
        String detectionRule = null;
        String description = null;
        
        // Yüksek tutarlı işlem kontrolü
        if (amount.compareTo(highAmountThreshold) >= 0) {
            riskScore += 30;
            detectionRule = "HIGH_AMOUNT";
            description = "High amount credit application: " + amount;
            log.warn("High amount credit application detected: {} for customer: {}", amount, customerId);
        }
        // Orta tutarlı işlem kontrolü
        else if (amount.compareTo(mediumAmountThreshold) >= 0) {
            riskScore += 15;
            detectionRule = "MEDIUM_AMOUNT";
            description = "Medium amount credit application: " + amount;
            log.info("Medium amount credit application detected: {} for customer: {}", amount, customerId);
        }
        
        // Sık işlem kontrolü
        if (counter.getCount() > frequencyThreshold) {
            riskScore += 25;
            detectionRule = "FREQUENT_APPLICATIONS";
            description = "Frequent credit applications: " + counter.getCount() + " in the last " + timeWindowMinutes + " minutes";
            log.warn("Frequent credit applications detected for customer: {}, count: {}", customerId, counter.getCount());
        }
        
        // Toplam tutar kontrolü
        if (counter.getTotalAmount().compareTo(highAmountThreshold.multiply(BigDecimal.valueOf(2))) >= 0) {
            riskScore += 20;
            detectionRule = "HIGH_TOTAL_AMOUNT";
            description = "High total amount in credit applications: " + counter.getTotalAmount();
            log.warn("High total amount in credit applications detected for customer: {}, total: {}", 
                    customerId, counter.getTotalAmount());
        }
        
        // Kural motoru ile kontrol
        for (FraudDetectionRule rule : fraudDetectionRules) {
            if (rule.evaluate(customer, amount, ipAddress, userAgent)) {
                riskScore += rule.getRiskScore();
                detectionRule = rule.getName();
                description = rule.getDescription();
                log.warn("Fraud detection rule triggered: {} for customer: {}", rule.getName(), customerId);
            }
        }
        
        // Şüpheli işlem kaydı oluştur
        if (riskScore >= 30) {
            SuspiciousTransaction suspiciousTransaction = new SuspiciousTransaction();
            suspiciousTransaction.setCustomer(customer);
            suspiciousTransaction.setTransactionType(TransactionType.CREDIT_APPLICATION);
            suspiciousTransaction.setTransactionId(applicationId.toString());
            suspiciousTransaction.setAmount(amount);
            suspiciousTransaction.setTransactionDate(LocalDateTime.now());
            suspiciousTransaction.setDetectionDate(LocalDateTime.now());
            suspiciousTransaction.setRiskScore(Double.valueOf(riskScore));
            suspiciousTransaction.setRiskLevel(getRiskLevel(riskScore));
            suspiciousTransaction.setDetectionRule(detectionRule);
            suspiciousTransaction.setDescription(description);
            suspiciousTransaction.setStatus(TransactionStatus.PENDING);
            suspiciousTransaction.setIpAddress(ipAddress);
            suspiciousTransaction.setUserAgent(userAgent);
            
            suspiciousTransactionRepository.save(suspiciousTransaction);
            
            // Olay yayınla
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("suspiciousTransactionId", suspiciousTransaction.getId());
            eventData.put("customerId", customerId);
            eventData.put("applicationId", applicationId);
            eventData.put("riskScore", riskScore);
            eventData.put("riskLevel", suspiciousTransaction.getRiskLevel());
            eventData.put("detectionRule", detectionRule);
            
            eventService.publishEvent("fraud.detection.credit.application", eventData);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Müşteri bilgisi güncelleme işlemini dolandırıcılık açısından kontrol eder.
     * 
     * @param customerId Müşteri ID
     * @param fieldName Değiştirilen alan adı
     * @param oldValue Eski değer
     * @param newValue Yeni değer
     * @param ipAddress IP adresi
     * @param userAgent Kullanıcı ajanı
     * @return Dolandırıcılık şüphesi varsa true, yoksa false
     */
    @Transactional
    public boolean checkCustomerInfoUpdateForFraud(Long customerId, String fieldName, String oldValue, String newValue, 
                                                String ipAddress, String userAgent) throws Exception {
        if (!fraudDetectionEnabled) {
            log.info("Fraud detection is disabled. Skipping check for customer: {}", customerId);
            return false;
        }
        
        Customer customer = null;
        try {
            customer = (Customer) customerRepository.findById(customerId).get();
        } catch (Exception e) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
        
        // İşlem sayacını güncelle
        String key = "customer_update:" + customerId;
        TransactionCounter counter = transactionCounters.computeIfAbsent(key, k -> new TransactionCounter());
        counter.incrementCount(BigDecimal.ZERO);
        
        // Kural tabanlı kontroller
        int riskScore = 0;
        String detectionRule = null;
        String description = null;
        
        // Hassas alan değişikliği kontrolü
        boolean isSensitiveField = fieldName.equalsIgnoreCase("email") || 
                                  fieldName.equalsIgnoreCase("phone") || 
                                  fieldName.equalsIgnoreCase("address") || 
                                  fieldName.equalsIgnoreCase("identityNumber") || 
                                  fieldName.equalsIgnoreCase("taxNumber");
        
        if (isSensitiveField) {
            riskScore += 20;
            detectionRule = "SENSITIVE_FIELD_UPDATE";
            description = "Sensitive field update: " + fieldName + " from " + oldValue + " to " + newValue;
            log.warn("Sensitive field update detected for customer: {}, field: {}", customerId, fieldName);
        }
        
        // Sık güncelleme kontrolü
        if (counter.getCount() > frequencyThreshold) {
            riskScore += 25;
            detectionRule = "FREQUENT_UPDATES";
            description = "Frequent customer info updates: " + counter.getCount() + " in the last " + timeWindowMinutes + " minutes";
            log.warn("Frequent customer info updates detected for customer: {}, count: {}", customerId, counter.getCount());
        }
        
        // Kural motoru ile kontrol
        for (FraudDetectionRule rule : fraudDetectionRules) {
            if (rule.evaluate(customer, BigDecimal.ZERO, ipAddress, userAgent)) {
                riskScore += rule.getRiskScore();
                detectionRule = rule.getName();
                description = rule.getDescription();
                log.warn("Fraud detection rule triggered: {} for customer: {}", rule.getName(), customerId);
            }
        }
        
        // Şüpheli işlem kaydı oluştur
        if (riskScore >= 30) {
            SuspiciousTransaction suspiciousTransaction = new SuspiciousTransaction();
            suspiciousTransaction.setCustomer(customer);
            suspiciousTransaction.setTransactionType(TransactionType.CUSTOMER_INFO_UPDATE);
            suspiciousTransaction.setTransactionId(null);
            suspiciousTransaction.setAmount(BigDecimal.ZERO);
            suspiciousTransaction.setTransactionDate(LocalDateTime.now());
            suspiciousTransaction.setDetectionDate(LocalDateTime.now());
            suspiciousTransaction.setRiskScore(Double.valueOf(riskScore));
            suspiciousTransaction.setRiskLevel(getRiskLevel(riskScore));
            suspiciousTransaction.setDetectionRule(detectionRule);
            suspiciousTransaction.setDescription(description);
            suspiciousTransaction.setStatus(TransactionStatus.PENDING);
            suspiciousTransaction.setIpAddress(ipAddress);
            suspiciousTransaction.setUserAgent(userAgent);
            
            suspiciousTransactionRepository.save(suspiciousTransaction);
            
            // Olay yayınla
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("suspiciousTransactionId", suspiciousTransaction.getId());
            eventData.put("customerId", customerId);
            eventData.put("fieldName", fieldName);
            eventData.put("oldValue", oldValue);
            eventData.put("newValue", newValue);
            eventData.put("riskScore", riskScore);
            eventData.put("riskLevel", suspiciousTransaction.getRiskLevel());
            eventData.put("detectionRule", detectionRule);
            
            eventService.publishEvent("fraud.detection.customer.info.update", eventData);
            
            return true;
        }
        
        return false;
    }
    
    private RiskLevel getRiskLevel(int riskScore) {
        if (riskScore >= 70) {
            return RiskLevel.HIGH;
        } else if (riskScore >= 40) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.LOW;
        }
    }
    
    /**
     * İşlem sayaçlarını temizler.
     * Her gün gece yarısı çalışır.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupTransactionCounters() {
        log.info("Cleaning up transaction counters");
        transactionCounters.clear();
    }
    
    /**
     * İşlem sayacı sınıfı.
     * İşlem sayısı, toplam tutar ve son güncelleme zamanını tutar.
     */
    private class TransactionCounter {
        private int count;
        private BigDecimal totalAmount;
        private LocalDateTime lastUpdateTime;
        
        public TransactionCounter() {
            this.count = 0;
            this.totalAmount = BigDecimal.ZERO;
            this.lastUpdateTime = LocalDateTime.now();
        }
        
        public void incrementCount(BigDecimal amount) {
            LocalDateTime now = LocalDateTime.now();
            
            // Zaman penceresi dışındaysa sayacı sıfırla
            if (lastUpdateTime.plusMinutes(timeWindowMinutes).isBefore(now)) {
                count = 0;
                totalAmount = BigDecimal.ZERO;
            }
            
            count++;
            totalAmount = totalAmount.add(amount);
            lastUpdateTime = now;
        }
        
        public int getCount() {
            LocalDateTime now = LocalDateTime.now();
            
            // Zaman penceresi dışındaysa sayacı sıfırla
            if (lastUpdateTime.plusMinutes(timeWindowMinutes).isBefore(now)) {
                count = 0;
                totalAmount = BigDecimal.ZERO;
            }
            
            return count;
        }
        
        public BigDecimal getTotalAmount() {
            LocalDateTime now = LocalDateTime.now();
            
            // Zaman penceresi dışındaysa sayacı sıfırla
            if (lastUpdateTime.plusMinutes(timeWindowMinutes).isBefore(now)) {
                count = 0;
                totalAmount = BigDecimal.ZERO;
            }
            
            return totalAmount;
        }
    }
} 
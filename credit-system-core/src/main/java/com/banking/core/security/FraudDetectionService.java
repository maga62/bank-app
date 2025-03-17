package com.banking.core.security;

import com.banking.core.audit.AuditService;
import com.banking.core.logging.LogAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dolandırıcılık tespit servisi.
 * Şüpheli işlemleri tespit etmek ve önlemek için kullanılır.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final LogAnalyticsService logAnalyticsService;
    private final AuditService auditService;
    
    @Value("${fraud.detection.enabled:true}")
    private boolean fraudDetectionEnabled;
    
    @Value("${fraud.detection.max.failed.attempts:5}")
    private int maxFailedAttempts;
    
    @Value("${fraud.detection.time.window.minutes:30}")
    private int timeWindowMinutes;
    
    @Value("${fraud.detection.amount.threshold:10000}")
    private double amountThreshold;
    
    // Başarısız giriş denemelerini izlemek için
    private final Map<String, AtomicInteger> failedLoginAttempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastFailedAttemptTime = new ConcurrentHashMap<>();
    
    // Şüpheli işlemleri izlemek için
    private final Map<String, Map<String, Object>> suspiciousActivities = new ConcurrentHashMap<>();

    /**
     * Başarısız giriş denemesini kaydeder ve şüpheli aktivite olup olmadığını kontrol eder.
     * 
     * @param username Kullanıcı adı
     * @param ipAddress IP adresi
     * @param userAgent Kullanıcı ajanı
     * @return Hesabın kilitlenip kilitlenmediği
     */
    public boolean recordFailedLoginAttempt(String username, String ipAddress, String userAgent) {
        if (!fraudDetectionEnabled) {
            return false;
        }
        
        String key = username + ":" + ipAddress;
        
        // Başarısız giriş denemesi sayısını artır
        AtomicInteger attempts = failedLoginAttempts.computeIfAbsent(key, k -> new AtomicInteger(0));
        int currentAttempts = attempts.incrementAndGet();
        
        // Son başarısız deneme zamanını güncelle
        lastFailedAttemptTime.put(key, LocalDateTime.now());
        
        // Şüpheli aktiviteyi logla
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("ipAddress", ipAddress);
        details.put("userAgent", userAgent);
        details.put("attemptCount", currentAttempts);
        
        logAnalyticsService.logSystemEvent(
                "FAILED_LOGIN_ATTEMPT",
                "FraudDetectionService",
                details
        );
        
        // Maksimum başarısız deneme sayısını aştıysa hesabı kilitle
        if (currentAttempts >= maxFailedAttempts) {
            lockAccount(username, ipAddress, userAgent, currentAttempts);
            return true;
        }
        
        return false;
    }
    
    /**
     * Başarılı giriş sonrası başarısız giriş denemelerini sıfırlar.
     * 
     * @param username Kullanıcı adı
     * @param ipAddress IP adresi
     */
    public void resetFailedLoginAttempts(String username, String ipAddress) {
        String key = username + ":" + ipAddress;
        failedLoginAttempts.remove(key);
        lastFailedAttemptTime.remove(key);
    }
    
    /**
     * Hesabı kilitler ve olayı loglar.
     * 
     * @param username Kullanıcı adı
     * @param ipAddress IP adresi
     * @param userAgent Kullanıcı ajanı
     * @param attemptCount Deneme sayısı
     */
    private void lockAccount(String username, String ipAddress, String userAgent, int attemptCount) {
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("ipAddress", ipAddress);
        details.put("userAgent", userAgent);
        details.put("attemptCount", attemptCount);
        details.put("lockTime", LocalDateTime.now());
        
        logAnalyticsService.logSystemEvent(
                "ACCOUNT_LOCKED",
                "FraudDetectionService",
                details
        );
        
        auditService.logSecurityEvent(
                "ACCOUNT_LOCKED",
                null,
                ipAddress,
                userAgent,
                details
        );
        
        log.warn("Account locked due to too many failed login attempts: {}, IP: {}", username, ipAddress);
    }
    
    /**
     * Kredi başvurusunu dolandırıcılık açısından kontrol eder.
     * 
     * @param customerId Müşteri ID
     * @param amount Kredi tutarı
     * @param ipAddress IP adresi
     * @param userAgent Kullanıcı ajanı
     * @return Dolandırıcılık şüphesi varsa true, yoksa false
     */
    public boolean checkCreditApplicationForFraud(Long customerId, double amount, String ipAddress, String userAgent) {
        if (!fraudDetectionEnabled) {
            return false;
        }
        
        boolean isSuspicious = false;
        Map<String, Object> details = new HashMap<>();
        details.put("customerId", customerId);
        details.put("amount", amount);
        details.put("ipAddress", ipAddress);
        details.put("userAgent", userAgent);
        details.put("timestamp", LocalDateTime.now());
        
        // Yüksek tutarlı işlemleri kontrol et
        if (amount > amountThreshold) {
            isSuspicious = true;
            details.put("reason", "HIGH_AMOUNT");
            details.put("threshold", amountThreshold);
        }
        
        // Şüpheli aktivite varsa logla
        if (isSuspicious) {
            String key = "credit_application:" + customerId;
            suspiciousActivities.put(key, details);
            
            logAnalyticsService.logSystemEvent(
                    "SUSPICIOUS_CREDIT_APPLICATION",
                    "FraudDetectionService",
                    details
            );
            
            auditService.logSecurityEvent(
                    "SUSPICIOUS_CREDIT_APPLICATION",
                    customerId,
                    ipAddress,
                    userAgent,
                    details
            );
            
            log.warn("Suspicious credit application detected: Customer ID: {}, Amount: {}, IP: {}", 
                    customerId, amount, ipAddress);
        }
        
        return isSuspicious;
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
    public boolean checkCustomerInfoUpdateForFraud(Long customerId, String fieldName, 
                                                 String oldValue, String newValue, 
                                                 String ipAddress, String userAgent) {
        if (!fraudDetectionEnabled) {
            return false;
        }
        
        boolean isSuspicious = false;
        Map<String, Object> details = new HashMap<>();
        details.put("customerId", customerId);
        details.put("fieldName", fieldName);
        details.put("oldValue", oldValue);
        details.put("newValue", newValue);
        details.put("ipAddress", ipAddress);
        details.put("userAgent", userAgent);
        details.put("timestamp", LocalDateTime.now());
        
        // Hassas alan değişikliklerini kontrol et
        if (fieldName.equalsIgnoreCase("email") || 
            fieldName.equalsIgnoreCase("phone") || 
            fieldName.equalsIgnoreCase("address") || 
            fieldName.equalsIgnoreCase("identityNumber") || 
            fieldName.equalsIgnoreCase("taxNumber")) {
            
            isSuspicious = true;
            details.put("reason", "SENSITIVE_FIELD_CHANGE");
        }
        
        // Şüpheli aktivite varsa logla
        if (isSuspicious) {
            String key = "customer_update:" + customerId + ":" + fieldName;
            suspiciousActivities.put(key, details);
            
            logAnalyticsService.logSystemEvent(
                    "SUSPICIOUS_CUSTOMER_UPDATE",
                    "FraudDetectionService",
                    details
            );
            
            auditService.logSecurityEvent(
                    "SUSPICIOUS_CUSTOMER_UPDATE",
                    customerId,
                    ipAddress,
                    userAgent,
                    details
            );
            
            log.warn("Suspicious customer info update detected: Customer ID: {}, Field: {}, IP: {}", 
                    customerId, fieldName, ipAddress);
        }
        
        return isSuspicious;
    }
} 
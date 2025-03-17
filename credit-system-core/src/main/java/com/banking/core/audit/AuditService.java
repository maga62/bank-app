package com.banking.core.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * İşlem izleme servisi.
 * Hangi müşteri hangi başvuruyu yaptı, hangi personel hangi işlemi gerçekleştirdi gibi bilgileri detaylı loglama sağlar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("com.banking.core.audit");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Müşteri işlemini loglar
     * 
     * @param customerId Müşteri ID
     * @param customerType Müşteri tipi
     * @param action İşlem
     * @param entityType Varlık tipi
     * @param entityId Varlık ID
     * @param details Detaylar
     */
    public void logCustomerAction(
            Long customerId,
            String customerType,
            String action,
            String entityType,
            Long entityId,
            Map<String, Object> details) {
        
        String auditId = UUID.randomUUID().toString();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("auditId", auditId);
        auditData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
        auditData.put("actorType", "CUSTOMER");
        auditData.put("actorId", customerId);
        auditData.put("customerType", customerType);
        auditData.put("action", action);
        auditData.put("entityType", entityType);
        auditData.put("entityId", entityId);
        auditData.put("details", details);
        
        AUDIT_LOGGER.info("Customer Action: {}", auditData);
        log.debug("Audit log created for customer action. Customer ID: {}, Action: {}", customerId, action);
    }

    /**
     * Personel işlemini loglar
     * 
     * @param userId Kullanıcı ID
     * @param userRole Kullanıcı rolü
     * @param action İşlem
     * @param entityType Varlık tipi
     * @param entityId Varlık ID
     * @param details Detaylar
     */
    public void logUserAction(
            Long userId,
            String userRole,
            String action,
            String entityType,
            Long entityId,
            Map<String, Object> details) {
        
        String auditId = UUID.randomUUID().toString();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("auditId", auditId);
        auditData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
        auditData.put("actorType", "USER");
        auditData.put("actorId", userId);
        auditData.put("userRole", userRole);
        auditData.put("action", action);
        auditData.put("entityType", entityType);
        auditData.put("entityId", entityId);
        auditData.put("details", details);
        
        AUDIT_LOGGER.info("User Action: {}", auditData);
        log.debug("Audit log created for user action. User ID: {}, Action: {}", userId, action);
    }

    /**
     * Sistem işlemini loglar
     * 
     * @param systemComponent Sistem bileşeni
     * @param action İşlem
     * @param entityType Varlık tipi
     * @param entityId Varlık ID
     * @param details Detaylar
     */
    public void logSystemAction(
            String systemComponent,
            String action,
            String entityType,
            Long entityId,
            Map<String, Object> details) {
        
        String auditId = UUID.randomUUID().toString();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("auditId", auditId);
        auditData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
        auditData.put("actorType", "SYSTEM");
        auditData.put("systemComponent", systemComponent);
        auditData.put("action", action);
        auditData.put("entityType", entityType);
        auditData.put("entityId", entityId);
        auditData.put("details", details);
        
        AUDIT_LOGGER.info("System Action: {}", auditData);
        log.debug("Audit log created for system action. Component: {}, Action: {}", systemComponent, action);
    }

    /**
     * Veri değişikliğini loglar
     * 
     * @param actorType Aktör tipi (CUSTOMER, USER, SYSTEM)
     * @param actorId Aktör ID
     * @param entityType Varlık tipi
     * @param entityId Varlık ID
     * @param action İşlem (CREATE, UPDATE, DELETE)
     * @param oldValues Eski değerler
     * @param newValues Yeni değerler
     */
    public void logDataChange(
            String actorType,
            Long actorId,
            String entityType,
            Long entityId,
            String action,
            Map<String, Object> oldValues,
            Map<String, Object> newValues) {
        
        String auditId = UUID.randomUUID().toString();
        
        Map<String, Object> auditData = new HashMap<>();
        auditData.put("auditId", auditId);
        auditData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
        auditData.put("actorType", actorType);
        auditData.put("actorId", actorId);
        auditData.put("entityType", entityType);
        auditData.put("entityId", entityId);
        auditData.put("action", action);
        auditData.put("oldValues", oldValues);
        auditData.put("newValues", newValues);
        
        AUDIT_LOGGER.info("Data Change: {}", auditData);
        log.debug("Audit log created for data change. Entity: {}, ID: {}, Action: {}", entityType, entityId, action);
    }

    /**
     * Güvenlik olaylarını loglar.
     *
     * @param eventType Olay tipi
     * @param userId Kullanıcı ID
     * @param ipAddress IP adresi
     * @param userAgent Kullanıcı ajanı
     * @param details Ek detaylar
     */
    public void logSecurityEvent(String eventType, Long userId, String ipAddress, String userAgent, Map<String, Object> details) {
        String auditId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();
        
        Map<String, Object> logData = new HashMap<>(details != null ? details : new HashMap<>());
        logData.put("auditId", auditId);
        logData.put("timestamp", timestamp);
        logData.put("eventType", eventType);
        logData.put("userId", userId);
        logData.put("ipAddress", ipAddress);
        logData.put("userAgent", userAgent);
        
        log.info("SECURITY_EVENT: {}", logData);
        
        if (log.isDebugEnabled()) {
            log.debug("Security event details: {}", logData);
        }
    }
} 
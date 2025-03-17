package com.banking.core.logging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Log analitik servisi.
 * API çağrıları, kullanıcı işlemleri ve sistem olayları için detaylı loglama sağlar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogAnalyticsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * API çağrısı loglar
     * 
     * @param userId Kullanıcı ID
     * @param endpoint Endpoint
     * @param httpMethod HTTP metodu
     * @param clientIp İstemci IP adresi
     * @param userAgent Kullanıcı ajanı
     * @param statusCode Durum kodu
     * @param responseTime Yanıt süresi (ms)
     * @param requestParams İstek parametreleri
     */
    public void logApiCall(
            String userId,
            String endpoint,
            String httpMethod,
            String clientIp,
            String userAgent,
            int statusCode,
            long responseTime,
            Map<String, String> requestParams) {
        
        String requestId = UUID.randomUUID().toString();
        
        try {
            MDC.put("userId", userId);
            MDC.put("requestId", requestId);
            MDC.put("clientIp", clientIp);
            MDC.put("userAgent", userAgent);
            MDC.put("endpoint", endpoint);
            MDC.put("httpMethod", httpMethod);
            MDC.put("statusCode", String.valueOf(statusCode));
            MDC.put("responseTime", String.valueOf(responseTime));
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            logData.put("userId", userId);
            logData.put("requestId", requestId);
            logData.put("clientIp", clientIp);
            logData.put("userAgent", userAgent);
            logData.put("endpoint", endpoint);
            logData.put("httpMethod", httpMethod);
            logData.put("statusCode", statusCode);
            logData.put("responseTime", responseTime);
            logData.put("requestParams", requestParams);
            
            log.info("API Call: {}", logData);
        } finally {
            MDC.remove("userId");
            MDC.remove("requestId");
            MDC.remove("clientIp");
            MDC.remove("userAgent");
            MDC.remove("endpoint");
            MDC.remove("httpMethod");
            MDC.remove("statusCode");
            MDC.remove("responseTime");
        }
    }

    /**
     * Kullanıcı işlemi loglar
     * 
     * @param userId Kullanıcı ID
     * @param action İşlem
     * @param details Detaylar
     * @param result Sonuç
     */
    public void logUserAction(
            String userId,
            String action,
            Map<String, Object> details,
            String result) {
        
        String actionId = UUID.randomUUID().toString();
        
        try {
            MDC.put("userId", userId);
            MDC.put("actionId", actionId);
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            logData.put("userId", userId);
            logData.put("actionId", actionId);
            logData.put("action", action);
            logData.put("details", details);
            logData.put("result", result);
            
            log.info("User Action: {}", logData);
        } finally {
            MDC.remove("userId");
            MDC.remove("actionId");
        }
    }

    /**
     * Sistem olayı loglar
     * 
     * @param eventType Olay tipi
     * @param source Kaynak
     * @param details Detaylar
     */
    public void logSystemEvent(
            String eventType,
            String source,
            Map<String, Object> details) {
        
        String eventId = UUID.randomUUID().toString();
        
        try {
            MDC.put("eventId", eventId);
            MDC.put("eventType", eventType);
            MDC.put("source", source);
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            logData.put("eventId", eventId);
            logData.put("eventType", eventType);
            logData.put("source", source);
            logData.put("details", details);
            
            log.info("System Event: {}", logData);
        } finally {
            MDC.remove("eventId");
            MDC.remove("eventType");
            MDC.remove("source");
        }
    }

    /**
     * Performans metriği loglar
     * 
     * @param operation İşlem
     * @param executionTime Yürütme süresi (ms)
     * @param details Detaylar
     */
    public void logPerformanceMetric(
            String operation,
            long executionTime,
            Map<String, Object> details) {
        
        try {
            MDC.put("operation", operation);
            MDC.put("executionTime", String.valueOf(executionTime));
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            logData.put("operation", operation);
            logData.put("executionTime", executionTime);
            logData.put("details", details);
            
            log.info("Performance Metric: {}", logData);
        } finally {
            MDC.remove("operation");
            MDC.remove("executionTime");
        }
    }

    /**
     * Hata loglar
     * 
     * @param errorCode Hata kodu
     * @param errorMessage Hata mesajı
     * @param source Kaynak
     * @param details Detaylar
     * @param exception İstisna
     */
    public void logError(
            String errorCode,
            String errorMessage,
            String source,
            Map<String, Object> details,
            Throwable exception) {
        
        String errorId = UUID.randomUUID().toString();
        
        try {
            MDC.put("errorId", errorId);
            MDC.put("errorCode", errorCode);
            MDC.put("source", source);
            
            Map<String, Object> logData = new HashMap<>();
            logData.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            logData.put("errorId", errorId);
            logData.put("errorCode", errorCode);
            logData.put("errorMessage", errorMessage);
            logData.put("source", source);
            logData.put("details", details);
            
            if (exception != null) {
                log.error("Error: {}", logData, exception);
            } else {
                log.error("Error: {}", logData);
            }
        } finally {
            MDC.remove("errorId");
            MDC.remove("errorCode");
            MDC.remove("source");
        }
    }
} 
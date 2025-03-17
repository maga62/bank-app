package com.banking.core.events;

import com.banking.core.logging.LogAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Olay tabanlı mimari için event servisi.
 * Sistem içindeki olayları yayınlar ve dinler.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final ApplicationEventPublisher eventPublisher;
    private final LogAnalyticsService logAnalyticsService;

    /**
     * Genel olay yayınlar.
     *
     * @param eventType Olay tipi
     * @param data Olay verileri
     */
    public void publishEvent(String eventType, Map<String, Object> data) {
        String eventId = UUID.randomUUID().toString();
        SystemEvent event = new SystemEvent(this, eventId, eventType, "FraudDetectionService", data);
        
        log.debug("Publishing event: {}", eventType);
        eventPublisher.publishEvent(event);
    }

    /**
     * Müşteri olayı yayınlar.
     *
     * @param eventType Olay tipi
     * @param customerId Müşteri ID
     * @param data Olay verileri
     */
    public void publishCustomerEvent(String eventType, Long customerId, Map<String, Object> data) {
        String eventId = UUID.randomUUID().toString();
        CustomerEvent event = new CustomerEvent(this, eventId, eventType, customerId, data);
        
        log.debug("Publishing customer event: {}, Customer ID: {}", eventType, customerId);
        eventPublisher.publishEvent(event);
    }

    /**
     * Kredi başvurusu olayı yayınlar.
     *
     * @param eventType Olay tipi
     * @param applicationId Başvuru ID
     * @param customerId Müşteri ID
     * @param data Olay verileri
     */
    public void publishCreditApplicationEvent(String eventType, Long applicationId, Long customerId, Map<String, Object> data) {
        String eventId = UUID.randomUUID().toString();
        CreditApplicationEvent event = new CreditApplicationEvent(this, eventId, eventType, applicationId, customerId, data);
        
        log.debug("Publishing credit application event: {}, Application ID: {}", eventType, applicationId);
        eventPublisher.publishEvent(event);
    }

    /**
     * Sistem olayı yayınlar.
     *
     * @param eventType Olay tipi
     * @param source Kaynak
     * @param data Olay verileri
     */
    public void publishSystemEvent(String eventType, String source, Map<String, Object> data) {
        String eventId = UUID.randomUUID().toString();
        SystemEvent event = new SystemEvent(this, eventId, eventType, source, data);
        
        log.debug("Publishing system event: {}, Source: {}", eventType, source);
        eventPublisher.publishEvent(event);
    }

    /**
     * Müşteri olaylarını dinler.
     *
     * @param event Müşteri olayı
     */
    @EventListener
    @Async
    public void handleCustomerEvent(CustomerEvent event) {
        log.debug("Handling customer event: {}, Customer ID: {}", event.getEventType(), event.getCustomerId());
        
        // Olayı logla
        logAnalyticsService.logSystemEvent(
                "CUSTOMER_EVENT_" + event.getEventType(),
                "EventService",
                Map.of(
                        "eventId", event.getEventId(),
                        "customerId", event.getCustomerId(),
                        "data", event.getData()
                )
        );
        
        // Burada olaya göre farklı işlemler yapılabilir
        switch (event.getEventType()) {
            case "CREATED":
                // Müşteri oluşturulduğunda yapılacak işlemler
                break;
            case "UPDATED":
                // Müşteri güncellendiğinde yapılacak işlemler
                break;
            case "DELETED":
                // Müşteri silindiğinde yapılacak işlemler
                break;
            default:
                log.debug("Unhandled customer event type: {}", event.getEventType());
                break;
        }
    }

    /**
     * Kredi başvurusu olaylarını dinler.
     *
     * @param event Kredi başvurusu olayı
     */
    @EventListener
    @Async
    public void handleCreditApplicationEvent(CreditApplicationEvent event) {
        log.debug("Handling credit application event: {}, Application ID: {}", 
                event.getEventType(), event.getApplicationId());
        
        // Olayı logla
        logAnalyticsService.logSystemEvent(
                "CREDIT_APPLICATION_EVENT_" + event.getEventType(),
                "EventService",
                Map.of(
                        "eventId", event.getEventId(),
                        "applicationId", event.getApplicationId(),
                        "customerId", event.getCustomerId(),
                        "data", event.getData()
                )
        );
        
        // Burada olaya göre farklı işlemler yapılabilir
        switch (event.getEventType()) {
            case "SUBMITTED":
                // Kredi başvurusu yapıldığında yapılacak işlemler
                break;
            case "APPROVED":
                // Kredi başvurusu onaylandığında yapılacak işlemler
                break;
            case "REJECTED":
                // Kredi başvurusu reddedildiğinde yapılacak işlemler
                break;
            default:
                log.debug("Unhandled credit application event type: {}", event.getEventType());
                break;
        }
    }

    /**
     * Sistem olaylarını dinler.
     *
     * @param event Sistem olayı
     */
    @EventListener
    @Async
    public void handleSystemEvent(SystemEvent event) {
        log.debug("Handling system event: {}, Source: {}", event.getEventType(), event.getSource());
        
        // Olayı logla
        logAnalyticsService.logSystemEvent(
                "SYSTEM_EVENT_" + event.getEventType(),
                "EventService",
                Map.of(
                        "eventId", event.getEventId(),
                        "source", event.getSource(),
                        "data", event.getData()
                )
        );
        
        // Burada olaya göre farklı işlemler yapılabilir
        switch (event.getEventType()) {
            case "STARTUP":
                // Sistem başlatıldığında yapılacak işlemler
                break;
            case "SHUTDOWN":
                // Sistem kapatıldığında yapılacak işlemler
                break;
            case "ERROR":
                // Sistem hatası oluştuğunda yapılacak işlemler
                break;
            default:
                log.debug("Unhandled system event type: {}", event.getEventType());
                break;
        }
    }
} 
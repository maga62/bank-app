package com.banking.core.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Kredi başvurusu ile ilgili olaylar için event sınıfı.
 */
@Getter
public class CreditApplicationEvent extends ApplicationEvent {

    private final String eventId;
    private final String eventType;
    private final Long applicationId;
    private final Long customerId;
    private final Map<String, Object> data;

    /**
     * Yeni bir kredi başvurusu olayı oluşturur.
     *
     * @param source Olayı yayınlayan kaynak
     * @param eventId Olay ID
     * @param eventType Olay tipi
     * @param applicationId Başvuru ID
     * @param customerId Müşteri ID
     * @param data Olay verileri
     */
    public CreditApplicationEvent(Object source, String eventId, String eventType, 
                                 Long applicationId, Long customerId, Map<String, Object> data) {
        super(source);
        this.eventId = eventId;
        this.eventType = eventType;
        this.applicationId = applicationId;
        this.customerId = customerId;
        this.data = data;
    }
} 
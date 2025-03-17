package com.banking.core.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Müşteri ile ilgili olaylar için event sınıfı.
 */
@Getter
public class CustomerEvent extends ApplicationEvent {

    private final String eventId;
    private final String eventType;
    private final Long customerId;
    private final Map<String, Object> data;

    /**
     * Yeni bir müşteri olayı oluşturur.
     *
     * @param source Olayı yayınlayan kaynak
     * @param eventId Olay ID
     * @param eventType Olay tipi
     * @param customerId Müşteri ID
     * @param data Olay verileri
     */
    public CustomerEvent(Object source, String eventId, String eventType, Long customerId, Map<String, Object> data) {
        super(source);
        this.eventId = eventId;
        this.eventType = eventType;
        this.customerId = customerId;
        this.data = data;
    }
} 
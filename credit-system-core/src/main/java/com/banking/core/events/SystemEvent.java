package com.banking.core.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Sistem ile ilgili olaylar için event sınıfı.
 */
@Getter
public class SystemEvent extends ApplicationEvent {

    private final String eventId;
    private final String eventType;
    private final String source;
    private final Map<String, Object> data;

    /**
     * Yeni bir sistem olayı oluşturur.
     *
     * @param publisher Olayı yayınlayan kaynak
     * @param eventId Olay ID
     * @param eventType Olay tipi
     * @param source Kaynak
     * @param data Olay verileri
     */
    public SystemEvent(Object publisher, String eventId, String eventType, String source, Map<String, Object> data) {
        super(publisher);
        this.eventId = eventId;
        this.eventType = eventType;
        this.source = source;
        this.data = data;
    }
} 
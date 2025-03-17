package com.banking.business.payment;

import com.banking.entities.enums.PaymentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentProcessorFactory {
    
    private final Map<PaymentProvider, PaymentProcessor> processors;
    
    @Autowired
    public PaymentProcessorFactory(Map<PaymentProvider, PaymentProcessor> processors) {
        this.processors = processors;
    }
    
    public PaymentProcessor getProcessor(PaymentProvider provider) {
        PaymentProcessor processor = processors.get(provider);
        if (processor == null) {
            throw new IllegalArgumentException("No processor found for provider: " + provider);
        }
        return processor;
    }
} 
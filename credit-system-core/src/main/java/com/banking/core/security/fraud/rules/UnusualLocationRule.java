package com.banking.core.security.fraud.rules;

import com.banking.core.security.fraud.FraudDetectionRule;
import com.banking.entities.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Olağandışı konum kuralı.
 * Müşterinin daha önce kullanmadığı bir IP adresinden işlem yapması durumunda tetiklenir.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UnusualLocationRule implements FraudDetectionRule {

    // Müşteri başına son kullanılan IP adreslerini saklar
    private final Set<String> knownIpAddresses = new HashSet<>();
    
    @Value("${fraud.rules.unusual.location.score:40}")
    private int riskScore;
    
    @Override
    public boolean evaluate(Customer customer, BigDecimal amount, String ipAddress, String userAgent) {
        String customerIpKey = customer.getId() + ":" + ipAddress;
        
        if (!knownIpAddresses.contains(customerIpKey)) {
            // Yeni IP adresi tespit edildi
            log.info("Unusual location detected for customer {}: {}", customer.getId(), ipAddress);
            
            // IP adresini bilinen adresler listesine ekle
            knownIpAddresses.add(customerIpKey);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getName() {
        return "UNUSUAL_LOCATION";
    }
    
    @Override
    public String getDescription() {
        return "Transaction from an unusual location or IP address";
    }
    
    @Override
    public int getRiskScore() {
        return riskScore;
    }
} 
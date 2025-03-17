package com.banking.business.fraud;

import com.banking.business.dtos.request.TransactionMonitorRequest;
import com.banking.business.enums.RiskLevel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class UnusualLocationRule implements FraudDetectionRule {
    
    private static final List<String> HIGH_RISK_COUNTRIES = Arrays.asList(
            "XY", "ZZ", "YY" // Örnek yüksek riskli ülke kodları
    );
    
    private static final List<String> MEDIUM_RISK_COUNTRIES = Arrays.asList(
            "AB", "CD", "EF" // Örnek orta riskli ülke kodları
    );
    
    private String riskReason;

    @Override
    public boolean isApplicable(TransactionMonitorRequest request) {
        // Lokasyon bilgisi varsa kural uygulanabilir
        return request.getLocation() != null && !request.getLocation().isEmpty();
    }

    @Override
    public RiskLevel evaluateRisk(TransactionMonitorRequest request) {
        String location = request.getLocation();
        String countryCode = extractCountryCode(location);
        
        if (HIGH_RISK_COUNTRIES.contains(countryCode)) {
            riskReason = "Transaction from high-risk location: " + countryCode;
            return RiskLevel.HIGH;
        } else if (MEDIUM_RISK_COUNTRIES.contains(countryCode)) {
            riskReason = "Transaction from medium-risk location: " + countryCode;
            return RiskLevel.MEDIUM;
        }
        
        // Müşterinin normal lokasyonundan farklı bir lokasyon kontrolü yapılabilir
        // Bu örnekte basit tutuyoruz
        
        riskReason = "No location-based risk detected";
        return RiskLevel.LOW;
    }

    @Override
    public String getRiskReason() {
        return riskReason;
    }
    
    // Lokasyon bilgisinden ülke kodunu çıkaran yardımcı metod
    private String extractCountryCode(String location) {
        // Gerçek uygulamada, lokasyon bilgisinden ülke kodunu çıkarmak için
        // daha karmaşık bir mantık kullanılabilir
        if (location.contains(",")) {
            String[] parts = location.split(",");
            return parts[parts.length - 1].trim();
        }
        return location.substring(0, Math.min(location.length(), 2));
    }
} 
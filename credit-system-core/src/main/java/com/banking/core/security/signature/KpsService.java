package com.banking.core.security.signature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Kimlik Paylaşım Sistemi (KPS) servisi.
 * Türkiye Cumhuriyeti vatandaşlarının kimlik bilgilerini doğrular.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KpsService {

    private final RestTemplate restTemplate;
    
    @Value("${kps.api.url:https://kpsv2.nvi.gov.tr/Services/IdentityService}")
    private String kpsApiUrl;
    
    @Value("${kps.api.enabled:false}")
    private boolean kpsEnabled;
    
    /**
     * Kimlik bilgilerini doğrular.
     * 
     * @param firstName Ad
     * @param lastName Soyad
     * @param identityNumber TC Kimlik No
     * @param birthDate Doğum Tarihi
     * @return Doğrulama sonucu
     */
    public boolean verifyIdentity(String firstName, String lastName, String identityNumber, LocalDate birthDate) {
        if (!kpsEnabled) {
            log.info("KPS integration is disabled. Returning mock verification result");
            return true;
        }
        
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("firstName", firstName);
            request.put("lastName", lastName);
            request.put("identityNumber", identityNumber);
            request.put("birthDate", birthDate.format(DateTimeFormatter.ISO_DATE));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(kpsApiUrl, request, Map.class);
            
            if (response != null && response.containsKey("verified")) {
                boolean verified = (boolean) response.get("verified");
                log.info("KPS verification result for identity number {}: {}", identityNumber, verified);
                return verified;
            } else {
                log.error("Invalid response from KPS API");
                return false;
            }
        } catch (Exception e) {
            log.error("Error verifying identity with KPS: {}", e.getMessage(), e);
            return false;
        }
    }
} 
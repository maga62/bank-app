package com.banking.core.security.signature;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Türkiye e-İmza API'si ile entegrasyon sağlayan istemci.
 * Dijital imza oluşturma ve doğrulama işlemlerini gerçekleştirir.
 */
@Service
@Slf4j
public class ESignatureApiClient {

    private final RestTemplate restTemplate;
    
    @Value("${esignature.api.url:https://api.e-imza.gov.tr}")
    private String apiUrl;
    
    @Value("${esignature.api.key}")
    private String apiKey;
    
    @Value("${esignature.enabled:false}")
    private boolean eSignatureEnabled;
    
    public ESignatureApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Dijital imza oluşturur.
     * 
     * @param documentHash Belge hash'i
     * @param identityNumber TC Kimlik No
     * @return İmza yanıtı
     */
    public SignatureResponse createSignature(String documentHash, String identityNumber) {
        if (!eSignatureEnabled) {
            log.info("e-Signature integration is disabled. Returning mock signature result");
            return createMockSignatureResponse(documentHash);
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("documentHash", documentHash);
            requestBody.put("identityNumber", identityNumber);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // e-İmza API'sine istek gönder
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    apiUrl + "/api/v1/signatures/create", request, Map.class);
            
            if (response != null) {
                SignatureResponse signatureResponse = new SignatureResponse();
                signatureResponse.setSuccess(true);
                signatureResponse.setSignatureData((String) response.get("signatureData"));
                signatureResponse.setCertificateSerialNumber((String) response.get("certificateSerialNumber"));
                signatureResponse.setCertificateIssuer((String) response.get("certificateIssuer"));
                signatureResponse.setCertificateSubject((String) response.get("certificateSubject"));
                
                // Tarih alanlarını dönüştür
                if (response.get("certificateValidFrom") != null) {
                    signatureResponse.setCertificateValidFrom(LocalDateTime.parse((String) response.get("certificateValidFrom")));
                }
                
                if (response.get("certificateValidTo") != null) {
                    signatureResponse.setCertificateValidTo(LocalDateTime.parse((String) response.get("certificateValidTo")));
                }
                
                return signatureResponse;
            } else {
                SignatureResponse signatureResponse = new SignatureResponse();
                signatureResponse.setSuccess(false);
                signatureResponse.setErrorMessage("Invalid response from e-Signature API");
                return signatureResponse;
            }
        } catch (Exception e) {
            log.error("Error creating signature with e-Signature API: {}", e.getMessage(), e);
            SignatureResponse signatureResponse = new SignatureResponse();
            signatureResponse.setSuccess(false);
            signatureResponse.setErrorMessage("Error: " + e.getMessage());
            return signatureResponse;
        }
    }
    
    /**
     * Dijital imzayı doğrular.
     * 
     * @param signatureData İmza verisi
     * @param documentHash Belge hash'i
     * @return Doğrulama yanıtı
     */
    public VerificationResponse verifySignature(String signatureData, String documentHash) {
        if (!eSignatureEnabled) {
            log.info("e-Signature integration is disabled. Returning mock verification result");
            return createMockVerificationResponse();
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", apiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("signatureData", signatureData);
            requestBody.put("documentHash", documentHash);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // e-İmza API'sine istek gönder
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    apiUrl + "/api/v1/signatures/verify", request, Map.class);
            
            if (response != null) {
                VerificationResponse verificationResponse = new VerificationResponse();
                verificationResponse.setValid((boolean) response.get("valid"));
                verificationResponse.setResultMessage((String) response.get("resultMessage"));
                return verificationResponse;
            } else {
                VerificationResponse verificationResponse = new VerificationResponse();
                verificationResponse.setValid(false);
                verificationResponse.setResultMessage("Invalid response from e-Signature API");
                return verificationResponse;
            }
        } catch (Exception e) {
            log.error("Error verifying signature with e-Signature API: {}", e.getMessage(), e);
            VerificationResponse verificationResponse = new VerificationResponse();
            verificationResponse.setValid(false);
            verificationResponse.setResultMessage("Error: " + e.getMessage());
            return verificationResponse;
        }
    }
    
    /**
     * Test ortamı için sahte imza yanıtı oluşturur.
     * 
     * @param documentHash Belge hash'i
     * @return Sahte imza yanıtı
     */
    private SignatureResponse createMockSignatureResponse(String documentHash) {
        SignatureResponse response = new SignatureResponse();
        response.setSuccess(true);
        response.setSignatureData("MOCK_SIGNATURE_" + documentHash);
        response.setCertificateSerialNumber("MOCK_SERIAL_123456");
        response.setCertificateIssuer("CN=Mock e-Signature CA, O=Mock CA, C=TR");
        response.setCertificateSubject("CN=Mock User, SERIALNUMBER=12345678901, C=TR");
        response.setCertificateValidFrom(LocalDateTime.now().minusYears(1));
        response.setCertificateValidTo(LocalDateTime.now().plusYears(1));
        return response;
    }
    
    /**
     * Test ortamı için sahte doğrulama yanıtı oluşturur.
     * 
     * @return Sahte doğrulama yanıtı
     */
    private VerificationResponse createMockVerificationResponse() {
        VerificationResponse response = new VerificationResponse();
        response.setValid(true);
        response.setResultMessage("Mock verification successful");
        return response;
    }
} 
package com.banking.core.integration;

import com.banking.core.dtos.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CentralBankClient {

    private final RestTemplate restTemplate;

    @Value("${central.bank.api.url}")
    private String apiUrl;

    @Value("${central.bank.api.key}")
    private String apiKey;

    public CentralBankClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CentralBankReportResponse getCustomerReport(String identityNumber) {
        log.info("Getting Central Bank report for identity number: {}", identityNumber);

        try {
            String url = apiUrl + "/customer-report";
            HttpEntity<Map<String, String>> request = createRequest(identityNumber);
            
            return restTemplate.postForObject(url, request, CentralBankReportResponse.class);
        } catch (Exception e) {
            log.error("Error getting Central Bank report: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting Central Bank report", e);
        }
    }

    public CreditHistorySummaryResponse getCreditHistorySummary(String identityNumber) {
        log.info("Getting credit history summary for identity number: {}", identityNumber);

        try {
            String url = apiUrl + "/credit-history";
            HttpEntity<Map<String, String>> request = createRequest(identityNumber);
            
            return restTemplate.postForObject(url, request, CreditHistorySummaryResponse.class);
        } catch (Exception e) {
            log.error("Error getting credit history summary: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting credit history summary", e);
        }
    }

    public BlacklistStatusResponse getBlacklistStatus(String identityNumber) {
        log.info("Checking blacklist status for identity number: {}", identityNumber);

        try {
            String url = apiUrl + "/blacklist-status";
            HttpEntity<Map<String, String>> request = createRequest(identityNumber);
            
            return restTemplate.postForObject(url, request, BlacklistStatusResponse.class);
        } catch (Exception e) {
            log.error("Error checking blacklist status: {}", e.getMessage(), e);
            throw new RuntimeException("Error checking blacklist status", e);
        }
    }

    public List<PaymentDelayResponse> getPaymentDelays(String identityNumber) {
        log.info("Getting payment delays for identity number: {}", identityNumber);

        try {
            String url = apiUrl + "/payment-delays";
            HttpEntity<Map<String, String>> request = createRequest(identityNumber);
            
            PaymentDelayResponse[] response = restTemplate.postForObject(url, request, PaymentDelayResponse[].class);
            return response != null ? List.of(response) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error getting payment delays: {}", e.getMessage(), e);
            throw new RuntimeException("Error getting payment delays", e);
        }
    }

    public RiskScoreResponse calculateRiskScore(String identityNumber) {
        log.info("Calculating risk score for identity number: {}", identityNumber);

        try {
            String url = apiUrl + "/risk-score";
            HttpEntity<Map<String, String>> request = createRequest(identityNumber);
            
            return restTemplate.postForObject(url, request, RiskScoreResponse.class);
        } catch (Exception e) {
            log.error("Error calculating risk score: {}", e.getMessage(), e);
            throw new RuntimeException("Error calculating risk score", e);
        }
    }

    private HttpEntity<Map<String, String>> createRequest(String identityNumber) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        Map<String, String> body = new HashMap<>();
        body.put("identityNumber", identityNumber);
        body.put("requestDate", LocalDateTime.now().toString());

        return new HttpEntity<>(body, headers);
    }
} 
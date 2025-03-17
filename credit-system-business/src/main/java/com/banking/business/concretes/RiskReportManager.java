package com.banking.business.concretes;

import com.banking.business.abstracts.RiskReportService;
import com.banking.core.dtos.response.*;
import com.banking.core.cache.CacheService;
import com.banking.core.integration.CentralBankClient;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.SuccessDataResult;
import com.banking.entities.Customer;
import com.banking.entities.RiskReport;
import com.banking.repositories.abstracts.CustomerRepository;
import com.banking.repositories.abstracts.RiskReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskReportManager implements RiskReportService {

    private final CentralBankClient centralBankClient;
    private final CustomerRepository<Customer> customerRepository;
    private final CacheService cacheService;
    private final RiskReportRepository riskReportRepository;

    @Value("${central.bank.cache.duration:30}")
    private long cacheDurationMinutes;

    @Override
    public CentralBankReportResponse getCentralBankReport(Long customerId, String identityNumber) {
        log.info("Getting Central Bank report for customer: {}", customerId);

        // Check cache first
        Optional<CentralBankReportResponse> cachedReport = getCachedReportData(customerId);
        if (cachedReport.isPresent() && isCachedDataValid(customerId)) {
            log.info("Returning cached Central Bank report for customer: {}", customerId);
            return cachedReport.get();
        }

        // Get customer details
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        // Get report from Central Bank
        CentralBankReportResponse report = centralBankClient.getCustomerReport(identityNumber);
        report.setCustomerId(customerId);
        report.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        report.setReportDate(LocalDateTime.now());

        // Cache the report
        cacheReportData(customerId, report);

        return report;
    }

    @Override
    public CreditHistorySummaryResponse getCreditHistorySummary(Long customerId) {
        log.info("Getting credit history summary for customer: {}", customerId);

        // Get customer details
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        return centralBankClient.getCreditHistorySummary(customer.getIdentityNumber());
    }

    @Override
    public BlacklistStatusResponse getBlacklistStatus(Long customerId) {
        log.info("Checking blacklist status for customer: {}", customerId);

        // Get customer details
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        return centralBankClient.getBlacklistStatus(customer.getIdentityNumber());
    }

    @Override
    public List<PaymentDelayResponse> getPaymentDelays(Long customerId) {
        log.info("Getting payment delays for customer: {}", customerId);

        // Get customer details
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        return centralBankClient.getPaymentDelays(customer.getIdentityNumber());
    }

    @Override
    public RiskScoreResponse calculateRiskScore(Long customerId) {
        log.info("Calculating risk score for customer: {}", customerId);

        // Get customer details
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        return centralBankClient.calculateRiskScore(customer.getIdentityNumber());
    }

    @Override
    public boolean cacheReportData(Long customerId, CentralBankReportResponse report) {
        log.info("Caching Central Bank report for customer: {}", customerId);

        String cacheKey = "central_bank_report:" + customerId;
        report.setCached(true);
        report.setCacheDate(LocalDateTime.now());

        return cacheService.put(cacheKey, report, Duration.ofMinutes(cacheDurationMinutes));
    }

    @Override
    public Optional<CentralBankReportResponse> getCachedReportData(Long customerId) {
        log.info("Getting cached Central Bank report for customer: {}", customerId);

        String cacheKey = "central_bank_report:" + customerId;
        return cacheService.get(cacheKey, CentralBankReportResponse.class);
    }

    @Override
    public boolean isCachedDataValid(Long customerId) {
        log.info("Checking if cached Central Bank report is valid for customer: {}", customerId);

        Optional<CentralBankReportResponse> cachedReport = getCachedReportData(customerId);
        if (cachedReport.isEmpty()) {
            return false;
        }

        LocalDateTime cacheDate = cachedReport.get().getCacheDate();
        LocalDateTime expirationDate = cacheDate.plusMinutes(cacheDurationMinutes);
        return LocalDateTime.now().isBefore(expirationDate);
    }

    @Override
    public boolean clearCachedData(Long customerId) {
        log.info("Clearing cached Central Bank report for customer: {}", customerId);

        String cacheKey = "central_bank_report:" + customerId;
        return cacheService.remove(cacheKey);
    }

    @Override
    public DataResult<RiskReport> getReport(Long customerId) {
        log.info("Getting risk report for customer: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
                
        CentralBankReportResponse report = getCentralBankReport(customerId, customer.getIdentityNumber());
        RiskScoreResponse riskScore = calculateRiskScore(customerId);
        BlacklistStatusResponse blacklistStatus = getBlacklistStatus(customerId);
        
        RiskReport riskReport = new RiskReport();
        riskReport.setCustomerId(customerId);
        riskReport.setReportDate(LocalDateTime.now());
        riskReport.setRiskScore(riskScore.getScore());
        riskReport.setBlacklistStatus(blacklistStatus.isBlacklisted());
        riskReport.setReportSource("CENTRAL_BANK");
        riskReport.setReportReference(report.getReportId());
        
        return new SuccessDataResult<>(riskReport, "Risk report generated successfully");
    }

    @Override
    public DataResult<Page<RiskReport>> getHistoricalReports(Long customerId, LocalDateTime startDate, 
            LocalDateTime endDate, Pageable pageable) {
        log.info("Getting historical risk reports for customer: {} between {} and {}", 
                customerId, startDate, endDate);
        
        Page<RiskReport> reports = riskReportRepository.findByCustomerIdAndReportDateBetween(
                customerId, startDate, endDate, pageable);
                
        return new SuccessDataResult<>(reports, "Historical reports retrieved successfully");
    }

    @Override
    public DataResult<List<RiskReport>> getHighRiskCustomers(Double riskScoreThreshold) {
        log.info("Getting high risk customers with threshold: {}", riskScoreThreshold);
        
        List<RiskReport> highRiskReports = riskReportRepository.findByRiskScoreGreaterThanEqual(riskScoreThreshold);
        return new SuccessDataResult<>(highRiskReports, "High risk customers retrieved successfully");
    }

    @Override
    public DataResult<List<RiskReport>> getBlacklistedCustomers() {
        log.info("Getting blacklisted customers");
        
        List<RiskReport> blacklistedReports = riskReportRepository.findByBlacklistStatusIsTrue();
        return new SuccessDataResult<>(blacklistedReports, "Blacklisted customers retrieved successfully");
    }
} 
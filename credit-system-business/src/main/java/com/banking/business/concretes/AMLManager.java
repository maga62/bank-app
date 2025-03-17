package com.banking.business.concretes;

import com.banking.business.abstracts.AMLService;
import com.banking.business.dtos.request.TransactionMonitorRequest;
import com.banking.business.dtos.response.RiskAssessmentResponse;
import com.banking.business.dtos.response.SuspiciousTransactionResponse;
import com.banking.business.enums.RiskLevel;
import com.banking.business.fraud.FraudDetectionRule;
import com.banking.entities.SuspiciousTransaction;
import com.banking.repositories.abstracts.SuspiciousTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AMLManager implements AMLService {

    private final SuspiciousTransactionRepository suspiciousTransactionRepository;
    private final List<FraudDetectionRule> fraudDetectionRules;
    
    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("10000.00");

    @Override
    public RiskAssessmentResponse monitorTransaction(TransactionMonitorRequest request) {
        // Apply all fraud detection rules
        RiskLevel highestRiskLevel = RiskLevel.LOW;
        String riskReason = "";
        
        for (FraudDetectionRule rule : fraudDetectionRules) {
            if (rule.isApplicable(request)) {
                RiskLevel riskLevel = rule.evaluateRisk(request);
                if (riskLevel.ordinal() > highestRiskLevel.ordinal()) {
                    highestRiskLevel = riskLevel;
                    riskReason = rule.getRiskReason();
                }
            }
        }
        
        // Check for high amount transactions
        if (request.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) >= 0) {
            highestRiskLevel = RiskLevel.HIGH;
            riskReason = "Transaction amount exceeds threshold of " + HIGH_AMOUNT_THRESHOLD;
        }
        
        // If risk is medium or high, save as suspicious transaction
        if (highestRiskLevel.ordinal() >= RiskLevel.MEDIUM.ordinal()) {
            SuspiciousTransaction transaction = new SuspiciousTransaction();
            transaction.setCustomerId(request.getCustomerId());
            transaction.setTransactionId(request.getTransactionId());
            transaction.setAmount(request.getAmount());
            transaction.setTransactionType(SuspiciousTransaction.TransactionType.valueOf(request.getTransactionType()));
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setDetectionDate(LocalDateTime.now());
            transaction.setRiskScore(calculateRiskScore(highestRiskLevel));
            transaction.setRiskLevel(SuspiciousTransaction.RiskLevel.valueOf(highestRiskLevel.name()));
            transaction.setRiskReason(riskReason);
            transaction.setDetectionRule("Amount threshold rule");
            transaction.setDescription(riskReason);
            transaction.setStatus(SuspiciousTransaction.Status.PENDING_REVIEW);
            transaction.setIpAddress(request.getIpAddress());
            transaction.setDeviceId(request.getDeviceId());
            transaction.setLocation(request.getLocation());
            
            suspiciousTransactionRepository.save(transaction);
        }
        
        return RiskAssessmentResponse.builder()
                .transactionId(request.getTransactionId())
                .customerId(request.getCustomerId())
                .riskLevel(highestRiskLevel.toString())
                .riskReason(riskReason)
                .assessmentDate(LocalDateTime.now())
                .build();
    }

    @Override
    public RiskAssessmentResponse assessCustomerRisk(Long customerId) {
        // Count suspicious transactions by risk level
        long highRiskCount = suspiciousTransactionRepository.findByCustomerIdAndRiskLevel(customerId, SuspiciousTransaction.RiskLevel.HIGH).size();
        long mediumRiskCount = suspiciousTransactionRepository.findByCustomerIdAndRiskLevel(customerId, SuspiciousTransaction.RiskLevel.MEDIUM).size();
        
        // Determine overall risk level
        RiskLevel overallRisk;
        String riskReason;
        
        if (highRiskCount > 2) {
            overallRisk = RiskLevel.HIGH;
            riskReason = "Customer has " + highRiskCount + " high-risk transactions";
        } else if (highRiskCount > 0 || mediumRiskCount > 3) {
            overallRisk = RiskLevel.MEDIUM;
            riskReason = "Customer has " + highRiskCount + " high-risk and " + mediumRiskCount + " medium-risk transactions";
        } else {
            overallRisk = RiskLevel.LOW;
            riskReason = "Customer has low risk profile";
        }
        
        return RiskAssessmentResponse.builder()
                .customerId(customerId)
                .riskLevel(overallRisk.toString())
                .riskReason(riskReason)
                .assessmentDate(LocalDateTime.now())
                .build();
    }

    @Override
    public List<SuspiciousTransactionResponse> getSuspiciousTransactionsByCustomerId(Long customerId) {
        List<SuspiciousTransaction> transactions = suspiciousTransactionRepository.findByCustomerId(customerId);
        return mapToResponseList(transactions);
    }

    @Override
    public List<SuspiciousTransactionResponse> getSuspiciousTransactionsByRiskLevel(String riskLevel) {
        SuspiciousTransaction.RiskLevel level = SuspiciousTransaction.RiskLevel.valueOf(riskLevel.toUpperCase());
        List<SuspiciousTransaction> transactions = suspiciousTransactionRepository.findByRiskLevel(level);
        return mapToResponseList(transactions);
    }

    @Override
    public List<SuspiciousTransactionResponse> getTransactionsAboveThreshold(BigDecimal threshold) {
        List<SuspiciousTransaction> transactions = suspiciousTransactionRepository.findByAmountGreaterThanEqual(threshold);
        return mapToResponseList(transactions);
    }

    @Override
    @Transactional
    public boolean reportSuspiciousTransaction(Long transactionId, String reportNotes) {
        SuspiciousTransaction transaction = suspiciousTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Suspicious transaction not found with id: " + transactionId));
        
        transaction.setStatus(SuspiciousTransaction.Status.REPORTED);
        transaction.setReportNotes(reportNotes);
        transaction.setReportDate(LocalDateTime.now());
        
        suspiciousTransactionRepository.save(transaction);
        return true;
    }

    @Override
    @Transactional
    public boolean resolveSuspiciousTransaction(Long transactionId, String resolutionNotes, boolean isFalsePositive) {
        SuspiciousTransaction transaction = suspiciousTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Suspicious transaction not found with id: " + transactionId));
        
        transaction.setStatus(isFalsePositive ? SuspiciousTransaction.Status.FALSE_POSITIVE : SuspiciousTransaction.Status.CONFIRMED_FRAUD);
        transaction.setResolutionNotes(resolutionNotes);
        transaction.setResolutionDate(LocalDateTime.now());
        transaction.setFalsePositive(isFalsePositive);
        
        suspiciousTransactionRepository.save(transaction);
        return true;
    }

    @Override
    public boolean trainMachineLearningModel() {
        // In a real implementation, this would train the ML model using historical data
        // For this example, we'll just return true
        return true;
    }
    
    // Helper methods
    
    private List<SuspiciousTransactionResponse> mapToResponseList(List<SuspiciousTransaction> transactions) {
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private SuspiciousTransactionResponse mapToResponse(SuspiciousTransaction transaction) {
        return SuspiciousTransactionResponse.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomerId())
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType().toString())
                .transactionDate(transaction.getTransactionDate())
                .riskLevel(transaction.getRiskLevel().toString())
                .riskReason(transaction.getRiskReason())
                .status(transaction.getStatus().toString())
                .reportDate(transaction.getReportDate())
                .resolutionDate(transaction.getResolutionDate())
                .isFalsePositive(transaction.isFalsePositive())
                .build();
    }

    private int calculateRiskScore(RiskLevel riskLevel) {
        switch (riskLevel) {
            case LOW:
                return 25;
            case MEDIUM:
                return 50;
            case HIGH:
                return 75;
            default:
                return 0;
        }
    }
} 
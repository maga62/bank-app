package com.banking.business.concretes;

import com.banking.business.abstracts.CreditHistoryService;
import com.banking.business.dtos.responses.CreditHistoryResponse;
import com.banking.business.dtos.responses.CreditHistorySummaryResponse;
import com.banking.business.dtos.responses.PaymentPerformanceResponse;
import com.banking.core.utilities.mappers.ModelMapperService;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.SuccessDataResult;
import com.banking.entities.CreditHistory;
import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.CreditType;
import com.banking.repositories.abstracts.CreditHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CreditHistoryManager implements CreditHistoryService {

    private final CreditHistoryRepository creditHistoryRepository;
    private final ModelMapperService modelMapperService;

    @Override
    public DataResult<CreditHistoryResponse> getById(Long id) {
        CreditHistory creditHistory = creditHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credit history not found with id: " + id));
        
        CreditHistoryResponse response = modelMapperService.forResponse()
                .map(creditHistory, CreditHistoryResponse.class);
        
        return new SuccessDataResult<>(response, "Credit history retrieved successfully");
    }

    @Override
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerId(Long customerId, Pageable pageable) {
        Page<CreditHistory> creditHistories = creditHistoryRepository.findByCustomerId(customerId, pageable);
        
        Page<CreditHistoryResponse> response = creditHistories.map(
                creditHistory -> modelMapperService.forResponse()
                        .map(creditHistory, CreditHistoryResponse.class));
        
        return new SuccessDataResult<>(response, "Credit histories retrieved successfully");
    }

    @Override
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndStatus(
            Long customerId, CreditHistoryStatus status, Pageable pageable) {
        
        // Get all credit histories for the customer with the specified status
        List<CreditHistory> allCreditHistories = creditHistoryRepository.findByCustomerIdAndStatus(customerId, status);
        
        // Create a new Page from the filtered list
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCreditHistories.size());
        
        List<CreditHistory> pageContent = start < end ? allCreditHistories.subList(start, end) : List.of();
        Page<CreditHistory> creditHistoriesPage = new PageImpl<>(pageContent, pageable, allCreditHistories.size());
        
        Page<CreditHistoryResponse> response = creditHistoriesPage.map(
                creditHistory -> modelMapperService.forResponse()
                        .map(creditHistory, CreditHistoryResponse.class));
        
        return new SuccessDataResult<>(response, "Credit histories retrieved successfully");
    }

    @Override
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndCreditType(
            Long customerId, CreditType creditType, Pageable pageable) {
        
        // Get all credit histories for the customer
        List<CreditHistory> allCreditHistories = creditHistoryRepository.findAllByCustomerId(customerId);
        
        // Filter by credit type
        List<CreditHistory> filteredCreditHistories = allCreditHistories.stream()
                .filter(creditHistory -> creditHistory.getCreditType() == creditType)
                .collect(Collectors.toList());
        
        // Create a new Page from the filtered list
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredCreditHistories.size());
        
        List<CreditHistory> pageContent = start < end ? filteredCreditHistories.subList(start, end) : List.of();
        Page<CreditHistory> creditHistoriesPage = new PageImpl<>(pageContent, pageable, filteredCreditHistories.size());
        
        Page<CreditHistoryResponse> response = creditHistoriesPage.map(
                creditHistory -> modelMapperService.forResponse()
                        .map(creditHistory, CreditHistoryResponse.class));
        
        return new SuccessDataResult<>(response, "Credit histories retrieved successfully");
    }

    @Override
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndStartDateBetween(
            Long customerId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        
        // Get all credit histories for the customer
        List<CreditHistory> allCreditHistories = creditHistoryRepository.findAllByCustomerId(customerId);
        
        // Filter by start date between
        List<CreditHistory> filteredCreditHistories = allCreditHistories.stream()
                .filter(creditHistory -> 
                        creditHistory.getStartDate().isAfter(startDate.minusDays(1)) && 
                        creditHistory.getStartDate().isBefore(endDate.plusDays(1)))
                .collect(Collectors.toList());
        
        // Create a new Page from the filtered list
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredCreditHistories.size());
        
        List<CreditHistory> pageContent = start < end ? filteredCreditHistories.subList(start, end) : List.of();
        Page<CreditHistory> creditHistoriesPage = new PageImpl<>(pageContent, pageable, filteredCreditHistories.size());
        
        Page<CreditHistoryResponse> response = creditHistoriesPage.map(
                creditHistory -> modelMapperService.forResponse()
                        .map(creditHistory, CreditHistoryResponse.class));
        
        return new SuccessDataResult<>(response, "Credit histories retrieved successfully");
    }

    @Override
    public DataResult<Page<CreditHistoryResponse>> getActiveByCustomerId(Long customerId, Pageable pageable) {
        // Get all credit histories for the customer with ACTIVE status
        List<CreditHistory> allCreditHistories = creditHistoryRepository.findByCustomerIdAndStatus(customerId, CreditHistoryStatus.ACTIVE);
        
        // Create a new Page from the filtered list
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCreditHistories.size());
        
        List<CreditHistory> pageContent = start < end ? allCreditHistories.subList(start, end) : List.of();
        Page<CreditHistory> creditHistoriesPage = new PageImpl<>(pageContent, pageable, allCreditHistories.size());
        
        Page<CreditHistoryResponse> response = creditHistoriesPage.map(
                creditHistory -> modelMapperService.forResponse()
                        .map(creditHistory, CreditHistoryResponse.class));
        
        return new SuccessDataResult<>(response, "Active credit histories retrieved successfully");
    }

    @Override
    public DataResult<CreditHistorySummaryResponse> getSummaryByCustomerId(Long customerId) {
        List<CreditHistory> creditHistories = creditHistoryRepository.findAllByCustomerId(customerId);
        
        // Calculate summary statistics
        int totalCredits = creditHistories.size();
        int activeCredits = (int) creditHistories.stream()
                .filter(ch -> ch.getStatus() == CreditHistoryStatus.ACTIVE)
                .count();
        int completedCredits = (int) creditHistories.stream()
                .filter(ch -> ch.getStatus() == CreditHistoryStatus.PAID_OFF)
                .count();
        int defaultedCredits = (int) creditHistories.stream()
                .filter(ch -> ch.getStatus() == CreditHistoryStatus.DEFAULT || 
                        ch.getStatus() == CreditHistoryStatus.CHARGED_OFF)
                .count();
        
        BigDecimal totalCreditAmount = creditHistories.stream()
                .map(CreditHistory::getOriginalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOutstandingAmount = creditHistoryRepository.calculateTotalOutstandingAmountByCustomer(customerId);
        
        BigDecimal totalPaidAmount = totalCreditAmount.subtract(
                totalOutstandingAmount != null ? totalOutstandingAmount : BigDecimal.ZERO);
        
        // Calculate credit type distribution
        Map<String, Integer> creditTypeDistribution = creditHistories.stream()
                .collect(Collectors.groupingBy(
                        ch -> ch.getCreditType().toString(),
                        Collectors.summingInt(ch -> 1)));
        
        // Calculate average interest rate
        Double averageInterestRate = creditHistories.stream()
                .filter(ch -> ch.getInterestRate() != null)
                .mapToDouble(ch -> ch.getInterestRate().doubleValue())
                .average()
                .orElse(0.0);
        
        // Calculate average term
        Integer averageTerm = (int) creditHistories.stream()
                .mapToInt(CreditHistory::getTermMonths)
                .average()
                .orElse(0.0);
        
        // Check if there are any overdue payments
        Boolean hasOverduePayments = creditHistories.stream()
                .anyMatch(ch -> ch.getStatus() == CreditHistoryStatus.DELINQUENT);
        
        // Calculate a simple credit score (just for demonstration)
        Integer creditScore = calculateCreditScore(creditHistories);
        
        CreditHistorySummaryResponse response = CreditHistorySummaryResponse.builder()
                .customerId(customerId)
                .totalCredits(totalCredits)
                .activeCredits(activeCredits)
                .completedCredits(completedCredits)
                .defaultedCredits(defaultedCredits)
                .totalCreditAmount(totalCreditAmount)
                .totalOutstandingAmount(totalOutstandingAmount != null ? totalOutstandingAmount : BigDecimal.ZERO)
                .totalPaidAmount(totalPaidAmount)
                .creditTypeDistribution(creditTypeDistribution)
                .averageInterestRate(averageInterestRate)
                .averageTerm(averageTerm)
                .hasOverduePayments(hasOverduePayments)
                .creditScore(creditScore)
                .build();
        
        return new SuccessDataResult<>(response, "Credit history summary retrieved successfully");
    }

    @Override
    public DataResult<PaymentPerformanceResponse> getPaymentPerformanceByCustomerId(Long customerId) {
        List<CreditHistory> creditHistories = creditHistoryRepository.findAllByCustomerId(customerId);
        
        // Calculate payment performance statistics
        int totalPayments = creditHistories.stream()
                .mapToInt(ch -> ch.getTotalPaymentsMade() != null ? ch.getTotalPaymentsMade() : 0)
                .sum();
        
        int onTimePayments = creditHistories.stream()
                .mapToInt(ch -> ch.getOnTimePayments() != null ? ch.getOnTimePayments() : 0)
                .sum();
        
        int latePayments = creditHistories.stream()
                .mapToInt(ch -> ch.getLatePayments() != null ? ch.getLatePayments() : 0)
                .sum();
        
        int missedPayments = creditHistories.stream()
                .mapToInt(ch -> ch.getMissedPayments() != null ? ch.getMissedPayments() : 0)
                .sum();
        
        double onTimePaymentPercentage = totalPayments > 0 
                ? (double) onTimePayments / totalPayments * 100 
                : 0.0;
        
        // Calculate average days late
        int averageDaysLate = (int) creditHistories.stream()
                .filter(ch -> ch.getAverageDaysLate() != null)
                .mapToDouble(CreditHistory::getAverageDaysLate)
                .average()
                .orElse(0.0);
        
        // Calculate total paid amount (approximation)
        BigDecimal totalPaidAmount = creditHistories.stream()
                .map(ch -> {
                    if (ch.getOriginalAmount() != null && ch.getOutstandingAmount() != null) {
                        return ch.getOriginalAmount().subtract(ch.getOutstandingAmount());
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total penalty amount
        BigDecimal totalPenaltyAmount = creditHistories.stream()
                .map(ch -> ch.getTotalLateFees() != null ? ch.getTotalLateFees() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Create a simple monthly payment trend (just for demonstration)
        Map<String, Integer> monthlyPaymentTrend = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyPaymentTrend.put("Month " + i, (int) (Math.random() * 10));
        }
        
        // Calculate payment reliability rating
        String paymentReliabilityRating = calculatePaymentReliabilityRating(onTimePaymentPercentage);
        
        // Calculate consecutive on-time payments and longest streak (simplified)
        int consecutiveOnTimePayments = 5; // Placeholder value
        int longestStreak = 10; // Placeholder value
        
        PaymentPerformanceResponse response = PaymentPerformanceResponse.builder()
                .customerId(customerId)
                .totalPayments(totalPayments)
                .onTimePayments(onTimePayments)
                .latePayments(latePayments)
                .missedPayments(missedPayments)
                .onTimePaymentPercentage(onTimePaymentPercentage)
                .averageDaysLate(averageDaysLate)
                .totalPaidAmount(totalPaidAmount)
                .totalPenaltyAmount(totalPenaltyAmount)
                .monthlyPaymentTrend(monthlyPaymentTrend)
                .paymentReliabilityRating(paymentReliabilityRating)
                .consecutiveOnTimePayments(consecutiveOnTimePayments)
                .longestStreak(longestStreak)
                .build();
        
        return new SuccessDataResult<>(response, "Payment performance retrieved successfully");
    }

    @Override
    public DataResult<BigDecimal> getTotalOutstandingAmountByCustomerId(Long customerId) {
        BigDecimal totalOutstandingAmount = creditHistoryRepository.calculateTotalOutstandingAmountByCustomer(customerId);
        
        if (totalOutstandingAmount == null) {
            totalOutstandingAmount = BigDecimal.ZERO;
        }
        
        return new SuccessDataResult<>(totalOutstandingAmount, "Total outstanding amount retrieved successfully");
    }
    
    // Helper methods
    
    private Integer calculateCreditScore(List<CreditHistory> creditHistories) {
        // Base score
        int score = 650;
        
        // Adjust based on payment history
        int totalPayments = creditHistories.stream()
                .mapToInt(ch -> ch.getTotalPaymentsMade() != null ? ch.getTotalPaymentsMade() : 0)
                .sum();
        
        int onTimePayments = creditHistories.stream()
                .mapToInt(ch -> ch.getOnTimePayments() != null ? ch.getOnTimePayments() : 0)
                .sum();
        
        if (totalPayments > 0) {
            double onTimeRatio = (double) onTimePayments / totalPayments;
            score += (int) (onTimeRatio * 100);
        }
        
        // Adjust based on credit utilization
        BigDecimal totalOriginal = creditHistories.stream()
                .map(ch -> ch.getOriginalAmount() != null ? ch.getOriginalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOutstanding = creditHistories.stream()
                .map(ch -> ch.getOutstandingAmount() != null ? ch.getOutstandingAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalOriginal.compareTo(BigDecimal.ZERO) > 0) {
            double utilizationRatio = totalOutstanding.divide(totalOriginal, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (utilizationRatio < 0.3) {
                score += 50;
            } else if (utilizationRatio < 0.5) {
                score += 25;
            } else if (utilizationRatio > 0.8) {
                score -= 50;
            }
        }
        
        // Adjust based on delinquencies
        long delinquentCredits = creditHistories.stream()
                .filter(ch -> ch.getStatus() == CreditHistoryStatus.DELINQUENT || 
                        ch.getStatus() == CreditHistoryStatus.DEFAULT || 
                        ch.getStatus() == CreditHistoryStatus.COLLECTION)
                .count();
        
        score -= delinquentCredits * 50;
        
        // Ensure score is within reasonable bounds
        return Math.max(300, Math.min(850, score));
    }
    
    private String calculatePaymentReliabilityRating(double onTimePaymentPercentage) {
        if (onTimePaymentPercentage >= 95) {
            return "EXCELLENT";
        } else if (onTimePaymentPercentage >= 85) {
            return "GOOD";
        } else if (onTimePaymentPercentage >= 70) {
            return "FAIR";
        } else {
            return "POOR";
        }
    }
} 
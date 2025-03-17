package com.banking.business.abstracts;

import com.banking.business.dtos.responses.CreditHistoryResponse;
import com.banking.business.dtos.responses.CreditHistorySummaryResponse;
import com.banking.business.dtos.responses.PaymentPerformanceResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.CreditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CreditHistoryService {
    DataResult<CreditHistoryResponse> getById(Long id);
    
    DataResult<Page<CreditHistoryResponse>> getAllByCustomerId(Long customerId, Pageable pageable);
    
    DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndStatus(Long customerId, CreditHistoryStatus status, Pageable pageable);
    
    DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndCreditType(Long customerId, CreditType creditType, Pageable pageable);
    
    DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndStartDateBetween(Long customerId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    DataResult<Page<CreditHistoryResponse>> getActiveByCustomerId(Long customerId, Pageable pageable);
    
    DataResult<CreditHistorySummaryResponse> getSummaryByCustomerId(Long customerId);
    
    DataResult<PaymentPerformanceResponse> getPaymentPerformanceByCustomerId(Long customerId);
    
    DataResult<BigDecimal> getTotalOutstandingAmountByCustomerId(Long customerId);
} 
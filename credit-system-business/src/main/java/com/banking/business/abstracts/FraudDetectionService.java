package com.banking.business.abstracts;

import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.entities.SuspiciousTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface FraudDetectionService {
    Result checkTransaction(String transactionId);
    DataResult<Page<SuspiciousTransaction>> getFlaggedTransactions(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    DataResult<List<SuspiciousTransaction>> getCustomerTransactions(Long customerId);
    Result markAsResolved(Long transactionId, String resolution, String resolvedBy);
} 
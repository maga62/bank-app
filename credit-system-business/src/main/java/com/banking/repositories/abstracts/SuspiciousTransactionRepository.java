package com.banking.repositories.abstracts;

import com.banking.entities.SuspiciousTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuspiciousTransactionRepository extends JpaRepository<SuspiciousTransaction, Long> {

    List<SuspiciousTransaction> findByCustomerIdAndStatus(Long customerId, SuspiciousTransaction.Status status);

    List<SuspiciousTransaction> findByCustomerIdAndRiskLevel(Long customerId, SuspiciousTransaction.RiskLevel riskLevel);

    List<SuspiciousTransaction> findByCustomerId(Long customerId);

    List<SuspiciousTransaction> findByRiskLevel(SuspiciousTransaction.RiskLevel riskLevel);

    List<SuspiciousTransaction> findByCustomerIdAndTransactionTypeAndTransactionDateAfter(
            Long customerId, 
            SuspiciousTransaction.TransactionType transactionType, 
            LocalDateTime startTime);

    /**
     * Belirli bir tutardan yüksek veya eşit şüpheli işlemleri bulur.
     * 
     * @param amount Tutar
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByAmountGreaterThanEqual(BigDecimal amount);

    /**
     * Belirli bir IP adresinden yapılan şüpheli işlemleri bulur.
     * 
     * @param ipAddress IP adresi
     * @return Şüpheli işlem listesi
     */
    List<SuspiciousTransaction> findByIpAddress(String ipAddress);
}
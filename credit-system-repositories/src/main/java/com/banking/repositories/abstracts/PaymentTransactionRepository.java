package com.banking.repositories.abstracts;

import com.banking.entities.PaymentTransaction;
import com.banking.entities.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.loanId = :loanId AND pt.deletedDate IS NULL")
    List<PaymentTransaction> findActiveByLoanId(@Param("loanId") Long loanId);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentStatus = :status AND pt.deletedDate IS NULL")
    List<PaymentTransaction> findActiveByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentDate BETWEEN :startDate AND :endDate AND pt.deletedDate IS NULL")
    List<PaymentTransaction> findActiveByPaymentDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.amountPaid >= :amount AND pt.deletedDate IS NULL")
    List<PaymentTransaction> findActiveByAmountPaidGreaterThanEqual(@Param("amount") BigDecimal amount);
    
    @Query(value = "SELECT * FROM payment_transactions pt " +
            "WHERE pt.payment_status = 'FAILED' AND pt.deleted_date IS NULL " +
            "AND pt.created_at >= :startDate", nativeQuery = true)
    List<PaymentTransaction> findRecentFailedTransactions(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(pt) FROM PaymentTransaction pt " +
            "WHERE pt.paymentStatus = 'FAILED' AND pt.loanId = :loanId AND pt.deletedDate IS NULL")
    long countFailedPaymentsByLoanId(@Param("loanId") Long loanId);
    
    @Query("UPDATE PaymentTransaction pt SET pt.deletedDate = CURRENT_TIMESTAMP WHERE pt.id = :id")
    void softDelete(@Param("id") Long id);
    
    @Query(value = "SELECT pt.* FROM payment_transactions pt " +
            "INNER JOIN loan_agreements la ON pt.loan_id = la.id " +
            "WHERE la.customer_id = :customerId AND pt.deleted_date IS NULL " +
            "ORDER BY pt.payment_date DESC", nativeQuery = true)
    Page<PaymentTransaction> findActiveByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query(value = "SELECT * FROM payment_transactions pt " +
            "WHERE pt.payment_status = 'COMPLETED' AND pt.deleted_date IS NULL " +
            "AND pt.payment_date >= :startDate " +
            "GROUP BY pt.loan_id " +
            "HAVING COUNT(*) >= :minCount", nativeQuery = true)
    List<PaymentTransaction> findLoansWithConsistentPayments(
            @Param("startDate") LocalDateTime startDate, 
            @Param("minCount") int minCount);
} 
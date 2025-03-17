package com.banking.repositories.abstracts;

import com.banking.entities.CreditHistory;
import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.CreditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for CreditHistory entity.
 * Provides methods for accessing credit history data.
 */
@Repository
public interface CreditHistoryRepository extends GenericRepository<CreditHistory, Long> {
    
    /**
     * Find all credit histories by customer ID.
     *
     * @param customerId the customer ID
     * @return list of credit histories
     */
    List<CreditHistory> findByCustomerId(Long customerId);
    
    /**
     * Find all credit histories by customer ID (alias for findByCustomerId).
     *
     * @param customerId the customer ID
     * @return list of credit histories
     */
    @Query("SELECT ch FROM CreditHistory ch WHERE ch.customer.id = :customerId")
    List<CreditHistory> findAllByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * Find all credit histories by credit type.
     *
     * @param creditType the credit type
     * @return list of credit histories
     */
    List<CreditHistory> findByCreditType(CreditType creditType);
    
    /**
     * Find all credit histories by credit provider.
     *
     * @param creditProvider the credit provider
     * @return list of credit histories
     */
    List<CreditHistory> findByCreditProvider(String creditProvider);
    
    /**
     * Find all credit histories by original amount greater than.
     *
     * @param originalAmount the original amount
     * @return list of credit histories
     */
    List<CreditHistory> findByOriginalAmountGreaterThan(BigDecimal originalAmount);
    
    /**
     * Find all credit histories by outstanding amount greater than.
     *
     * @param outstandingAmount the outstanding amount
     * @return list of credit histories
     */
    List<CreditHistory> findByOutstandingAmountGreaterThan(BigDecimal outstandingAmount);
    
    /**
     * Find all credit histories by start date.
     *
     * @param startDate the start date
     * @return list of credit histories
     */
    List<CreditHistory> findByStartDate(LocalDate startDate);
    
    /**
     * Find all credit histories by end date.
     *
     * @param endDate the end date
     * @return list of credit histories
     */
    List<CreditHistory> findByEndDate(LocalDate endDate);
    
    /**
     * Find all credit histories by start date between.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of credit histories
     */
    List<CreditHistory> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all credit histories by end date between.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of credit histories
     */
    List<CreditHistory> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all credit histories by term months.
     *
     * @param termMonths the term months
     * @return list of credit histories
     */
    List<CreditHistory> findByTermMonths(Integer termMonths);
    
    /**
     * Find all credit histories by interest rate greater than.
     *
     * @param interestRate the interest rate
     * @return list of credit histories
     */
    List<CreditHistory> findByInterestRateGreaterThan(BigDecimal interestRate);
    
    /**
     * Find all credit histories by status.
     *
     * @param status the status
     * @return list of credit histories
     */
    List<CreditHistory> findByStatus(CreditHistoryStatus status);
    
    /**
     * Find all credit histories by is closed.
     *
     * @param isClosed the is closed
     * @return list of credit histories
     */
    List<CreditHistory> findByIsClosed(Boolean isClosed);
    
    /**
     * Find all credit histories by closure date.
     *
     * @param closureDate the closure date
     * @return list of credit histories
     */
    List<CreditHistory> findByClosureDate(LocalDate closureDate);
    
    /**
     * Find all credit histories by closure reason.
     *
     * @param closureReason the closure reason
     * @return list of credit histories
     */
    List<CreditHistory> findByClosureReason(String closureReason);
    
    /**
     * Find all credit histories by customer ID and credit type.
     *
     * @param customerId the customer ID
     * @param creditType the credit type
     * @return list of credit histories
     */
    List<CreditHistory> findByCustomerIdAndCreditType(Long customerId, CreditType creditType);
    
    /**
     * Find all credit histories by customer ID and status.
     *
     * @param customerId the customer ID
     * @param status the status
     * @return list of credit histories
     */
    List<CreditHistory> findByCustomerIdAndStatus(Long customerId, CreditHistoryStatus status);
    
    /**
     * Find all credit histories by customer ID with pagination.
     *
     * @param customerId the customer ID
     * @param pageable the pageable
     * @return page of credit histories
     */
    Page<CreditHistory> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Calculate total original amount by customer.
     *
     * @param customerId the customer ID
     * @return the total original amount
     */
    @Query("SELECT SUM(ch.originalAmount) FROM CreditHistory ch WHERE ch.customer.id = :customerId")
    BigDecimal calculateTotalOriginalAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total outstanding amount by customer.
     *
     * @param customerId the customer ID
     * @return the total outstanding amount
     */
    @Query("SELECT SUM(ch.outstandingAmount) FROM CreditHistory ch WHERE ch.customer.id = :customerId AND ch.isClosed = false")
    BigDecimal calculateTotalOutstandingAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate average interest rate by customer.
     *
     * @param customerId the customer ID
     * @return the average interest rate
     */
    @Query("SELECT AVG(ch.interestRate) FROM CreditHistory ch WHERE ch.customer.id = :customerId")
    BigDecimal calculateAverageInterestRateByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total late payments by customer.
     *
     * @param customerId the customer ID
     * @return the total late payments
     */
    @Query("SELECT SUM(ch.latePayments) FROM CreditHistory ch WHERE ch.customer.id = :customerId")
    Integer calculateTotalLatePaymentsByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total missed payments by customer.
     *
     * @param customerId the customer ID
     * @return the total missed payments
     */
    @Query("SELECT SUM(ch.missedPayments) FROM CreditHistory ch WHERE ch.customer.id = :customerId")
    Integer calculateTotalMissedPaymentsByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total late fees by customer.
     *
     * @param customerId the customer ID
     * @return the total late fees
     */
    @Query("SELECT SUM(ch.totalLateFees) FROM CreditHistory ch WHERE ch.customer.id = :customerId")
    BigDecimal calculateTotalLateFeesByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate on-time payment percentage by customer.
     *
     * @param customerId the customer ID
     * @return the on-time payment percentage
     */
    @Query("""
            SELECT 
                CASE 
                    WHEN SUM(ch.totalPaymentsMade) > 0 
                    THEN (SUM(ch.onTimePayments) * 100.0) / SUM(ch.totalPaymentsMade) 
                    ELSE 0 
                END 
            FROM CreditHistory ch 
            WHERE ch.customer.id = :customerId
            """)
    Double calculateOnTimePaymentPercentageByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Find all active credit histories.
     *
     * @param currentDate the current date
     * @return list of credit histories
     */
    @Query("SELECT ch FROM CreditHistory ch WHERE ch.startDate <= :currentDate AND (ch.endDate >= :currentDate OR ch.endDate IS NULL) AND ch.isClosed = false")
    List<CreditHistory> findActiveCreditHistories(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find all credit histories with delinquent status.
     *
     * @return list of credit histories
     */
    @Query("SELECT ch FROM CreditHistory ch WHERE ch.status IN (com.banking.entities.enums.CreditHistoryStatus.DELINQUENT, com.banking.entities.enums.CreditHistoryStatus.DEFAULT, com.banking.entities.enums.CreditHistoryStatus.COLLECTION)")
    List<CreditHistory> findDelinquentCreditHistories();
    
    /**
     * Batch update credit history status.
     *
     * @param oldStatus the old status
     * @param newStatus the new status
     * @return the number of updated records
     */
    @Query("UPDATE CreditHistory ch SET ch.status = :newStatus WHERE ch.status = :oldStatus")
    int batchUpdateCreditHistoryStatus(
            @Param("oldStatus") CreditHistoryStatus oldStatus, 
            @Param("newStatus") CreditHistoryStatus newStatus);
    
    /**
     * Batch update credit scores.
     *
     * @param customerId the customer ID
     * @param scoreIncrease the score increase
     * @return the number of updated records
     */
    @Query(value = """
            UPDATE customers c 
            SET c.credit_score = c.credit_score + :scoreIncrease 
            WHERE c.id = :customerId 
            AND EXISTS (
                SELECT 1 FROM credit_histories ch 
                WHERE ch.customer_id = c.id 
                AND ch.status = 'PAID_OFF'
            )
            """, nativeQuery = true)
    int batchUpdateCreditScores(
            @Param("customerId") Long customerId, 
            @Param("scoreIncrease") Integer scoreIncrease);
} 
package com.banking.repositories.abstracts;

import com.banking.entities.PaymentHistory;
import com.banking.entities.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for PaymentHistory entity.
 * Provides methods for accessing payment history data.
 */
@Repository
public interface PaymentHistoryRepository extends GenericRepository<PaymentHistory, Long> {
    
    /**
     * Find all payment histories by customer ID.
     *
     * @param customerId the customer ID
     * @return list of payment histories
     */
    List<PaymentHistory> findByCustomerId(Long customerId);
    
    /**
     * Find all payment histories by credit application ID.
     *
     * @param creditApplicationId the credit application ID
     * @return list of payment histories
     */
    List<PaymentHistory> findByCreditApplicationId(Long creditApplicationId);
    
    /**
     * Find all payment histories by repayment plan ID.
     *
     * @param repaymentPlanId the repayment plan ID
     * @return list of payment histories
     */
    List<PaymentHistory> findByRepaymentPlanId(Long repaymentPlanId);
    
    /**
     * Find all payment histories by payment status.
     *
     * @param paymentStatus the payment status
     * @return list of payment histories
     */
    List<PaymentHistory> findByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Find all payment histories by due date.
     *
     * @param dueDate the due date
     * @return list of payment histories
     */
    List<PaymentHistory> findByDueDate(LocalDate dueDate);
    
    /**
     * Find all payment histories by payment date.
     *
     * @param paymentDate the payment date
     * @return list of payment histories
     */
    List<PaymentHistory> findByPaymentDate(LocalDate paymentDate);
    
    /**
     * Find all payment histories by customer ID and payment status.
     *
     * @param customerId the customer ID
     * @param paymentStatus the payment status
     * @return list of payment histories
     */
    List<PaymentHistory> findByCustomerIdAndPaymentStatus(Long customerId, PaymentStatus paymentStatus);
    
    /**
     * Find all payment histories by customer ID and due date between.
     *
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of payment histories
     */
    List<PaymentHistory> findByCustomerIdAndDueDateBetween(Long customerId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all payment histories by customer ID and payment status and due date between.
     *
     * @param customerId the customer ID
     * @param paymentStatus the payment status
     * @param startDate the start date
     * @param endDate the end date
     * @return list of payment histories
     */
    List<PaymentHistory> findByCustomerIdAndPaymentStatusAndDueDateBetween(
            Long customerId, PaymentStatus paymentStatus, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all payment histories by customer ID with pagination.
     *
     * @param customerId the customer ID
     * @param pageable the pageable
     * @return page of payment histories
     */
    Page<PaymentHistory> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Find all overdue payments.
     *
     * @param currentDate the current date
     * @return list of payment histories
     */
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.dueDate < :currentDate AND ph.paymentStatus IN (com.banking.entities.enums.PaymentStatus.PENDING, com.banking.entities.enums.PaymentStatus.PARTIALLY_PAID)")
    List<PaymentHistory> findOverduePayments(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find all upcoming payments.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of payment histories
     */
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.dueDate BETWEEN :startDate AND :endDate AND ph.paymentStatus = com.banking.entities.enums.PaymentStatus.PENDING")
    List<PaymentHistory> findUpcomingPayments(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Calculate total amount paid by customer.
     *
     * @param customerId the customer ID
     * @return the total amount paid
     */
    @Query("SELECT SUM(ph.amountPaid) FROM PaymentHistory ph WHERE ph.customer.id = :customerId AND ph.paymentStatus IN (com.banking.entities.enums.PaymentStatus.PAID_ON_TIME, com.banking.entities.enums.PaymentStatus.PAID_LATE, com.banking.entities.enums.PaymentStatus.PARTIALLY_PAID)")
    Double calculateTotalAmountPaidByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total late fees by customer.
     *
     * @param customerId the customer ID
     * @return the total late fees
     */
    @Query("SELECT SUM(ph.lateFee) FROM PaymentHistory ph WHERE ph.customer.id = :customerId AND ph.lateFee IS NOT NULL")
    Double calculateTotalLateFeesByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Count payments by status and customer.
     *
     * @param customerId the customer ID
     * @param paymentStatus the payment status
     * @return the count
     */
    Long countByCustomerIdAndPaymentStatus(Long customerId, PaymentStatus paymentStatus);
    
    /**
     * Find payments with late fees greater than specified amount.
     *
     * @param amount the amount
     * @return list of payment histories
     */
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.lateFee > :amount")
    List<PaymentHistory> findPaymentsWithLateFeeGreaterThan(@Param("amount") Double amount);
    
    /**
     * Batch update payment status for overdue payments.
     *
     * @param currentDate the current date
     * @param newStatus the new status
     * @return the number of updated records
     */
    @Query("UPDATE PaymentHistory ph SET ph.paymentStatus = :newStatus WHERE ph.dueDate < :currentDate AND ph.paymentStatus = com.banking.entities.enums.PaymentStatus.PENDING")
    int batchUpdatePaymentStatusForOverduePayments(
            @Param("currentDate") LocalDate currentDate, 
            @Param("newStatus") PaymentStatus newStatus);
} 
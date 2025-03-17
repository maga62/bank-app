package com.banking.repositories.abstracts;

import com.banking.entities.Expense;
import com.banking.entities.enums.ExpenseFrequency;
import com.banking.entities.enums.ExpenseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Expense entity.
 * Provides methods for accessing expense data.
 */
@Repository
public interface ExpenseRepository extends GenericRepository<Expense, Long> {
    
    /**
     * Find all expenses by customer ID.
     *
     * @param customerId the customer ID
     * @return list of expenses
     */
    List<Expense> findByCustomerId(Long customerId);
    
    /**
     * Find all expenses by expense type.
     *
     * @param expenseType the expense type
     * @return list of expenses
     */
    List<Expense> findByExpenseType(ExpenseType expenseType);
    
    /**
     * Find all expenses by expense frequency.
     *
     * @param expenseFrequency the expense frequency
     * @return list of expenses
     */
    List<Expense> findByExpenseFrequency(ExpenseFrequency expenseFrequency);
    
    /**
     * Find all expenses by amount greater than.
     *
     * @param amount the amount
     * @return list of expenses
     */
    List<Expense> findByAmountGreaterThan(BigDecimal amount);
    
    /**
     * Find all expenses by amount less than.
     *
     * @param amount the amount
     * @return list of expenses
     */
    List<Expense> findByAmountLessThan(BigDecimal amount);
    
    /**
     * Find all expenses by description containing.
     *
     * @param description the description
     * @return list of expenses
     */
    List<Expense> findByDescriptionContaining(String description);
    
    /**
     * Find all expenses by payee.
     *
     * @param payee the payee
     * @return list of expenses
     */
    List<Expense> findByPayee(String payee);
    
    /**
     * Find all expenses by start date.
     *
     * @param startDate the start date
     * @return list of expenses
     */
    List<Expense> findByStartDate(LocalDate startDate);
    
    /**
     * Find all expenses by end date.
     *
     * @param endDate the end date
     * @return list of expenses
     */
    List<Expense> findByEndDate(LocalDate endDate);
    
    /**
     * Find all expenses by is mandatory.
     *
     * @param isMandatory the is mandatory
     * @return list of expenses
     */
    List<Expense> findByIsMandatory(Boolean isMandatory);
    
    /**
     * Find all expenses by is verified.
     *
     * @param isVerified the is verified
     * @return list of expenses
     */
    List<Expense> findByIsVerified(Boolean isVerified);
    
    /**
     * Find all expenses by verification date.
     *
     * @param verificationDate the verification date
     * @return list of expenses
     */
    List<Expense> findByVerificationDate(LocalDate verificationDate);
    
    /**
     * Find all expenses by customer ID and expense type.
     *
     * @param customerId the customer ID
     * @param expenseType the expense type
     * @return list of expenses
     */
    List<Expense> findByCustomerIdAndExpenseType(Long customerId, ExpenseType expenseType);
    
    /**
     * Find all expenses by customer ID and expense frequency.
     *
     * @param customerId the customer ID
     * @param expenseFrequency the expense frequency
     * @return list of expenses
     */
    List<Expense> findByCustomerIdAndExpenseFrequency(Long customerId, ExpenseFrequency expenseFrequency);
    
    /**
     * Find all expenses by customer ID and is mandatory.
     *
     * @param customerId the customer ID
     * @param isMandatory the is mandatory
     * @return list of expenses
     */
    List<Expense> findByCustomerIdAndIsMandatory(Long customerId, Boolean isMandatory);
    
    /**
     * Find all expenses by customer ID with pagination.
     *
     * @param customerId the customer ID
     * @param pageable the pageable
     * @return page of expenses
     */
    Page<Expense> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Calculate total expense amount by customer.
     *
     * @param customerId the customer ID
     * @return the total expense amount
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.customer.id = :customerId")
    BigDecimal calculateTotalExpenseAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total expense amount by customer and expense type.
     *
     * @param customerId the customer ID
     * @param expenseType the expense type
     * @return the total expense amount
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.customer.id = :customerId AND e.expenseType = :expenseType")
    BigDecimal calculateTotalExpenseAmountByCustomerAndExpenseType(
            @Param("customerId") Long customerId, 
            @Param("expenseType") ExpenseType expenseType);
    
    /**
     * Calculate total mandatory expense amount by customer.
     *
     * @param customerId the customer ID
     * @return the total mandatory expense amount
     */
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.customer.id = :customerId AND e.isMandatory = true")
    BigDecimal calculateTotalMandatoryExpenseAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate monthly expense amount by customer.
     *
     * @param customerId the customer ID
     * @return the monthly expense amount
     */
    @Query("""
            SELECT SUM(CASE 
                WHEN e.expenseFrequency = com.banking.entities.enums.ExpenseFrequency.DAILY THEN e.amount * 30
                WHEN e.expenseFrequency = com.banking.entities.enums.ExpenseFrequency.WEEKLY THEN e.amount * 4
                WHEN e.expenseFrequency = com.banking.entities.enums.ExpenseFrequency.BI_WEEKLY THEN e.amount * 2
                WHEN e.expenseFrequency = com.banking.entities.enums.ExpenseFrequency.MONTHLY THEN e.amount
                WHEN e.expenseFrequency = com.banking.entities.enums.ExpenseFrequency.QUARTERLY THEN e.amount / 3
                WHEN e.expenseFrequency = com.banking.entities.enums.ExpenseFrequency.SEMI_ANNUALLY THEN e.amount / 6
                WHEN e.expenseFrequency = com.banking.entities.enums.ExpenseFrequency.ANNUALLY THEN e.amount / 12
                ELSE e.amount
            END) 
            FROM Expense e 
            WHERE e.customer.id = :customerId
            """)
    BigDecimal calculateMonthlyExpenseAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Find all expenses requiring verification.
     *
     * @param thresholdDate the threshold date
     * @return list of expenses
     */
    @Query("SELECT e FROM Expense e WHERE e.isVerified = false OR (e.verificationDate < :thresholdDate)")
    List<Expense> findExpensesRequiringVerification(@Param("thresholdDate") LocalDate thresholdDate);
    
    /**
     * Batch update verification status.
     *
     * @param isVerified the is verified
     * @param verificationDate the verification date
     * @param customerId the customer ID
     * @return the number of updated records
     */
    @Query("UPDATE Expense e SET e.isVerified = :isVerified, e.verificationDate = :verificationDate WHERE e.customer.id = :customerId")
    int batchUpdateVerificationStatus(
            @Param("isVerified") Boolean isVerified, 
            @Param("verificationDate") LocalDate verificationDate, 
            @Param("customerId") Long customerId);
} 
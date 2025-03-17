package com.banking.repositories.abstracts;

import com.banking.entities.RepaymentPlan;
import com.banking.entities.enums.RepaymentFrequency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RepaymentPlan entity.
 * Provides methods for accessing repayment plan data.
 */
@Repository
public interface RepaymentPlanRepository extends GenericRepository<RepaymentPlan, Long> {
    
    /**
     * Find repayment plan by credit application ID.
     *
     * @param creditApplicationId the credit application ID
     * @return the repayment plan
     */
    Optional<RepaymentPlan> findByCreditApplicationId(Long creditApplicationId);
    
    /**
     * Find all repayment plans by customer ID.
     *
     * @param customerId the customer ID
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByCustomerId(Long customerId);
    
    /**
     * Find all repayment plans by repayment frequency.
     *
     * @param repaymentFrequency the repayment frequency
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByRepaymentFrequency(RepaymentFrequency repaymentFrequency);
    
    /**
     * Find all repayment plans by start date.
     *
     * @param startDate the start date
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByStartDate(LocalDate startDate);
    
    /**
     * Find all repayment plans by end date.
     *
     * @param endDate the end date
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByEndDate(LocalDate endDate);
    
    /**
     * Find all repayment plans by start date between.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all repayment plans by end date between.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all repayment plans by customer ID and start date between.
     *
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByCustomerIdAndStartDateBetween(Long customerId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all repayment plans by customer ID with pagination.
     *
     * @param customerId the customer ID
     * @param pageable the pageable
     * @return page of repayment plans
     */
    Page<RepaymentPlan> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Find all repayment plans by interest rate greater than.
     *
     * @param interestRate the interest rate
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByInterestRateGreaterThan(BigDecimal interestRate);
    
    /**
     * Find all repayment plans by interest rate less than.
     *
     * @param interestRate the interest rate
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByInterestRateLessThan(BigDecimal interestRate);
    
    /**
     * Find all repayment plans by total amount greater than.
     *
     * @param totalAmount the total amount
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByTotalAmountGreaterThan(BigDecimal totalAmount);
    
    /**
     * Find all repayment plans by number of installments.
     *
     * @param numberOfInstallments the number of installments
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByNumberOfInstallments(Integer numberOfInstallments);
    
    /**
     * Find all repayment plans by early payment allowed.
     *
     * @param earlyPaymentAllowed the early payment allowed
     * @return list of repayment plans
     */
    List<RepaymentPlan> findByEarlyPaymentAllowed(Boolean earlyPaymentAllowed);
    
    /**
     * Calculate total interest amount by customer.
     *
     * @param customerId the customer ID
     * @return the total interest amount
     */
    @Query("SELECT SUM(rp.interestAmount) FROM RepaymentPlan rp WHERE rp.customer.id = :customerId")
    BigDecimal calculateTotalInterestAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate average interest rate by customer.
     *
     * @param customerId the customer ID
     * @return the average interest rate
     */
    @Query("SELECT AVG(rp.interestRate) FROM RepaymentPlan rp WHERE rp.customer.id = :customerId")
    BigDecimal calculateAverageInterestRateByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Find all active repayment plans.
     *
     * @param currentDate the current date
     * @return list of repayment plans
     */
    @Query("SELECT rp FROM RepaymentPlan rp WHERE rp.startDate <= :currentDate AND rp.endDate >= :currentDate")
    List<RepaymentPlan> findActiveRepaymentPlans(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find all repayment plans ending soon.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of repayment plans
     */
    @Query("SELECT rp FROM RepaymentPlan rp WHERE rp.endDate BETWEEN :startDate AND :endDate")
    List<RepaymentPlan> findRepaymentPlansEndingSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Batch update interest rate for repayment plans.
     *
     * @param oldInterestRate the old interest rate
     * @param newInterestRate the new interest rate
     * @return the number of updated records
     */
    @Query("UPDATE RepaymentPlan rp SET rp.interestRate = :newInterestRate WHERE rp.interestRate = :oldInterestRate")
    int batchUpdateInterestRate(
            @Param("oldInterestRate") BigDecimal oldInterestRate, 
            @Param("newInterestRate") BigDecimal newInterestRate);
} 
package com.banking.repositories.abstracts;

import com.banking.entities.IncomeSource;
import com.banking.entities.enums.IncomeFrequency;
import com.banking.entities.enums.IncomeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for IncomeSource entity.
 * Provides methods for accessing income source data.
 */
@Repository
public interface IncomeSourceRepository extends GenericRepository<IncomeSource, Long> {
    
    /**
     * Find all income sources by customer ID.
     *
     * @param customerId the customer ID
     * @return list of income sources
     */
    List<IncomeSource> findByCustomerId(Long customerId);
    
    /**
     * Find all income sources by income type.
     *
     * @param incomeType the income type
     * @return list of income sources
     */
    List<IncomeSource> findByIncomeType(IncomeType incomeType);
    
    /**
     * Find all income sources by income frequency.
     *
     * @param incomeFrequency the income frequency
     * @return list of income sources
     */
    List<IncomeSource> findByIncomeFrequency(IncomeFrequency incomeFrequency);
    
    /**
     * Find all income sources by amount greater than.
     *
     * @param amount the amount
     * @return list of income sources
     */
    List<IncomeSource> findByAmountGreaterThan(BigDecimal amount);
    
    /**
     * Find all income sources by amount less than.
     *
     * @param amount the amount
     * @return list of income sources
     */
    List<IncomeSource> findByAmountLessThan(BigDecimal amount);
    
    /**
     * Find all income sources by source name.
     *
     * @param sourceName the source name
     * @return list of income sources
     */
    List<IncomeSource> findBySourceName(String sourceName);
    
    /**
     * Find all income sources by employer name.
     *
     * @param employerName the employer name
     * @return list of income sources
     */
    List<IncomeSource> findByEmployerName(String employerName);
    
    /**
     * Find all income sources by position.
     *
     * @param position the position
     * @return list of income sources
     */
    List<IncomeSource> findByPosition(String position);
    
    /**
     * Find all income sources by start date.
     *
     * @param startDate the start date
     * @return list of income sources
     */
    List<IncomeSource> findByStartDate(LocalDate startDate);
    
    /**
     * Find all income sources by end date.
     *
     * @param endDate the end date
     * @return list of income sources
     */
    List<IncomeSource> findByEndDate(LocalDate endDate);
    
    /**
     * Find all income sources by is verified.
     *
     * @param isVerified the is verified
     * @return list of income sources
     */
    List<IncomeSource> findByIsVerified(Boolean isVerified);
    
    /**
     * Find all income sources by verification date.
     *
     * @param verificationDate the verification date
     * @return list of income sources
     */
    List<IncomeSource> findByVerificationDate(LocalDate verificationDate);
    
    /**
     * Find all income sources by customer ID and income type.
     *
     * @param customerId the customer ID
     * @param incomeType the income type
     * @return list of income sources
     */
    List<IncomeSource> findByCustomerIdAndIncomeType(Long customerId, IncomeType incomeType);
    
    /**
     * Find all income sources by customer ID and income frequency.
     *
     * @param customerId the customer ID
     * @param incomeFrequency the income frequency
     * @return list of income sources
     */
    List<IncomeSource> findByCustomerIdAndIncomeFrequency(Long customerId, IncomeFrequency incomeFrequency);
    
    /**
     * Find all income sources by customer ID with pagination.
     *
     * @param customerId the customer ID
     * @param pageable the pageable
     * @return page of income sources
     */
    Page<IncomeSource> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Calculate total income amount by customer.
     *
     * @param customerId the customer ID
     * @return the total income amount
     */
    @Query("SELECT SUM(i.amount) FROM IncomeSource i WHERE i.customer.id = :customerId")
    BigDecimal calculateTotalIncomeAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total income amount by customer and income type.
     *
     * @param customerId the customer ID
     * @param incomeType the income type
     * @return the total income amount
     */
    @Query("SELECT SUM(i.amount) FROM IncomeSource i WHERE i.customer.id = :customerId AND i.incomeType = :incomeType")
    BigDecimal calculateTotalIncomeAmountByCustomerAndIncomeType(
            @Param("customerId") Long customerId, 
            @Param("incomeType") IncomeType incomeType);
    
    /**
     * Calculate monthly income amount by customer.
     *
     * @param customerId the customer ID
     * @return the monthly income amount
     */
    @Query("""
            SELECT SUM(CASE 
                WHEN i.incomeFrequency = com.banking.entities.enums.IncomeFrequency.DAILY THEN i.amount * 30
                WHEN i.incomeFrequency = com.banking.entities.enums.IncomeFrequency.WEEKLY THEN i.amount * 4
                WHEN i.incomeFrequency = com.banking.entities.enums.IncomeFrequency.BI_WEEKLY THEN i.amount * 2
                WHEN i.incomeFrequency = com.banking.entities.enums.IncomeFrequency.MONTHLY THEN i.amount
                WHEN i.incomeFrequency = com.banking.entities.enums.IncomeFrequency.QUARTERLY THEN i.amount / 3
                WHEN i.incomeFrequency = com.banking.entities.enums.IncomeFrequency.SEMI_ANNUALLY THEN i.amount / 6
                WHEN i.incomeFrequency = com.banking.entities.enums.IncomeFrequency.ANNUALLY THEN i.amount / 12
                ELSE i.amount
            END) 
            FROM IncomeSource i 
            WHERE i.customer.id = :customerId
            """)
    BigDecimal calculateMonthlyIncomeAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Find all income sources requiring verification.
     *
     * @param thresholdDate the threshold date
     * @return list of income sources
     */
    @Query("SELECT i FROM IncomeSource i WHERE i.isVerified = false OR (i.verificationDate < :thresholdDate)")
    List<IncomeSource> findIncomeSourcesRequiringVerification(@Param("thresholdDate") LocalDate thresholdDate);
    
    /**
     * Batch update verification status.
     *
     * @param isVerified the is verified
     * @param verificationDate the verification date
     * @param customerId the customer ID
     * @return the number of updated records
     */
    @Query("UPDATE IncomeSource i SET i.isVerified = :isVerified, i.verificationDate = :verificationDate WHERE i.customer.id = :customerId")
    int batchUpdateVerificationStatus(
            @Param("isVerified") Boolean isVerified, 
            @Param("verificationDate") LocalDate verificationDate, 
            @Param("customerId") Long customerId);
} 
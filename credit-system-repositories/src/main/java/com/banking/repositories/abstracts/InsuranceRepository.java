package com.banking.repositories.abstracts;

import com.banking.entities.Insurance;
import com.banking.entities.enums.InsuranceStatus;
import com.banking.entities.enums.InsuranceType;
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
 * Repository interface for Insurance entity.
 * Provides methods for accessing insurance data.
 */
@Repository
public interface InsuranceRepository extends GenericRepository<Insurance, Long> {
    
    /**
     * Find insurance by policy number.
     *
     * @param policyNumber the policy number
     * @return the insurance
     */
    Optional<Insurance> findByPolicyNumber(String policyNumber);
    
    /**
     * Find all insurances by customer ID.
     *
     * @param customerId the customer ID
     * @return list of insurances
     */
    List<Insurance> findByCustomerId(Long customerId);
    
    /**
     * Find all insurances by credit application ID.
     *
     * @param creditApplicationId the credit application ID
     * @return list of insurances
     */
    List<Insurance> findByCreditApplicationId(Long creditApplicationId);
    
    /**
     * Find all insurances by insurance type.
     *
     * @param insuranceType the insurance type
     * @return list of insurances
     */
    List<Insurance> findByInsuranceType(InsuranceType insuranceType);
    
    /**
     * Find all insurances by insurance status.
     *
     * @param insuranceStatus the insurance status
     * @return list of insurances
     */
    List<Insurance> findByInsuranceStatus(InsuranceStatus insuranceStatus);
    
    /**
     * Find all insurances by insurance company.
     *
     * @param insuranceCompany the insurance company
     * @return list of insurances
     */
    List<Insurance> findByInsuranceCompany(String insuranceCompany);
    
    /**
     * Find all insurances by start date.
     *
     * @param startDate the start date
     * @return list of insurances
     */
    List<Insurance> findByStartDate(LocalDate startDate);
    
    /**
     * Find all insurances by end date.
     *
     * @param endDate the end date
     * @return list of insurances
     */
    List<Insurance> findByEndDate(LocalDate endDate);
    
    /**
     * Find all insurances by start date between.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of insurances
     */
    List<Insurance> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all insurances by end date between.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of insurances
     */
    List<Insurance> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all insurances by premium amount greater than.
     *
     * @param premiumAmount the premium amount
     * @return list of insurances
     */
    List<Insurance> findByPremiumAmountGreaterThan(BigDecimal premiumAmount);
    
    /**
     * Find all insurances by coverage amount greater than.
     *
     * @param coverageAmount the coverage amount
     * @return list of insurances
     */
    List<Insurance> findByCoverageAmountGreaterThan(BigDecimal coverageAmount);
    
    /**
     * Find all insurances by beneficiary.
     *
     * @param beneficiary the beneficiary
     * @return list of insurances
     */
    List<Insurance> findByBeneficiary(String beneficiary);
    
    /**
     * Find all insurances by customer ID and insurance type.
     *
     * @param customerId the customer ID
     * @param insuranceType the insurance type
     * @return list of insurances
     */
    List<Insurance> findByCustomerIdAndInsuranceType(Long customerId, InsuranceType insuranceType);
    
    /**
     * Find all insurances by customer ID and insurance status.
     *
     * @param customerId the customer ID
     * @param insuranceStatus the insurance status
     * @return list of insurances
     */
    List<Insurance> findByCustomerIdAndInsuranceStatus(Long customerId, InsuranceStatus insuranceStatus);
    
    /**
     * Find all insurances by customer ID with pagination.
     *
     * @param customerId the customer ID
     * @param pageable the pageable
     * @return page of insurances
     */
    Page<Insurance> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Calculate total premium amount by customer.
     *
     * @param customerId the customer ID
     * @return the total premium amount
     */
    @Query("SELECT SUM(i.premiumAmount) FROM Insurance i WHERE i.customer.id = :customerId")
    BigDecimal calculateTotalPremiumAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total coverage amount by customer.
     *
     * @param customerId the customer ID
     * @return the total coverage amount
     */
    @Query("SELECT SUM(i.coverageAmount) FROM Insurance i WHERE i.customer.id = :customerId")
    BigDecimal calculateTotalCoverageAmountByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Find all active insurances.
     *
     * @param currentDate the current date
     * @return list of insurances
     */
    @Query("SELECT i FROM Insurance i WHERE i.startDate <= :currentDate AND i.endDate >= :currentDate AND i.insuranceStatus = com.banking.entities.enums.InsuranceStatus.ACTIVE")
    List<Insurance> findActiveInsurances(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find all insurances expiring soon.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of insurances
     */
    @Query("SELECT i FROM Insurance i WHERE i.endDate BETWEEN :startDate AND :endDate AND i.insuranceStatus = com.banking.entities.enums.InsuranceStatus.ACTIVE")
    List<Insurance> findInsurancesExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * Batch update insurance status.
     *
     * @param oldStatus the old status
     * @param newStatus the new status
     * @return the number of updated records
     */
    @Query("UPDATE Insurance i SET i.insuranceStatus = :newStatus WHERE i.insuranceStatus = :oldStatus")
    int batchUpdateInsuranceStatus(
            @Param("oldStatus") InsuranceStatus oldStatus, 
            @Param("newStatus") InsuranceStatus newStatus);
    
    /**
     * Batch update expired insurances.
     *
     * @param currentDate the current date
     * @return the number of updated records
     */
    @Query("UPDATE Insurance i SET i.insuranceStatus = com.banking.entities.enums.InsuranceStatus.EXPIRED WHERE i.endDate < :currentDate AND i.insuranceStatus = com.banking.entities.enums.InsuranceStatus.ACTIVE")
    int batchUpdateExpiredInsurances(@Param("currentDate") LocalDate currentDate);
} 
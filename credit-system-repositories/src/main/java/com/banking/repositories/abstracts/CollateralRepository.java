package com.banking.repositories.abstracts;

import com.banking.entities.Collateral;
import com.banking.entities.enums.CollateralStatus;
import com.banking.entities.enums.CollateralType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Collateral entity.
 * Provides methods for accessing collateral data.
 */
@Repository
public interface CollateralRepository extends GenericRepository<Collateral, Long> {
    
    /**
     * Find all collaterals by customer ID.
     *
     * @param customerId the customer ID
     * @return list of collaterals
     */
    List<Collateral> findByCustomerId(Long customerId);
    
    /**
     * Find all collaterals by credit application ID.
     *
     * @param creditApplicationId the credit application ID
     * @return list of collaterals
     */
    List<Collateral> findByCreditApplicationId(Long creditApplicationId);
    
    /**
     * Find all collaterals by collateral type.
     *
     * @param collateralType the collateral type
     * @return list of collaterals
     */
    List<Collateral> findByCollateralType(CollateralType collateralType);
    
    /**
     * Find all collaterals by collateral status.
     *
     * @param collateralStatus the collateral status
     * @return list of collaterals
     */
    List<Collateral> findByCollateralStatus(CollateralStatus collateralStatus);
    
    /**
     * Find all collaterals by valuation date.
     *
     * @param valuationDate the valuation date
     * @return list of collaterals
     */
    List<Collateral> findByValuationDate(LocalDate valuationDate);
    
    /**
     * Find all collaterals by valuation date between.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of collaterals
     */
    List<Collateral> findByValuationDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all collaterals by collateral value greater than.
     *
     * @param collateralValue the collateral value
     * @return list of collaterals
     */
    List<Collateral> findByCollateralValueGreaterThan(BigDecimal collateralValue);
    
    /**
     * Find all collaterals by collateral value less than.
     *
     * @param collateralValue the collateral value
     * @return list of collaterals
     */
    List<Collateral> findByCollateralValueLessThan(BigDecimal collateralValue);
    
    /**
     * Find all collaterals by customer ID and collateral type.
     *
     * @param customerId the customer ID
     * @param collateralType the collateral type
     * @return list of collaterals
     */
    List<Collateral> findByCustomerIdAndCollateralType(Long customerId, CollateralType collateralType);
    
    /**
     * Find all collaterals by customer ID and collateral status.
     *
     * @param customerId the customer ID
     * @param collateralStatus the collateral status
     * @return list of collaterals
     */
    List<Collateral> findByCustomerIdAndCollateralStatus(Long customerId, CollateralStatus collateralStatus);
    
    /**
     * Find all collaterals by customer ID with pagination.
     *
     * @param customerId the customer ID
     * @param pageable the pageable
     * @return page of collaterals
     */
    Page<Collateral> findByCustomerId(Long customerId, Pageable pageable);
    
    /**
     * Calculate total collateral value by customer.
     *
     * @param customerId the customer ID
     * @return the total collateral value
     */
    @Query("SELECT SUM(c.collateralValue) FROM Collateral c WHERE c.customer.id = :customerId")
    BigDecimal calculateTotalCollateralValueByCustomer(@Param("customerId") Long customerId);
    
    /**
     * Calculate total collateral value by credit application.
     *
     * @param creditApplicationId the credit application ID
     * @return the total collateral value
     */
    @Query("SELECT SUM(c.collateralValue) FROM Collateral c WHERE c.creditApplication.id = :creditApplicationId")
    BigDecimal calculateTotalCollateralValueByCreditApplication(@Param("creditApplicationId") Long creditApplicationId);
    
    /**
     * Find all collaterals by appraiser name.
     *
     * @param appraiserName the appraiser name
     * @return list of collaterals
     */
    List<Collateral> findByAppraiserName(String appraiserName);
    
    /**
     * Find all collaterals by registration number.
     *
     * @param registrationNumber the registration number
     * @return list of collaterals
     */
    List<Collateral> findByRegistrationNumber(String registrationNumber);
    
    /**
     * Find all collaterals by appraisal report reference.
     *
     * @param appraisalReportReference the appraisal report reference
     * @return list of collaterals
     */
    List<Collateral> findByAppraisalReportReference(String appraisalReportReference);
    
    /**
     * Find all collaterals requiring revaluation.
     *
     * @param thresholdDate the threshold date
     * @return list of collaterals
     */
    @Query("SELECT c FROM Collateral c WHERE c.valuationDate < :thresholdDate AND c.collateralStatus = com.banking.entities.enums.CollateralStatus.ACTIVE")
    List<Collateral> findCollateralsRequiringRevaluation(@Param("thresholdDate") LocalDate thresholdDate);
    
    /**
     * Batch update collateral status.
     *
     * @param oldStatus the old status
     * @param newStatus the new status
     * @return the number of updated records
     */
    @Query("UPDATE Collateral c SET c.collateralStatus = :newStatus WHERE c.collateralStatus = :oldStatus")
    int batchUpdateCollateralStatus(
            @Param("oldStatus") CollateralStatus oldStatus, 
            @Param("newStatus") CollateralStatus newStatus);
} 
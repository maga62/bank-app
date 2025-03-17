package com.banking.repositories.abstracts;

import com.banking.entities.CreditApplication;
import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.entities.enums.CreditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CreditApplication entities.
 */
@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {
    
    /**
     * Find all credit applications for a customer, with pagination and sorting.
     * Only returns active (not deleted) applications.
     * 
     * @param customerId the customer ID to search for
     * @param pageable pagination and sorting information
     * @return a Page of credit applications
     */
    @Query("SELECT ca FROM CreditApplication ca WHERE ca.customer.id = :customerId AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditApplication> findAllByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    /**
     * Find all credit applications of a specific type, with pagination and sorting.
     * Only returns active (not deleted) applications.
     * 
     * @param creditType the credit type to search for
     * @param pageable pagination and sorting information
     * @return a Page of credit applications
     */
    @Query("SELECT ca FROM CreditApplication ca WHERE ca.creditType = :creditType AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditApplication> findAllByCreditType(@Param("creditType") CreditType creditType, Pageable pageable);
    
    /**
     * Check if a customer has an active application for a specific credit type.
     * 
     * @param customerId the customer ID to check
     * @param creditType the credit type to check
     * @return true if the customer has an active application for the given credit type, false otherwise
     */
    @Query("SELECT COUNT(ca) > 0 FROM CreditApplication ca WHERE ca.customer.id = :customerId " +
           "AND ca.creditType = :creditType AND ca.status <> 'CANCELLED' AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    boolean existsByCustomerIdAndCreditType(@Param("customerId") Long customerId, @Param("creditType") CreditType creditType);
    
    /**
     * Find all credit applications with a specific status, with pagination and sorting.
     * Only returns active (not deleted) applications.
     * 
     * @param status the status to search for
     * @param pageable pagination and sorting information
     * @return a Page of credit applications
     */
    @Query("SELECT ca FROM CreditApplication ca WHERE ca.status = :status AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditApplication> findAllByStatus(@Param("status") CreditApplicationStatus status, Pageable pageable);
    
    /**
     * Find all credit applications created between the given dates, with pagination and sorting.
     * Only returns active (not deleted) applications.
     * 
     * @param startDate the start date to search from (inclusive)
     * @param endDate the end date to search to (inclusive)
     * @param pageable pagination and sorting information
     * @return a Page of credit applications
     */
    @Query("SELECT ca FROM CreditApplication ca WHERE ca.createdDate BETWEEN :startDate AND :endDate " +
           "AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditApplication> findAllByCreatedDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Find all credit applications with an amount greater than or equal to the given amount, with pagination and sorting.
     * Only returns active (not deleted) applications.
     * 
     * @param amount the minimum amount to search for
     * @param pageable pagination and sorting information
     * @return a Page of credit applications
     */
    @Query("SELECT ca FROM CreditApplication ca WHERE ca.amount >= :amount AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditApplication> findAllByAmountGreaterThanEqual(@Param("amount") BigDecimal amount, Pageable pageable);
    
    /**
     * Find all credit applications with optimized field selection for listing purposes.
     * This query only selects the fields needed for display in a list, reducing the amount of data transferred.
     * Only returns active (not deleted) applications.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of credit applications with selected fields
     */
    @Query("SELECT new map(ca.id as id, ca.customer.id as customerId, ca.customer.customerNumber as customerNumber, " +
           "ca.creditType as creditType, ca.status as status, ca.amount as amount, ca.termMonths as termMonths, " +
           "ca.createdDate as createdDate) " +
           "FROM CreditApplication ca WHERE ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditApplication> findAllForListing(Pageable pageable);
    
    /**
     * Find a credit application by ID, ensuring it is active (not deleted).
     * 
     * @param id the ID to search for
     * @return an Optional containing the credit application if found and active, or an empty Optional if not found or deleted
     */
    @Query("SELECT ca FROM CreditApplication ca WHERE ca.id = :id AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Optional<CreditApplication> findByIdAndDeletedDateIsNull(@Param("id") Long id);

    /**
     * Find all credit applications for a customer with a specific status.
     * Only returns active (not deleted) applications.
     * 
     * @param customerId the customer ID to search for
     * @param status the status to search for
     * @return a List of credit applications
     */
    @Query("SELECT ca FROM CreditApplication ca WHERE ca.customer.id = :customerId AND ca.status = :status AND ca.deletedDate IS NULL")
    @Transactional(readOnly = true)
    List<CreditApplication> findAllByCustomerIdAndStatus(
            @Param("customerId") Long customerId, 
            @Param("status") CreditApplicationStatus status);
} 
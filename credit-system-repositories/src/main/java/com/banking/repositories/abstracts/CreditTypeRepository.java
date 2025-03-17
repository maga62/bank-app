package com.banking.repositories.abstracts;

import com.banking.entities.CreditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CreditType entities.
 */
@Repository
public interface CreditTypeRepository extends JpaRepository<CreditType, Long> {
    
    /**
     * Check if a credit type with the given name exists.
     * 
     * @param name the name to check
     * @return true if a credit type with the given name exists, false otherwise
     */
    @Transactional(readOnly = true)
    boolean existsByName(String name);
    
    /**
     * Find a credit type by name.
     * 
     * @param name the name to search for
     * @return an Optional containing the credit type if found, or an empty Optional if not found
     */
    @Transactional(readOnly = true)
    Optional<CreditType> findByName(String name);
    
    /**
     * Find all credit types for individual or corporate customers, with soft delete filtering.
     * 
     * @param isIndividual true for individual credit types, false for corporate credit types
     * @return a List of credit types
     */
    @Query("SELECT ct FROM CreditType ct WHERE ct.isIndividual = :isIndividual AND ct.deletedDate IS NULL")
    @Transactional(readOnly = true)
    List<CreditType> findAllByIsIndividual(@Param("isIndividual") boolean isIndividual);
    
    /**
     * Find all active or inactive credit types, with pagination, sorting, and soft delete filtering.
     * 
     * @param isActive true for active credit types, false for inactive credit types
     * @param pageable pagination and sorting information
     * @return a Page of credit types
     */
    @Query("SELECT ct FROM CreditType ct WHERE ct.isActive = :isActive AND ct.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditType> findAllByIsActive(@Param("isActive") boolean isActive, Pageable pageable);
    
    /**
     * Find all credit types for individual or corporate customers, active or inactive, with pagination, sorting, and soft delete filtering.
     * 
     * @param isIndividual true for individual credit types, false for corporate credit types
     * @param isActive true for active credit types, false for inactive credit types
     * @param pageable pagination and sorting information
     * @return a Page of credit types
     */
    @Query("SELECT ct FROM CreditType ct WHERE ct.isIndividual = :isIndividual AND ct.isActive = :isActive AND ct.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditType> findAllByIsIndividualAndIsActive(
            @Param("isIndividual") boolean isIndividual, 
            @Param("isActive") boolean isActive, 
            Pageable pageable);
    
    /**
     * Find all credit types with an interest rate in the given range, with pagination, sorting, and soft delete filtering.
     * 
     * @param minRate the minimum interest rate
     * @param maxRate the maximum interest rate
     * @param pageable pagination and sorting information
     * @return a Page of credit types
     */
    @Query("SELECT ct FROM CreditType ct WHERE ct.minInterestRate >= :minRate AND ct.maxInterestRate <= :maxRate AND ct.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditType> findAllByInterestRateBetween(
            @Param("minRate") double minRate, 
            @Param("maxRate") double maxRate, 
            Pageable pageable);
    
    /**
     * Find all credit types with a term in the given range, with pagination, sorting, and soft delete filtering.
     * 
     * @param minTerm the minimum term in months
     * @param maxTerm the maximum term in months
     * @param pageable pagination and sorting information
     * @return a Page of credit types
     */
    @Query("SELECT ct FROM CreditType ct WHERE ct.minTermMonths >= :minTerm AND ct.maxTermMonths <= :maxTerm AND ct.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditType> findAllByTermBetween(
            @Param("minTerm") int minTerm, 
            @Param("maxTerm") int maxTerm, 
            Pageable pageable);
    
    /**
     * Find all credit types with optimized field selection for listing purposes.
     * This query only selects the fields needed for display in a list, reducing the amount of data transferred.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of credit types with selected fields
     */
    @Query("SELECT new map(ct.id as id, ct.name as name, ct.isIndividual as isIndividual, " +
           "ct.isActive as isActive, ct.minInterestRate as minInterestRate, ct.maxInterestRate as maxInterestRate, " +
           "ct.minTermMonths as minTermMonths, ct.maxTermMonths as maxTermMonths) " +
           "FROM CreditType ct WHERE ct.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CreditType> findAllForListing(Pageable pageable);
} 
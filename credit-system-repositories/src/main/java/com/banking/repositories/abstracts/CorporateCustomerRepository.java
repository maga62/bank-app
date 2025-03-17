package com.banking.repositories.abstracts;

import com.banking.entities.CorporateCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * Repository interface for CorporateCustomer entities.
 * Extends CustomerRepository to inherit common customer operations.
 */
@Repository
public interface CorporateCustomerRepository extends CustomerRepository<CorporateCustomer> {
    
    /**
     * Check if a corporate customer with the given tax number exists.
     * 
     * @param taxNumber the tax number to check
     * @return true if a corporate customer with the given tax number exists, false otherwise
     */
    @Transactional(readOnly = true)
    boolean existsByTaxNumber(String taxNumber);
    
    /**
     * Find a corporate customer by tax number.
     * 
     * @param taxNumber the tax number to search for
     * @return an Optional containing the corporate customer if found, or an empty Optional if not found
     */
    @Transactional(readOnly = true)
    Optional<CorporateCustomer> findByTaxNumber(String taxNumber);
    
    /**
     * Find all corporate customers with a company name containing the given string, with pagination and sorting.
     * 
     * @param companyName the string to search for in company names
     * @param pageable pagination and sorting information
     * @return a Page of corporate customers
     */
    @Transactional(readOnly = true)
    Page<CorporateCustomer> findAllByCompanyNameContaining(String companyName, Pageable pageable);
    
    /**
     * Find all corporate customers with a company type matching the given string, with pagination and sorting.
     * 
     * @param companyType the company type to search for
     * @param pageable pagination and sorting information
     * @return a Page of corporate customers
     */
    @Transactional(readOnly = true)
    Page<CorporateCustomer> findAllByCompanyType(String companyType, Pageable pageable);
    
    /**
     * Find all corporate customers with a trade register number matching the given string.
     * 
     * @param tradeRegisterNumber the trade register number to search for
     * @return an Optional containing the corporate customer if found, or an empty Optional if not found
     */
    @Transactional(readOnly = true)
    Optional<CorporateCustomer> findByTradeRegisterNumber(String tradeRegisterNumber);
    
    /**
     * Find all corporate customers with optimized field selection for listing purposes.
     * This query only selects the fields needed for display in a list, reducing the amount of data transferred.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of corporate customers with selected fields
     */
    @Query("SELECT new map(c.id as id, c.customerNumber as customerNumber, c.companyName as companyName, " +
           "c.taxNumber as taxNumber, c.companyType as companyType, c.email as email, c.phoneNumber as phoneNumber) " +
           "FROM CorporateCustomer c WHERE c.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<CorporateCustomer> findAllForListing(Pageable pageable);
}

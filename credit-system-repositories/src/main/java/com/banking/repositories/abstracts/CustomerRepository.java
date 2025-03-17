package com.banking.repositories.abstracts;

import com.banking.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Base repository interface for Customer entities.
 * Supports polymorphic queries for both Individual and Corporate Customers.
 * 
 * @param <T> Customer type (IndividualCustomer or CorporateCustomer)
 */
@NoRepositoryBean
public interface CustomerRepository<T extends Customer> extends GenericRepository<T, Long> {
    
    /**
     * Check if a customer with the given customer number exists.
     * 
     * @param customerNumber the customer number to check
     * @return true if a customer with the given customer number exists, false otherwise
     */
    @Transactional(readOnly = true)
    boolean existsByCustomerNumber(String customerNumber);
    
    /**
     * Find a customer by customer number.
     * 
     * @param customerNumber the customer number to search for
     * @return an Optional containing the customer if found, or an empty Optional if not found
     */
    @Transactional(readOnly = true)
    Optional<T> findByCustomerNumber(String customerNumber);
    
    /**
     * Find all customers with pagination and sorting.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of customers
     */
    @Override
    @Transactional(readOnly = true)
    @NonNull
    Page<T> findAll(@NonNull Pageable pageable);
    
    /**
     * Find all active customers (not deleted) with pagination and sorting.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of active customers
     */
    @Query("SELECT c FROM #{#entityName} c WHERE c.deletedDate IS NULL")
    @Transactional(readOnly = true)
    @NonNull
    Page<T> findAllActive(@NonNull Pageable pageable);
} 
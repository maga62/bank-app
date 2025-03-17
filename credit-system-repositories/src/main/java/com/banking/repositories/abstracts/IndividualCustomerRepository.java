package com.banking.repositories.abstracts;

import com.banking.entities.IndividualCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository interface for IndividualCustomer entities.
 * Extends CustomerRepository to inherit common customer operations.
 */
@Repository
public interface IndividualCustomerRepository extends CustomerRepository<IndividualCustomer> {
    
    /**
     * Check if an individual customer with the given identity number exists.
     * 
     * @param identityNumber the identity number to check
     * @return true if an individual customer with the given identity number exists, false otherwise
     */
    @Transactional(readOnly = true)
    boolean existsByIdentityNumber(String identityNumber);
    
    /**
     * Find an individual customer by identity number.
     * 
     * @param identityNumber the identity number to search for
     * @return an Optional containing the individual customer if found, or an empty Optional if not found
     */
    @Transactional(readOnly = true)
    Optional<IndividualCustomer> findByIdentityNumber(String identityNumber);
    
    /**
     * Find all individual customers with a first name containing the given string, with pagination and sorting.
     * 
     * @param firstName the string to search for in first names
     * @param pageable pagination and sorting information
     * @return a Page of individual customers
     */
    @Transactional(readOnly = true)
    Page<IndividualCustomer> findAllByFirstNameContaining(String firstName, Pageable pageable);
    
    /**
     * Find all individual customers with a last name containing the given string, with pagination and sorting.
     * 
     * @param lastName the string to search for in last names
     * @param pageable pagination and sorting information
     * @return a Page of individual customers
     */
    @Transactional(readOnly = true)
    Page<IndividualCustomer> findAllByLastNameContaining(String lastName, Pageable pageable);
    
    /**
     * Find all individual customers with a first name and last name containing the given strings, with pagination and sorting.
     * 
     * @param firstName the string to search for in first names
     * @param lastName the string to search for in last names
     * @param pageable pagination and sorting information
     * @return a Page of individual customers
     */
    @Transactional(readOnly = true)
    Page<IndividualCustomer> findAllByFirstNameContainingAndLastNameContaining(String firstName, String lastName, Pageable pageable);
    
    /**
     * Find all individual customers with a nationality matching the given string, with pagination and sorting.
     * 
     * @param nationality the nationality to search for
     * @param pageable pagination and sorting information
     * @return a Page of individual customers
     */
    @Transactional(readOnly = true)
    Page<IndividualCustomer> findAllByNationality(String nationality, Pageable pageable);
    
    /**
     * Find all individual customers with optimized field selection for listing purposes.
     * This query only selects the fields needed for display in a list, reducing the amount of data transferred.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of individual customers with selected fields
     */
    @Query("SELECT new map(i.id as id, i.customerNumber as customerNumber, i.firstName as firstName, " +
           "i.lastName as lastName, i.identityNumber as identityNumber, i.email as email, i.phoneNumber as phoneNumber) " +
           "FROM IndividualCustomer i WHERE i.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<IndividualCustomer> findAllForListing(Pageable pageable);
} 
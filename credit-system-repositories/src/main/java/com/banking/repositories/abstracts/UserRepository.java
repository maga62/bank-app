package com.banking.repositories.abstracts;

import com.banking.entities.User;
import com.banking.entities.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by email.
     * This method is used for authentication and should be used with caution.
     * 
     * @param email the email to search for
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    @Transactional(readOnly = true)
    Optional<User> findByEmail(String email);
    
    /**
     * Find a user by username.
     * This method is used for authentication and should be used with caution.
     * 
     * @param username the username to search for
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    @Transactional(readOnly = true)
    Optional<User> findByUsername(String username);
    
    /**
     * Check if a user with the given email exists.
     * 
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    @Transactional(readOnly = true)
    boolean existsByEmail(String email);
    
    /**
     * Check if a user with the given username exists.
     * 
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    @Transactional(readOnly = true)
    boolean existsByUsername(String username);
    
    /**
     * Find all users with the given role, with pagination and sorting.
     * 
     * @param role the role to search for
     * @param pageable pagination and sorting information
     * @return a Page of users
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role AND u.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<User> findAllByRole(@Param("role") Role role, Pageable pageable);
    
    /**
     * Find all users with any of the given roles, with pagination and sorting.
     * 
     * @param roles the roles to search for
     * @param pageable pagination and sorting information
     * @return a Page of users
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r IN :roles AND u.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<User> findAllByRoles(@Param("roles") Set<Role> roles, Pageable pageable);
    
    /**
     * Find all active users (not deleted) with pagination and sorting.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of active users
     */
    @Query("SELECT u FROM User u WHERE u.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<User> findAllActive(Pageable pageable);
    
    /**
     * Find all users with optimized field selection for listing purposes.
     * This query only selects the fields needed for display in a list, reducing the amount of data transferred.
     * 
     * @param pageable pagination and sorting information
     * @return a Page of users with selected fields
     */
    @Query("SELECT new map(u.id as id, u.email as email, u.firstName as firstName, " +
           "u.lastName as lastName, u.enabled as enabled) " +
           "FROM User u WHERE u.deletedDate IS NULL")
    @Transactional(readOnly = true)
    Page<User> findAllForListing(Pageable pageable);
} 
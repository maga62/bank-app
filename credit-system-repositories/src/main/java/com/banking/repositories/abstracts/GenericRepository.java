package com.banking.repositories.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for all entities.
 * Extends JpaRepository to inherit all standard CRUD operations.
 * 
 * @param <T> Entity type
 * @param <ID> ID type
 */
@NoRepositoryBean
public interface GenericRepository<T, ID> extends JpaRepository<T, ID> {
    // No need to define methods that are already provided by JpaRepository
} 
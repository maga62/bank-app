package com.banking.repositories.abstracts;

import com.banking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseCustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByCustomerNumber(String customerNumber);
    Customer findByCustomerNumber(String customerNumber);
} 
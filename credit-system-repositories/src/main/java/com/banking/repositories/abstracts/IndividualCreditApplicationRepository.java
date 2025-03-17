package com.banking.repositories.abstracts;

import com.banking.entities.IndividualCreditApplication;
import com.banking.entities.enums.IndividualCreditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualCreditApplicationRepository extends JpaRepository<IndividualCreditApplication, Long> {
    Page<IndividualCreditApplication> findAllByCustomerId(Long customerId, Pageable pageable);
    Page<IndividualCreditApplication> findAllByCreditType(IndividualCreditType creditType, Pageable pageable);
    boolean existsByCustomerIdAndCreditType(Long customerId, IndividualCreditType creditType);
} 
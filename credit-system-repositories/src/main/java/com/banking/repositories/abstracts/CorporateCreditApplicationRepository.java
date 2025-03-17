package com.banking.repositories.abstracts;

import com.banking.entities.CorporateCreditApplication;
import com.banking.entities.enums.CorporateCreditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorporateCreditApplicationRepository extends JpaRepository<CorporateCreditApplication, Long> {
    Page<CorporateCreditApplication> findAllByCustomerId(Long customerId, Pageable pageable);
    Page<CorporateCreditApplication> findAllByCreditType(CorporateCreditType creditType, Pageable pageable);
    boolean existsByCustomerIdAndCreditType(Long customerId, CorporateCreditType creditType);
} 
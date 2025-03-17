package com.banking.repositories.abstracts;

import com.banking.entities.LoanAgreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanAgreementRepository extends JpaRepository<LoanAgreement, Long> {
    
    @Query("SELECT la FROM LoanAgreement la WHERE la.customerId = :customerId AND la.deletedDate IS NULL")
    List<LoanAgreement> findActiveByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT la FROM LoanAgreement la WHERE la.customerId = :customerId AND la.deletedDate IS NULL")
    Page<LoanAgreement> findActiveByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT la FROM LoanAgreement la WHERE la.signedAt BETWEEN :startDate AND :endDate AND la.deletedDate IS NULL")
    List<LoanAgreement> findActiveBySignedAtBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT la FROM LoanAgreement la WHERE la.signedBy = :signedBy AND la.deletedDate IS NULL")
    List<LoanAgreement> findActiveBySignedBy(@Param("signedBy") String signedBy);
    
    @Query(value = "SELECT * FROM loan_agreements la WHERE " +
            "la.customer_id = :customerId AND la.deleted_date IS NULL " +
            "ORDER BY la.signed_at DESC LIMIT 1", nativeQuery = true)
    Optional<LoanAgreement> findLatestActiveByCustomerId(@Param("customerId") Long customerId);
    
    @Query("UPDATE LoanAgreement la SET la.deletedDate = CURRENT_TIMESTAMP WHERE la.id = :id")
    void softDelete(@Param("id") Long id);
    
    @Query(value = "SELECT la.* FROM loan_agreements la " +
            "INNER JOIN customers c ON la.customer_id = c.id " +
            "WHERE c.risk_score >= :minRiskScore AND la.deleted_date IS NULL", 
            nativeQuery = true)
    List<LoanAgreement> findActiveByCustomerRiskScoreGreaterThan(@Param("minRiskScore") Double minRiskScore);
} 
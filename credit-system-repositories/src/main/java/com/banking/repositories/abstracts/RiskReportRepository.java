package com.banking.repositories.abstracts;

import com.banking.entities.RiskReport;
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
public interface RiskReportRepository extends JpaRepository<RiskReport, Long> {
    
    @Query("SELECT rr FROM RiskReport rr WHERE rr.customerId = :customerId AND rr.deletedDate IS NULL " +
           "AND rr.validityPeriod > CURRENT_TIMESTAMP ORDER BY rr.reportDate DESC")
    Optional<RiskReport> findLatestValidByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT rr FROM RiskReport rr WHERE rr.customerId = :customerId AND rr.deletedDate IS NULL")
    List<RiskReport> findActiveByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT rr FROM RiskReport rr WHERE rr.reportDate BETWEEN :startDate AND :endDate AND rr.deletedDate IS NULL")
    List<RiskReport> findActiveByReportDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT rr FROM RiskReport rr WHERE rr.riskScore >= :minScore AND rr.deletedDate IS NULL")
    List<RiskReport> findActiveByRiskScoreGreaterThanEqual(@Param("minScore") Double minScore);
    
    @Query("SELECT rr FROM RiskReport rr WHERE rr.blacklistStatus = true AND rr.deletedDate IS NULL")
    List<RiskReport> findActiveBlacklistedCustomers();
    
    @Query(value = "SELECT * FROM risk_reports rr " +
            "WHERE rr.customer_id = :customerId AND rr.deleted_date IS NULL " +
            "AND rr.report_date >= :startDate " +
            "ORDER BY rr.report_date DESC", nativeQuery = true)
    Page<RiskReport> findRecentReportsByCustomerId(
            @Param("customerId") Long customerId, 
            @Param("startDate") LocalDateTime startDate, 
            Pageable pageable);
    
    @Query("UPDATE RiskReport rr SET rr.deletedDate = CURRENT_TIMESTAMP WHERE rr.id = :id")
    void softDelete(@Param("id") Long id);
    
    @Query(value = "SELECT rr.* FROM risk_reports rr " +
            "INNER JOIN customers c ON rr.customer_id = c.id " +
            "WHERE c.last_activity_date >= :activityDate " +
            "AND rr.risk_score >= :minScore " +
            "AND rr.deleted_date IS NULL", nativeQuery = true)
    List<RiskReport> findHighRiskActiveCustomers(
            @Param("activityDate") LocalDateTime activityDate, 
            @Param("minScore") Double minScore);
    
    @Query("SELECT AVG(rr.riskScore) FROM RiskReport rr " +
            "WHERE rr.customerId = :customerId AND rr.deletedDate IS NULL " +
            "AND rr.reportDate >= :startDate")
    Double calculateAverageRiskScore(
            @Param("customerId") Long customerId, 
            @Param("startDate") LocalDateTime startDate);
} 
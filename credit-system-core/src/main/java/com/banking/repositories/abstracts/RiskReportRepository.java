package com.banking.repositories.abstracts;

import com.banking.entities.RiskReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskReportRepository extends JpaRepository<RiskReport, Long> {
    // Find risk reports by customer ID and date range with pagination
    Page<RiskReport> findByCustomerIdAndReportDateBetween(
            Long customerId, 
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);

    // Find risk reports with score greater than or equal to threshold
    List<RiskReport> findByRiskScoreGreaterThanEqual(Double riskScoreThreshold);

    // Find all blacklisted customers
    List<RiskReport> findByBlacklistStatusIsTrue();
} 
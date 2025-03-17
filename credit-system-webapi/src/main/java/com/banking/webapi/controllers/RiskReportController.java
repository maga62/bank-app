package com.banking.webapi.controllers;

import com.banking.business.abstracts.RiskReportService;
import com.banking.core.utilities.results.DataResult;
import com.banking.entities.RiskReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
@Validated
@Tag(name = "Risk Report", description = "Risk assessment and reporting operations")
public class RiskReportController {

    private final RiskReportService riskReportService;

    @GetMapping("/get-report/{customerId}")
    @Operation(summary = "Get customer's risk report from Central Bank")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<DataResult<RiskReport>> getReport(@PathVariable Long customerId) {
        return ResponseEntity.ok(riskReportService.getReport(customerId));
    }

    @GetMapping("/get-historical-reports/{customerId}")
    @Operation(summary = "Get customer's historical risk reports")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<DataResult<Page<RiskReport>>> getHistoricalReports(
            @PathVariable Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(riskReportService.getHistoricalReports(customerId, startDate, endDate, pageable));
    }

    @GetMapping("/get-high-risk-customers")
    @Operation(summary = "Get list of high-risk customers")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<DataResult<List<RiskReport>>> getHighRiskCustomers(
            @RequestParam(defaultValue = "70.0") Double minRiskScore) {
        return ResponseEntity.ok(riskReportService.getHighRiskCustomers(minRiskScore));
    }

    @GetMapping("/get-blacklisted-customers")
    @Operation(summary = "Get list of blacklisted customers")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<DataResult<List<RiskReport>>> getBlacklistedCustomers() {
        return ResponseEntity.ok(riskReportService.getBlacklistedCustomers());
    }
} 
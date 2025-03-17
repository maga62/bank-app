package com.banking.webapi.controllers;

import com.banking.business.abstracts.FraudDetectionService;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.entities.SuspiciousTransaction;
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
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Validated
@Tag(name = "Fraud Detection", description = "Fraud detection and monitoring operations")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @GetMapping("/check-transaction/{transactionId}")
    @Operation(summary = "Check if a transaction is fraudulent")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Result> checkTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(fraudDetectionService.checkTransaction(transactionId));
    }

    @GetMapping("/get-flagged-transactions")
    @Operation(summary = "Retrieve all flagged transactions")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<DataResult<Page<SuspiciousTransaction>>> getFlaggedTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(fraudDetectionService.getFlaggedTransactions(startDate, endDate, pageable));
    }

    @GetMapping("/get-customer-transactions/{customerId}")
    @Operation(summary = "Get suspicious transactions for a specific customer")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<DataResult<List<SuspiciousTransaction>>> getCustomerTransactions(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(fraudDetectionService.getCustomerTransactions(customerId));
    }

    @PostMapping("/mark-as-resolved/{transactionId}")
    @Operation(summary = "Mark a suspicious transaction as resolved")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Result> markAsResolved(
            @PathVariable Long transactionId,
            @RequestParam String resolution,
            @RequestParam String resolvedBy) {
        return ResponseEntity.ok(fraudDetectionService.markAsResolved(transactionId, resolution, resolvedBy));
    }
} 
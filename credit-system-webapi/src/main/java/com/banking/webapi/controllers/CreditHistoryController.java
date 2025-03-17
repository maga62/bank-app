package com.banking.webapi.controllers;

import com.banking.business.abstracts.CreditHistoryService;
import com.banking.business.dtos.responses.CreditHistoryResponse;
import com.banking.business.dtos.responses.CreditHistorySummaryResponse;
import com.banking.business.dtos.responses.PaymentPerformanceResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.entities.enums.CreditHistoryStatus;
import com.banking.entities.enums.CreditType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/credit-histories")
@AllArgsConstructor
@Tag(name = "Credit Histories", description = "Credit History API")
@Validated
public class CreditHistoryController {
    private final CreditHistoryService creditHistoryService;

    @GetMapping("/{id}")
    @Operation(summary = "Get credit history by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Credit history not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<CreditHistoryResponse> getById(@PathVariable @Min(1) Long id) {
        return creditHistoryService.getById(id);
    }

    @GetMapping("/by-customer")
    @Operation(summary = "Get credit histories by customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit histories retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerId(
            @RequestParam @Min(1) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditHistoryService.getAllByCustomerId(customerId, pageable);
    }

    @GetMapping("/by-customer-and-status")
    @Operation(summary = "Get credit histories by customer ID and status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit histories retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndStatus(
            @RequestParam @Min(1) Long customerId,
            @RequestParam CreditHistoryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditHistoryService.getAllByCustomerIdAndStatus(customerId, status, pageable);
    }

    @GetMapping("/by-customer-and-credit-type")
    @Operation(summary = "Get credit histories by customer ID and credit type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit histories retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndCreditType(
            @RequestParam @Min(1) Long customerId,
            @RequestParam CreditType creditType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditHistoryService.getAllByCustomerIdAndCreditType(customerId, creditType, pageable);
    }

    @GetMapping("/by-customer-and-date-range")
    @Operation(summary = "Get credit histories by customer ID and date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit histories retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<Page<CreditHistoryResponse>> getAllByCustomerIdAndDateRange(
            @RequestParam @Min(1) Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditHistoryService.getAllByCustomerIdAndStartDateBetween(customerId, startDate, endDate, pageable);
    }

    @GetMapping("/active-by-customer")
    @Operation(summary = "Get active credit histories by customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit histories retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<Page<CreditHistoryResponse>> getActiveByCustomerId(
            @RequestParam @Min(1) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditHistoryService.getActiveByCustomerId(customerId, pageable);
    }

    @GetMapping("/summary-by-customer")
    @Operation(summary = "Get credit history summary by customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit history summary retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<CreditHistorySummaryResponse> getSummaryByCustomerId(@RequestParam @Min(1) Long customerId) {
        return creditHistoryService.getSummaryByCustomerId(customerId);
    }

    @GetMapping("/payment-performance-by-customer")
    @Operation(summary = "Get payment performance by customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment performance retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<PaymentPerformanceResponse> getPaymentPerformanceByCustomerId(@RequestParam @Min(1) Long customerId) {
        return creditHistoryService.getPaymentPerformanceByCustomerId(customerId);
    }

    @GetMapping("/total-outstanding-amount-by-customer")
    @Operation(summary = "Get total outstanding amount by customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total outstanding amount retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<BigDecimal> getTotalOutstandingAmountByCustomerId(@RequestParam @Min(1) Long customerId) {
        return creditHistoryService.getTotalOutstandingAmountByCustomerId(customerId);
    }
} 
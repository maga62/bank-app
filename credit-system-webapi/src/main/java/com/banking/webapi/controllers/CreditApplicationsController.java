package com.banking.webapi.controllers;

import com.banking.business.abstracts.CreditApplicationService;
import com.banking.business.dtos.requests.CreateCreditApplicationRequest;
import com.banking.business.dtos.responses.CreditApplicationResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.entities.enums.CreditType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-applications")
@AllArgsConstructor
@Tag(name = "Credit Applications", description = "Credit Application API")
@Validated
public class CreditApplicationsController {
    private final CreditApplicationService creditApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Apply for a credit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Credit application created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<CreditApplicationResponse> apply(@Valid @RequestBody CreateCreditApplicationRequest request) {
        return creditApplicationService.apply(request);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel a credit application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit application cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Credit application not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public ResponseEntity<Result> cancel(@RequestParam @Min(1) Long id) {
        Result result = creditApplicationService.cancel(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get credit application by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit application retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Credit application not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<CreditApplicationResponse> getById(@PathVariable @Min(1) Long id) {
        return creditApplicationService.getById(id);
    }

    @GetMapping("/by-customer")
    @Operation(summary = "Get credit applications by customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit applications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<Page<CreditApplicationResponse>> getAllByCustomerId(
            @RequestParam @Min(1) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditApplicationService.getAllByCustomerId(customerId, pageable);
    }

    @GetMapping("/by-type")
    @Operation(summary = "Get credit applications by credit type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit applications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public DataResult<Page<CreditApplicationResponse>> getAllByCreditType(
            @RequestParam CreditType creditType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditApplicationService.getAllByCreditType(creditType, pageable);
    }

    @GetMapping
    @Operation(summary = "Get all credit applications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credit applications retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public DataResult<Page<CreditApplicationResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditApplicationService.getAll(pageable);
    }
} 
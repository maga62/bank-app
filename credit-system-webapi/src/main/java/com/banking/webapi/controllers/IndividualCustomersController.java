package com.banking.webapi.controllers;

import com.banking.business.abstracts.IndividualCustomerService;
import com.banking.business.dtos.requests.CreateIndividualCustomerRequest;
import com.banking.business.dtos.responses.IndividualCustomerResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/api/individual-customers")
@AllArgsConstructor
@Tag(name = "Individual Customers", description = "Individual Customer API")
@Validated
public class IndividualCustomersController {
    private final IndividualCustomerService individualCustomerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new individual customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Individual customer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<IndividualCustomerResponse> add(@Valid @RequestBody CreateIndividualCustomerRequest request) {
        return individualCustomerService.add(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an individual customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Individual customer deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Individual customer not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Result> delete(@PathVariable @Min(1) Long id) {
        Result result = individualCustomerService.delete(id);
        return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-customer-number")
    @Operation(summary = "Get individual customer by customer number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Individual customer retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Individual customer not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<IndividualCustomerResponse> getByCustomerNumber(@RequestParam @NotBlank String customerNumber) {
        return individualCustomerService.getByCustomerNumber(customerNumber);
    }

    @GetMapping("/by-identity-number")
    @Operation(summary = "Get individual customer by identity number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Individual customer retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Individual customer not found")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('INDIVIDUAL_CUSTOMER') or hasRole('CORPORATE_CUSTOMER')")
    public DataResult<IndividualCustomerResponse> getByIdentityNumber(
            @RequestParam @NotBlank @Size(min = 11, max = 11) @Pattern(regexp = "^[0-9]{11}$") String identityNumber) {
        return individualCustomerService.getByIdentityNumber(identityNumber);
    }

    @GetMapping
    @Operation(summary = "Get all individual customers with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Individual customers retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public DataResult<Page<IndividualCustomerResponse>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") @Pattern(regexp = "^(ASC|DESC)$") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return individualCustomerService.getAll(pageable);
    }
} 
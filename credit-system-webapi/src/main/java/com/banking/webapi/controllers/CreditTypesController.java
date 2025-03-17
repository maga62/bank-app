package com.banking.webapi.controllers;

import com.banking.business.abstracts.CreditTypeService;
import com.banking.business.dtos.responses.CreditTypeResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-types")
@AllArgsConstructor
@Tag(name = "Credit Types", description = "Credit Type API")
public class CreditTypesController {
    private final CreditTypeService creditTypeService;

    @GetMapping("/{id}")
    @Operation(summary = "Get credit type by ID")
    public DataResult<CreditTypeResponse> getById(@PathVariable Long id) {
        return creditTypeService.getById(id);
    }

    @GetMapping("/by-name/{name}")
    @Operation(summary = "Get credit type by name")
    public DataResult<CreditTypeResponse> getByName(@PathVariable String name) {
        return creditTypeService.getByName(name);
    }

    @GetMapping("/by-customer-type")
    @Operation(summary = "Get credit types by customer type")
    public DataResult<List<CreditTypeResponse>> getAllByCustomerType(
            @RequestParam boolean isIndividual) {
        return creditTypeService.getAllByCustomerType(isIndividual);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active credit types")
    public DataResult<Page<CreditTypeResponse>> getAllActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditTypeService.getAllActive(pageable);
    }

    @GetMapping("/active/by-customer-type")
    @Operation(summary = "Get all active credit types by customer type")
    public DataResult<Page<CreditTypeResponse>> getAllActiveByCustomerType(
            @RequestParam boolean isIndividual,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return creditTypeService.getAllActiveByCustomerType(isIndividual, pageable);
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate credit type")
    public Result activate(@PathVariable Long id) {
        return creditTypeService.activate(id);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate credit type")
    public Result deactivate(@PathVariable Long id) {
        return creditTypeService.deactivate(id);
    }

    @PutMapping("/{id}/interest-rates")
    @Operation(summary = "Update credit type interest rates")
    public Result updateInterestRates(
            @PathVariable Long id,
            @RequestParam double minRate,
            @RequestParam double maxRate) {
        return creditTypeService.updateInterestRates(id, minRate, maxRate);
    }

    @PutMapping("/{id}/term-months")
    @Operation(summary = "Update credit type term months")
    public Result updateTermMonths(
            @PathVariable Long id,
            @RequestParam int minMonths,
            @RequestParam int maxMonths) {
        return creditTypeService.updateTermMonths(id, minMonths, maxMonths);
    }
} 
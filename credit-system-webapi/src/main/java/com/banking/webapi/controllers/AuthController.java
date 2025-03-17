package com.banking.webapi.controllers;

import com.banking.business.abstracts.AuthService;
import com.banking.business.dtos.requests.LoginRequest;
import com.banking.business.dtos.requests.RegisterCorporateRequest;
import com.banking.business.dtos.requests.RegisterIndividualRequest;
import com.banking.business.dtos.responses.JwtResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<DataResult<JwtResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        DataResult<JwtResponse> result = authService.login(loginRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/register/individual")
    @Operation(summary = "Register as individual customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration successful"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<Result> registerIndividual(@Valid @RequestBody RegisterIndividualRequest registerRequest) {
        Result result = authService.registerIndividual(registerRequest);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/register/corporate")
    @Operation(summary = "Register as corporate customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration successful"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    public ResponseEntity<Result> registerCorporate(@Valid @RequestBody RegisterCorporateRequest registerRequest) {
        Result result = authService.registerCorporate(registerRequest);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
} 
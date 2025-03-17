package com.banking.webapi.controllers;

import com.banking.business.abstracts.DigitalSignatureService;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.dtos.request.SignLoanAgreementRequest;
import com.banking.core.dtos.response.SignedAgreementResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signature")
@RequiredArgsConstructor
@Validated
@Tag(name = "Digital Signature", description = "Digital signature operations for loan agreements")
public class DigitalSignatureController {

    private final DigitalSignatureService digitalSignatureService;

    @PostMapping("/sign-loan-agreement")
    @Operation(summary = "Sign a loan agreement digitally")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Result> signLoanAgreement(@Valid @RequestBody SignLoanAgreementRequest request) {
        return ResponseEntity.ok(digitalSignatureService.signLoanAgreement(request));
    }

    @GetMapping("/get-signed-agreement/{customerId}")
    @Operation(summary = "Retrieve signed agreements for a customer")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<DataResult<SignedAgreementResponse>> getSignedAgreement(
            @PathVariable Long customerId,
            @RequestParam(required = false) Long agreementId) {
        return ResponseEntity.ok(digitalSignatureService.getSignedAgreement(customerId, agreementId));
    }

    @GetMapping("/verify-signature/{agreementId}")
    @Operation(summary = "Verify the digital signature of an agreement")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Result> verifySignature(@PathVariable Long agreementId) {
        return ResponseEntity.ok(digitalSignatureService.verifySignature(agreementId));
    }
} 
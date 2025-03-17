package com.banking.webapi.controllers;

import com.banking.business.abstracts.PaymentService;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.dtos.request.ProcessPaymentRequest;
import com.banking.core.dtos.response.PaymentStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Validated
@Tag(name = "Payment", description = "Payment processing operations")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process a new payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Result> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/status/{paymentId}")
    @Operation(summary = "Check payment status")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<DataResult<PaymentStatusResponse>> getPaymentStatus(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(paymentId));
    }

    @PostMapping("/cancel/{paymentId}")
    @Operation(summary = "Cancel a pending payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Result> cancelPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.cancelPayment(paymentId));
    }

    @PostMapping("/refund/{paymentId}")
    @Operation(summary = "Request a refund for a payment")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Result> refundPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, reason));
    }
} 
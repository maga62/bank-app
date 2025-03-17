package com.banking.business.concretes;

import com.banking.business.abstracts.PaymentGatewayService;
import com.banking.business.dtos.request.PaymentRequest;
import com.banking.business.dtos.response.PaymentResponse;
import com.banking.business.dtos.response.PaymentStatusResponse;
import com.banking.business.payment.PaymentProcessor;
import com.banking.business.payment.PaymentProcessorFactory;
import com.banking.entities.Payment;
import com.banking.entities.enums.PaymentProvider;
import com.banking.entities.enums.PaymentStatus;
import com.banking.repositories.abstracts.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentGatewayManager implements PaymentGatewayService {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessorFactory paymentProcessorFactory;
    
    private PaymentProvider currentProvider = PaymentProvider.STRIPE; // Default provider

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // Create payment record
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setCustomerId(request.getCustomerId());
        payment.setApplicationId(request.getApplicationId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setDescription(request.getDescription());
        payment.setProvider(currentProvider);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedDate(LocalDateTime.now());
        
        // Save initial payment record
        payment = paymentRepository.save(payment);
        
        // Process payment with the selected provider
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(currentProvider);
        boolean success = processor.processPayment(payment.getPaymentId(), request);
        
        // Update payment status
        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setUpdatedDate(LocalDateTime.now());
        payment = paymentRepository.save(payment);
        
        // Return response
        return mapToResponse(payment);
    }

    @Override
    public PaymentStatusResponse verifyPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        
        // Get the processor for this payment's provider
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(payment.getProvider());
        
        // Verify status with the provider
        PaymentStatus currentStatus = processor.checkPaymentStatus(paymentId);
        
        // Update status if changed
        if (payment.getStatus() != currentStatus) {
            payment.setStatus(currentStatus);
            payment.setUpdatedDate(LocalDateTime.now());
            paymentRepository.save(payment);
        }
        
        return PaymentStatusResponse.builder()
                .paymentId(paymentId)
                .status(currentStatus.toString())
                .lastUpdated(payment.getUpdatedDate())
                .build();
    }

    @Override
    @Transactional
    public boolean cancelPayment(String paymentId, String reason) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        
        // Check if payment can be cancelled
        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new RuntimeException("Payment cannot be cancelled in status: " + payment.getStatus());
        }
        
        // Get the processor for this payment's provider
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(payment.getProvider());
        
        // Cancel payment with the provider
        boolean success = processor.cancelPayment(paymentId, reason);
        
        if (success) {
            // Update payment status
            payment.setStatus(PaymentStatus.CANCELLED);
            payment.setCancellationReason(reason);
            payment.setUpdatedDate(LocalDateTime.now());
            paymentRepository.save(payment);
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean refundPayment(String paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        
        // Check if payment can be refunded
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        // Check if refund amount is valid
        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new RuntimeException("Refund amount cannot exceed payment amount");
        }
        
        // Get the processor for this payment's provider
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(payment.getProvider());
        
        // Process refund with the provider
        boolean success = processor.refundPayment(paymentId, amount, reason);
        
        if (success) {
            // Update payment status
            payment.setStatus(amount.compareTo(payment.getAmount()) == 0 ? 
                    PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED);
            payment.setRefundedAmount(amount);
            payment.setRefundReason(reason);
            payment.setUpdatedDate(LocalDateTime.now());
            paymentRepository.save(payment);
        }
        
        return success;
    }

    @Override
    public List<PaymentResponse> getPaymentsByCustomerId(Long customerId) {
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByApplicationId(Long applicationId) {
        List<Payment> payments = paymentRepository.findByApplicationId(applicationId);
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatus(status);
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getPaymentsByProvider(PaymentProvider provider) {
        List<Payment> payments = paymentRepository.findByProvider(provider);
        return payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean switchPaymentProvider(PaymentProvider provider) {
        this.currentProvider = provider;
        return true;
    }
    
    // Helper methods
    
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .customerId(payment.getCustomerId())
                .applicationId(payment.getApplicationId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .description(payment.getDescription())
                .provider(payment.getProvider().toString())
                .status(payment.getStatus().toString())
                .createdDate(payment.getCreatedDate())
                .updatedDate(payment.getUpdatedDate())
                .build();
    }
} 
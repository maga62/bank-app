package com.banking.business.payment;

import com.banking.business.dtos.request.PaymentRequest;
import com.banking.entities.enums.PaymentStatus;

import java.math.BigDecimal;

public interface PaymentProcessor {
    
    /**
     * Processes a payment with the payment provider.
     * 
     * @param paymentId The payment ID
     * @param request The payment request
     * @return true if the payment was processed successfully, false otherwise
     */
    boolean processPayment(String paymentId, PaymentRequest request);
    
    /**
     * Checks the status of a payment with the payment provider.
     * 
     * @param paymentId The payment ID
     * @return The current status of the payment
     */
    PaymentStatus checkPaymentStatus(String paymentId);
    
    /**
     * Cancels a payment with the payment provider.
     * 
     * @param paymentId The payment ID
     * @param reason The reason for cancellation
     * @return true if the payment was cancelled successfully, false otherwise
     */
    boolean cancelPayment(String paymentId, String reason);
    
    /**
     * Refunds a payment with the payment provider.
     * 
     * @param paymentId The payment ID
     * @param amount The amount to refund
     * @param reason The reason for the refund
     * @return true if the refund was processed successfully, false otherwise
     */
    boolean refundPayment(String paymentId, BigDecimal amount, String reason);
} 
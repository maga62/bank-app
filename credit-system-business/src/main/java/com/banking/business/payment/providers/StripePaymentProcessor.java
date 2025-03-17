package com.banking.business.payment.providers;

import com.banking.business.dtos.request.PaymentRequest;
import com.banking.business.payment.PaymentProcessor;
import com.banking.entities.enums.PaymentStatus;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {

    @Value("${stripe.api.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public boolean processPayment(String paymentId, PaymentRequest request) {
        try {
            Map<String, Object> chargeParams = new HashMap<>();
            // Stripe expects amount in cents
            long amountInCents = request.getAmount().multiply(new BigDecimal(100)).longValue();
            chargeParams.put("amount", amountInCents);
            chargeParams.put("currency", request.getCurrency().toLowerCase());
            chargeParams.put("source", request.getCardNumber()); // token/source from Stripe.js
            chargeParams.put("description", request.getDescription());
            
            Map<String, String> metadata = new HashMap<>();
            metadata.put("paymentId", paymentId);
            metadata.put("customerId", request.getCustomerId().toString());
            if (request.getApplicationId() != null) {
                metadata.put("applicationId", request.getApplicationId().toString());
            }
            chargeParams.put("metadata", metadata);

            Charge charge = Charge.create(chargeParams);
            return charge.getPaid();
        } catch (StripeException e) {
            log.error("Error processing Stripe payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public PaymentStatus checkPaymentStatus(String paymentId) {
        try {
            Charge charge = Charge.retrieve(paymentId);
            if (charge.getPaid() && !charge.getRefunded()) {
                return PaymentStatus.COMPLETED;
            } else if (charge.getRefunded()) {
                return PaymentStatus.REFUNDED;
            } else if (charge.getStatus().equals("failed")) {
                return PaymentStatus.FAILED;
            } else {
                return PaymentStatus.PENDING;
            }
        } catch (StripeException e) {
            log.error("Error checking Stripe payment status: {}", e.getMessage(), e);
            return PaymentStatus.UNKNOWN;
        }
    }

    @Override
    public boolean cancelPayment(String paymentId, String reason) {
        try {
            Charge charge = Charge.retrieve(paymentId);
            if (!charge.getPaid() || charge.getRefunded()) {
                return false;
            }
            
            Map<String, Object> refundParams = new HashMap<>();
            refundParams.put("charge", paymentId);
            refundParams.put("reason", "requested_by_customer");
            if (reason != null) {
                Map<String, String> metadata = new HashMap<>();
                metadata.put("cancellation_reason", reason);
                refundParams.put("metadata", metadata);
            }

            Refund refund = Refund.create(refundParams);
            return refund.getStatus().equals("succeeded");
        } catch (StripeException e) {
            log.error("Error canceling Stripe payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean refundPayment(String paymentId, BigDecimal amount, String reason) {
        try {
            Charge charge = Charge.retrieve(paymentId);
            if (!charge.getPaid() || charge.getRefunded()) {
                return false;
            }

            Map<String, Object> refundParams = new HashMap<>();
            refundParams.put("charge", paymentId);
            // Convert amount to cents
            long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
            refundParams.put("amount", amountInCents);
            refundParams.put("reason", "requested_by_customer");
            if (reason != null) {
                Map<String, String> metadata = new HashMap<>();
                metadata.put("refund_reason", reason);
                refundParams.put("metadata", metadata);
            }

            Refund refund = Refund.create(refundParams);
            return refund.getStatus().equals("succeeded");
        } catch (StripeException e) {
            log.error("Error refunding Stripe payment: {}", e.getMessage(), e);
            return false;
        }
    }
} 
package com.banking.business.payment.providers;

import com.banking.business.dtos.request.PaymentRequest;
import com.banking.business.payment.PaymentProcessor;
import com.banking.entities.enums.PaymentStatus;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PayPalPaymentProcessor implements PaymentProcessor {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}")
    private String mode;

    private APIContext getApiContext() {
        APIContext context = new APIContext(clientId, clientSecret, mode);
        Map<String, String> config = new HashMap<>();
        config.put("mode", mode);
        context.setConfigurationMap(config);
        return context;
    }

    @Override
    public boolean processPayment(String paymentId, PaymentRequest request) {
        try {
            Payment payment = createPayPalPayment(request);
            for (Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    // Redirect URL for PayPal approval page
                    log.info("PayPal approval URL: {}", links.getHref());
                    return true;
                }
            }
            return false;
        } catch (PayPalRESTException e) {
            log.error("Error processing PayPal payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public PaymentStatus checkPaymentStatus(String paymentId) {
        try {
            Payment payment = Payment.get(getApiContext(), paymentId);
            String state = payment.getState();
            switch (state.toLowerCase()) {
                case "approved":
                    return PaymentStatus.COMPLETED;
                case "created":
                    return PaymentStatus.PENDING;
                case "failed":
                    return PaymentStatus.FAILED;
                case "canceled":
                    return PaymentStatus.CANCELLED;
                default:
                    return PaymentStatus.UNKNOWN;
            }
        } catch (PayPalRESTException e) {
            log.error("Error checking PayPal payment status: {}", e.getMessage(), e);
            return PaymentStatus.UNKNOWN;
        }
    }

    @Override
    public boolean cancelPayment(String paymentId, String reason) {
        try {
            Payment payment = Payment.get(getApiContext(), paymentId);
            if ("created".equals(payment.getState().toLowerCase())) {
                Patch patch = new Patch();
                patch.setOp("replace");
                patch.setPath("/state");
                patch.setValue("canceled");
                payment.update(getApiContext(), Collections.singletonList(patch));
                return true;
            }
            return false;
        } catch (PayPalRESTException e) {
            log.error("Error canceling PayPal payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean refundPayment(String paymentId, BigDecimal amount, String reason) {
        try {
            Payment payment = Payment.get(getApiContext(), paymentId);
            Sale sale = null;
            for (Transaction transaction : payment.getTransactions()) {
                if (transaction.getRelatedResources() != null && !transaction.getRelatedResources().isEmpty()) {
                    sale = transaction.getRelatedResources().get(0).getSale();
                    break;
                }
            }

            if (sale != null) {
                Refund refund = new Refund();
                Amount refundAmount = new Amount();
                refundAmount.setCurrency(payment.getTransactions().get(0).getAmount().getCurrency());
                refundAmount.setTotal(amount.toString());
                refund.setAmount(refundAmount);
                
                sale.refund(getApiContext(), refund);
                return true;
            }
            return false;
        } catch (PayPalRESTException e) {
            log.error("Error refunding PayPal payment: {}", e.getMessage(), e);
            return false;
        }
    }

    private Payment createPayPalPayment(PaymentRequest request) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(request.getCurrency());
        amount.setTotal(request.getAmount().toString());

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(request.getDescription());

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(request.getReturnUrl());
        redirectUrls.setCancelUrl(request.getCancelUrl());
        payment.setRedirectUrls(redirectUrls);

        return payment.create(getApiContext());
    }
} 
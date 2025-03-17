package com.banking.business.payment.providers;

import com.banking.business.dtos.request.PaymentRequest;
import com.banking.business.payment.PaymentProcessor;
import com.banking.entities.enums.PaymentStatus;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class IyzicoPaymentProcessor implements PaymentProcessor {

    @Value("${iyzico.api.key}")
    private String apiKey;

    @Value("${iyzico.secret.key}")
    private String secretKey;

    @Value("${iyzico.base.url:https://sandbox-api.iyzipay.com}")
    private String baseUrl;

    private Options options;

    @PostConstruct
    public void init() {
        options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
    }

    @Override
    public boolean processPayment(String paymentId, PaymentRequest request) {
        try {
            CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
            paymentRequest.setLocale(Locale.TR.getValue());
            paymentRequest.setConversationId(paymentId);
            paymentRequest.setPrice(request.getAmount());
            paymentRequest.setPaidPrice(request.getAmount());
            paymentRequest.setCurrency(Currency.TRY.name());
            paymentRequest.setInstallment(1);
            paymentRequest.setBasketId(paymentId);
            paymentRequest.setPaymentChannel(PaymentChannel.WEB.name());
            paymentRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());

            PaymentCard paymentCard = new PaymentCard();
            paymentCard.setCardHolderName(request.getCardHolderName());
            paymentCard.setCardNumber(request.getCardNumber());
            paymentCard.setExpireMonth(request.getExpiryMonth());
            paymentCard.setExpireYear(request.getExpiryYear());
            paymentCard.setCvc(request.getCvv());
            paymentCard.setRegisterCard(0);
            paymentRequest.setPaymentCard(paymentCard);

            Buyer buyer = new Buyer();
            buyer.setId(request.getCustomerId().toString());
            buyer.setName("John");
            buyer.setSurname("Doe");
            buyer.setGsmNumber("+905350000000");
            buyer.setEmail("email@email.com");
            buyer.setIdentityNumber("74300864791");
            buyer.setRegistrationAddress("Nidakule Göztepe, Merdivenköy Mah. Bora Sok. No:1");
            buyer.setIp(request.getIpAddress());
            buyer.setCity("Istanbul");
            buyer.setCountry("Turkey");
            paymentRequest.setBuyer(buyer);

            Address shippingAddress = new Address();
            shippingAddress.setContactName("Jane Doe");
            shippingAddress.setCity("Istanbul");
            shippingAddress.setCountry("Turkey");
            shippingAddress.setAddress("Nidakule Göztepe, Merdivenköy Mah. Bora Sok. No:1");
            paymentRequest.setShippingAddress(shippingAddress);

            Address billingAddress = new Address();
            billingAddress.setContactName("Jane Doe");
            billingAddress.setCity("Istanbul");
            billingAddress.setCountry("Turkey");
            billingAddress.setAddress("Nidakule Göztepe, Merdivenköy Mah. Bora Sok. No:1");
            paymentRequest.setBillingAddress(billingAddress);

            List<BasketItem> basketItems = new ArrayList<>();
            BasketItem basketItem = new BasketItem();
            basketItem.setId("BI101");
            basketItem.setName("Credit Payment");
            basketItem.setCategory1("Credit");
            basketItem.setItemType(BasketItemType.VIRTUAL.name());
            basketItem.setPrice(request.getAmount());
            basketItems.add(basketItem);
            paymentRequest.setBasketItems(basketItems);

            Payment payment = Payment.create(paymentRequest, options);

            return payment.getStatus().equals("success");
        } catch (Exception e) {
            log.error("Error processing iyzico payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public PaymentStatus checkPaymentStatus(String paymentId) {
        try {
            RetrievePaymentRequest retrieveRequest = new RetrievePaymentRequest();
            retrieveRequest.setLocale(Locale.TR.getValue());
            retrieveRequest.setConversationId(paymentId);
            retrieveRequest.setPaymentId(paymentId);

            Payment payment = Payment.retrieve(retrieveRequest, options);

            switch (payment.getStatus()) {
                case "SUCCESS":
                    return PaymentStatus.COMPLETED;
                case "FAILURE":
                    return PaymentStatus.FAILED;
                case "INIT_SUCCEEDED":
                case "PENDING":
                    return PaymentStatus.PENDING;
                default:
                    return PaymentStatus.UNKNOWN;
            }
        } catch (Exception e) {
            log.error("Error checking iyzico payment status: {}", e.getMessage(), e);
            return PaymentStatus.UNKNOWN;
        }
    }

    @Override
    public boolean cancelPayment(String paymentId, String reason) {
        try {
            CreateCancelRequest cancelRequest = new CreateCancelRequest();
            cancelRequest.setLocale(Locale.TR.getValue());
            cancelRequest.setConversationId(paymentId);
            cancelRequest.setPaymentId(paymentId);
            cancelRequest.setIp("127.0.0.1");
            cancelRequest.setReason(RefundReason.BUYER_REQUEST);
            cancelRequest.setDescription("Customer requested cancellation");

            Cancel cancel = Cancel.create(cancelRequest, options);

            return cancel.getStatus().equals("success");
        } catch (Exception e) {
            log.error("Error canceling iyzico payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean refundPayment(String paymentId, BigDecimal amount, String reason) {
        try {
            CreateRefundRequest refundRequest = new CreateRefundRequest();
            refundRequest.setLocale(Locale.TR.getValue());
            refundRequest.setConversationId(paymentId);
            refundRequest.setPaymentTransactionId(paymentId);
            refundRequest.setPrice(amount);
            refundRequest.setCurrency(Currency.TRY.name());
            refundRequest.setIp("127.0.0.1");
            refundRequest.setReason(RefundReason.BUYER_REQUEST);
            refundRequest.setDescription("Customer requested refund");

            Refund refund = Refund.create(refundRequest, options);

            return refund.getStatus().equals("success");
        } catch (Exception e) {
            log.error("Error refunding iyzico payment: {}", e.getMessage(), e);
            return false;
        }
    }
} 
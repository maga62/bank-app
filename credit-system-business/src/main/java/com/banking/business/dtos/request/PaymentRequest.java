package com.banking.business.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ödeme isteği için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotNull(message = "Müşteri ID boş olamaz")
    private Long customerId;
    
    private Long applicationId;
    
    @NotNull(message = "Ödeme tutarı boş olamaz")
    @Positive(message = "Ödeme tutarı pozitif olmalıdır")
    private BigDecimal amount;
    
    @NotBlank(message = "Ödeme türü boş olamaz")
    private String paymentType;
    
    @NotBlank(message = "Ödeme sağlayıcısı boş olamaz")
    private String paymentProvider;
    
    private String cardNumber;
    
    private String cardHolderName;
    
    private String expiryMonth;
    
    private String expiryYear;
    
    private String cvv;
    
    private String accountNumber;
    
    private String iban;
    
    private String description;
    
    private String currency;
    
    private String returnUrl;
    
    private String cancelUrl;
    
    private String ipAddress;
    
    private String userAgent;
} 
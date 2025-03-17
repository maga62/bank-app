package com.banking.business.abstracts;

import com.banking.business.dtos.request.PaymentRequest;
import com.banking.business.dtos.response.PaymentResponse;
import com.banking.business.dtos.response.PaymentStatusResponse;
import com.banking.entities.enums.PaymentProvider;
import com.banking.entities.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ödeme işlemleri için servis arayüzü.
 * Farklı ödeme sağlayıcıları (PayPal, Stripe, iyzico) ile entegrasyon sağlar.
 */
public interface PaymentGatewayService {
    
    /**
     * Ödeme işlemini gerçekleştirir.
     * 
     * @param request Ödeme isteği
     * @return Ödeme yanıtı
     */
    PaymentResponse processPayment(PaymentRequest request);
    
    /**
     * Ödeme durumunu kontrol eder.
     * 
     * @param paymentId Ödeme ID
     * @return Ödeme durumu yanıtı
     */
    PaymentStatusResponse verifyPaymentStatus(String paymentId);
    
    /**
     * Ödemeyi iptal eder.
     * 
     * @param paymentId Ödeme ID
     * @param reason İptal nedeni
     * @return İptal başarılıysa true, değilse false
     */
    boolean cancelPayment(String paymentId, String reason);
    
    /**
     * Ödeme iadesi yapar.
     * 
     * @param paymentId Ödeme ID
     * @param amount İade tutarı
     * @param reason İade nedeni
     * @return İade başarılıysa true, değilse false
     */
    boolean refundPayment(String paymentId, BigDecimal amount, String reason);
    
    /**
     * Belirli bir müşteriye ait ödemeleri getirir.
     * 
     * @param customerId Müşteri ID
     * @return Ödeme yanıtları listesi
     */
    List<PaymentResponse> getPaymentsByCustomerId(Long customerId);
    
    /**
     * Belirli bir kredi başvurusuna ait ödemeleri getirir.
     * 
     * @param applicationId Kredi başvuru ID
     * @return Ödeme yanıtları listesi
     */
    List<PaymentResponse> getPaymentsByApplicationId(Long applicationId);
    
    /**
     * Belirli bir duruma sahip ödemeleri getirir.
     * 
     * @param status Ödeme durumu
     * @return Ödeme yanıtları listesi
     */
    List<PaymentResponse> getPaymentsByStatus(PaymentStatus status);
    
    /**
     * Belirli bir sağlayıcıya ait ödemeleri getirir.
     * 
     * @param provider Ödeme sağlayıcısı
     * @return Ödeme yanıtları listesi
     */
    List<PaymentResponse> getPaymentsByProvider(PaymentProvider provider);
    
    /**
     * Ödeme sağlayıcısını değiştirir.
     * 
     * @param provider Yeni ödeme sağlayıcısı
     * @return Değişiklik başarılıysa true, değilse false
     */
    boolean switchPaymentProvider(PaymentProvider provider);
} 
package com.banking.core.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SMS gönderimi için kullanılan servis.
 */
@Service
@Slf4j
public class SmsService {

    @Value("${sms.service.enabled:true}")
    private boolean smsServiceEnabled;
    
    @Value("${sms.service.provider:default}")
    private String smsProvider;
    
    /**
     * SMS gönderir.
     * 
     * @param phoneNumber Telefon numarası
     * @param message Mesaj
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendSms(String phoneNumber, String message) {
        if (!smsServiceEnabled) {
            log.info("SMS service is disabled. SMS not sent to: {}", phoneNumber);
            return false;
        }
        
        try {
            // Gerçek uygulamada burada SMS sağlayıcısı üzerinden SMS gönderimi yapılır
            // Örneğin Twilio, Nexmo, MessageBird vb. kullanılabilir
            
            log.info("SMS sent to: {}, provider: {}", phoneNumber, smsProvider);
            log.debug("SMS content: {}", message);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", phoneNumber, e);
            return false;
        }
    }
    
    /**
     * OTP (Tek Kullanımlık Şifre) SMS'i gönderir.
     * 
     * @param phoneNumber Telefon numarası
     * @param otp OTP kodu
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendOtpSms(String phoneNumber, String otp) {
        String message = "Tek kullanımlık şifreniz: " + otp + ". Bu şifre 5 dakika süreyle geçerlidir.";
        return sendSms(phoneNumber, message);
    }
} 
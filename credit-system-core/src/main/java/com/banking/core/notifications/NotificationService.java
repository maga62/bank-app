package com.banking.core.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Bildirim servisi.
 * SMS ve e-posta bildirimleri göndermek için kullanılır.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    
    @Value("${notifications.enabled:true}")
    private boolean notificationsEnabled;
    
    /**
     * E-posta bildirimi gönderir.
     * 
     * @param to Alıcı e-posta adresi
     * @param subject Konu
     * @param content İçerik
     * @param templateVariables Şablon değişkenleri
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendEmail(String to, String subject, String content, Map<String, Object> templateVariables) {
        if (!notificationsEnabled) {
            log.info("Notifications disabled. Email not sent to: {}", to);
            return false;
        }
        
        try {
            log.info("Sending email to: {}, subject: {}", to, subject);
            return emailService.sendEmail(to, subject, content, templateVariables);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            return false;
        }
    }
    
    /**
     * SMS bildirimi gönderir.
     * 
     * @param phoneNumber Telefon numarası
     * @param message Mesaj
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendSms(String phoneNumber, String message) {
        if (!notificationsEnabled) {
            log.info("Notifications disabled. SMS not sent to: {}", phoneNumber);
            return false;
        }
        
        try {
            log.info("Sending SMS to: {}", phoneNumber);
            return smsService.sendSms(phoneNumber, message);
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", phoneNumber, e);
            return false;
        }
    }
    
    /**
     * Kredi başvurusu durumu değişikliği bildirimi gönderir.
     * 
     * @param email E-posta adresi
     * @param phoneNumber Telefon numarası
     * @param customerName Müşteri adı
     * @param applicationId Başvuru ID
     * @param status Yeni durum
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendCreditApplicationStatusNotification(String email, String phoneNumber, 
                                                         String customerName, Long applicationId, 
                                                         String status) {
        // E-posta bildirimi
        String subject = "Kredi Başvurunuz Güncellendi";
        String content = "Sayın " + customerName + ", " + applicationId + " numaralı kredi başvurunuzun durumu " + status + " olarak güncellenmiştir.";
        
        Map<String, Object> templateVariables = Map.of(
                "customerName", customerName,
                "applicationId", applicationId,
                "status", status
        );
        
        boolean emailSent = sendEmail(email, subject, content, templateVariables);
        
        // SMS bildirimi
        String smsMessage = "Sayın " + customerName + ", " + applicationId + " numaralı kredi başvurunuzun durumu " + status + " olarak güncellenmiştir.";
        boolean smsSent = sendSms(phoneNumber, smsMessage);
        
        return emailSent || smsSent;
    }
    
    /**
     * Ödeme hatırlatma bildirimi gönderir.
     * 
     * @param email E-posta adresi
     * @param phoneNumber Telefon numarası
     * @param customerName Müşteri adı
     * @param paymentAmount Ödeme tutarı
     * @param dueDate Son ödeme tarihi
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendPaymentReminderNotification(String email, String phoneNumber, 
                                                 String customerName, double paymentAmount, 
                                                 String dueDate) {
        // E-posta bildirimi
        String subject = "Ödeme Hatırlatması";
        String content = "Sayın " + customerName + ", " + dueDate + " tarihinde " + paymentAmount + " TL tutarında ödemeniz bulunmaktadır.";
        
        Map<String, Object> templateVariables = Map.of(
                "customerName", customerName,
                "paymentAmount", paymentAmount,
                "dueDate", dueDate
        );
        
        boolean emailSent = sendEmail(email, subject, content, templateVariables);
        
        // SMS bildirimi
        String smsMessage = "Sayın " + customerName + ", " + dueDate + " tarihinde " + paymentAmount + " TL tutarında ödemeniz bulunmaktadır.";
        boolean smsSent = sendSms(phoneNumber, smsMessage);
        
        return emailSent || smsSent;
    }
} 
package com.banking.core.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * E-posta gönderimi için kullanılan servis.
 */
@Service
@Slf4j
public class EmailService {

    @Value("${email.service.enabled:true}")
    private boolean emailServiceEnabled;
    
    @Value("${email.service.from:no-reply@banking.com}")
    private String defaultFromAddress;
    
    /**
     * E-posta gönderir.
     * 
     * @param to Alıcı e-posta adresi
     * @param subject Konu
     * @param content İçerik
     * @param templateVariables Şablon değişkenleri
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendEmail(String to, String subject, String content, Map<String, Object> templateVariables) {
        if (!emailServiceEnabled) {
            log.info("Email service is disabled. Email not sent to: {}", to);
            return false;
        }
        
        try {
            // Gerçek uygulamada burada SMTP sunucusu üzerinden e-posta gönderimi yapılır
            // Örneğin JavaMail API, Spring Mail, SendGrid, Amazon SES vb. kullanılabilir
            
            log.info("Email sent to: {}, subject: {}", to, subject);
            log.debug("Email content: {}", content);
            log.debug("Template variables: {}", templateVariables);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            return false;
        }
    }
    
    /**
     * HTML formatında e-posta gönderir.
     * 
     * @param to Alıcı e-posta adresi
     * @param subject Konu
     * @param htmlContent HTML içerik
     * @param templateVariables Şablon değişkenleri
     * @return Gönderim başarılı ise true, değilse false
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlContent, Map<String, Object> templateVariables) {
        if (!emailServiceEnabled) {
            log.info("Email service is disabled. HTML email not sent to: {}", to);
            return false;
        }
        
        try {
            // Gerçek uygulamada burada SMTP sunucusu üzerinden HTML e-posta gönderimi yapılır
            
            log.info("HTML email sent to: {}, subject: {}", to, subject);
            log.debug("HTML content: {}", htmlContent);
            log.debug("Template variables: {}", templateVariables);
            
            return true;
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            return false;
        }
    }
} 
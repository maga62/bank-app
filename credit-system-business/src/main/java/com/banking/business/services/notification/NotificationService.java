package com.banking.business.services.notification;

import com.banking.entities.CreditApplication;
import com.banking.entities.Customer;
import com.banking.entities.enums.CreditApplicationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;


/**
 * Bildirim servisi.
 * SMS ve e-posta bildirimleri gönderir.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    /**
     * Kredi başvurusu durum değişikliği bildirimi gönderir
     * 
     * @param application Kredi başvurusu
     * @param oldStatus Eski durum
     * @param newStatus Yeni durum
     */
    public void sendApplicationStatusChangeNotification(
            CreditApplication application, 
            CreditApplicationStatus oldStatus, 
            CreditApplicationStatus newStatus) {
        
        Customer customer = application.getCustomer();
        
        // E-posta bildirimi gönder
        sendEmail(customer.getEmail(), 
                "Kredi Başvurunuz Güncellendi", 
                createStatusChangeEmailContent(application, oldStatus, newStatus));
        
        // SMS bildirimi gönder
        sendSms(customer.getPhoneNumber(), 
                createStatusChangeSmsContent(application, newStatus));
        
        log.info("Status change notification sent to customer: {}, application: {}, status: {} -> {}", 
                customer.getCustomerNumber(), application.getId(), oldStatus, newStatus);
    }
    
    /**
     * Kredi başvurusu onay bildirimi gönderir
     * 
     * @param application Onaylanan kredi başvurusu
     */
    public void sendApplicationApprovalNotification(CreditApplication application) {
        Customer customer = application.getCustomer();
        
        // E-posta bildirimi gönder
        sendEmail(customer.getEmail(), 
                "Kredi Başvurunuz Onaylandı", 
                createApprovalEmailContent(application));
        
        // SMS bildirimi gönder
        sendSms(customer.getPhoneNumber(), 
                createApprovalSmsContent(application));
        
        log.info("Approval notification sent to customer: {}, application: {}", 
                customer.getCustomerNumber(), application.getId());
    }
    
    /**
     * Kredi başvurusu red bildirimi gönderir
     * 
     * @param application Reddedilen kredi başvurusu
     */
    public void sendApplicationRejectionNotification(CreditApplication application) {
        Customer customer = application.getCustomer();
        
        // E-posta bildirimi gönder
        sendEmail(customer.getEmail(), 
                "Kredi Başvurunuz Hakkında Bilgilendirme", 
                createRejectionEmailContent(application));
        
        // SMS bildirimi gönder
        sendSms(customer.getPhoneNumber(), 
                createRejectionSmsContent(application));
        
        log.info("Rejection notification sent to customer: {}, application: {}", 
                customer.getCustomerNumber(), application.getId());
    }
    
    /**
     * Kampanya bildirimi gönderir
     * 
     * @param customer Müşteri
     * @param campaignId Kampanya ID
     * @param campaignName Kampanya adı
     * @param campaignDescription Kampanya açıklaması
     */
    public void sendCampaignNotification(
            Customer customer, 
            String campaignId, 
            String campaignName, 
            String campaignDescription) {
        
        // E-posta bildirimi gönder
        sendEmail(customer.getEmail(), 
                "Size Özel Kredi Fırsatı: " + campaignName, 
                createCampaignEmailContent(customer, campaignName, campaignDescription));
        
        // SMS bildirimi gönder
        sendSms(customer.getPhoneNumber(), 
                createCampaignSmsContent(campaignName));
        
        log.info("Campaign notification sent to customer: {}, campaign: {}", 
                customer.getCustomerNumber(), campaignId);
    }
    
    /**
     * E-posta gönderir
     * 
     * @param to Alıcı e-posta adresi
     * @param subject Konu
     * @param content İçerik
     */
    private void sendEmail(String to, String subject, String content) {
        // Gerçek uygulamada burada e-posta gönderme işlemi yapılır
        // Örneğin: JavaMailSender, Amazon SES, SendGrid vb. kullanılabilir
        
        log.info("Email sent to: {}, subject: {}", to, subject);
        log.debug("Email content: {}", content);
    }
    
    /**
     * SMS gönderir
     * 
     * @param phoneNumber Telefon numarası
     * @param message Mesaj
     */
    private void sendSms(String phoneNumber, String message) {
        // Gerçek uygulamada burada SMS gönderme işlemi yapılır
        // Örneğin: Twilio, Nexmo, MessageBird vb. kullanılabilir
        
        log.info("SMS sent to: {}", phoneNumber);
        log.debug("SMS content: {}", message);
    }
    
    /**
     * Durum değişikliği e-posta içeriği oluşturur
     */
    private String createStatusChangeEmailContent(
            CreditApplication application, 
            CreditApplicationStatus oldStatus, 
            CreditApplicationStatus newStatus) {
        
        StringBuilder content = new StringBuilder();
        content.append("Sayın ").append(application.getCustomer().getEmail()).append(",\n\n");
        content.append("Kredi başvurunuzun durumu güncellendi.\n\n");
        content.append("Başvuru Numarası: ").append(application.getId()).append("\n");
        content.append("Eski Durum: ").append(getStatusDisplayName(oldStatus)).append("\n");
        content.append("Yeni Durum: ").append(getStatusDisplayName(newStatus)).append("\n\n");
        
        if (newStatus == CreditApplicationStatus.APPROVED) {
            content.append("Tebrikler! Kredi başvurunuz onaylandı. Detaylar için lütfen hesabınızı kontrol edin.\n\n");
        } else if (newStatus == CreditApplicationStatus.REJECTED) {
            content.append("Üzgünüz, kredi başvurunuz reddedildi. Detaylar için lütfen hesabınızı kontrol edin.\n\n");
        } else if (newStatus == CreditApplicationStatus.IN_REVIEW) {
            content.append("Başvurunuz inceleme aşamasındadır. En kısa sürede size bilgi vereceğiz.\n\n");
        } else if (newStatus == CreditApplicationStatus.PENDING_APPROVAL) {
            content.append("Başvurunuz ön değerlendirmeden geçti ve onay aşamasındadır.\n\n");
        }
        
        content.append("Başvurunuzun durumunu web sitemizden veya mobil uygulamamızdan takip edebilirsiniz.\n\n");
        content.append("Saygılarımızla,\n");
        content.append("Banka Kredi Ekibi");
        
        return content.toString();
    }
    
    /**
     * Durum değişikliği SMS içeriği oluşturur
     */
    private String createStatusChangeSmsContent(
            CreditApplication application, 
            CreditApplicationStatus newStatus) {
        
        StringBuilder content = new StringBuilder();
        content.append("Kredi basvurunuzun durumu: ").append(getStatusDisplayName(newStatus));
        content.append(". Basvuru no: ").append(application.getId());
        
        if (newStatus == CreditApplicationStatus.APPROVED) {
            content.append(". Tebrikler!");
        } else if (newStatus == CreditApplicationStatus.REJECTED) {
            content.append(". Detaylar icin hesabinizi kontrol edin.");
        }
        
        return content.toString();
    }
    
    /**
     * Onay e-posta içeriği oluşturur
     */
    private String createApprovalEmailContent(CreditApplication application) {
        StringBuilder content = new StringBuilder();
        content.append("Sayın ").append(application.getCustomer().getEmail()).append(",\n\n");
        content.append("Kredi başvurunuz onaylandı. Tebrikler!\n\n");
        content.append("Başvuru Numarası: ").append(application.getId()).append("\n");
        content.append("Kredi Tutarı: ").append(application.getAmount()).append(" TL\n");
        content.append("Vade: ").append(application.getTermMonths()).append(" ay\n");
        
        if (application.getApprovalDate() != null) {
            content.append("Onay Tarihi: ").append(application.getApprovalDate().format(DATE_FORMATTER)).append("\n\n");
        }
        
        content.append("Kredi sözleşmenizi imzalamak için lütfen en yakın şubemizi ziyaret edin.\n\n");
        content.append("Saygılarımızla,\n");
        content.append("Banka Kredi Ekibi");
        
        return content.toString();
    }
    
    /**
     * Onay SMS içeriği oluşturur
     */
    private String createApprovalSmsContent(CreditApplication application) {
        return "Tebrikler! " + application.getAmount() + " TL tutarindaki kredi basvurunuz onaylandi. " +
               "Basvuru no: " + application.getId() + ". Detaylar e-posta adresinize gonderildi.";
    }
    
    /**
     * Red e-posta içeriği oluşturur
     */
    private String createRejectionEmailContent(CreditApplication application) {
        StringBuilder content = new StringBuilder();
        content.append("Sayın ").append(application.getCustomer().getEmail()).append(",\n\n");
        content.append("Kredi başvurunuz değerlendirilmiş olup, maalesef onaylanamamıştır.\n\n");
        content.append("Başvuru Numarası: ").append(application.getId()).append("\n");
        
        if (application.getRejectionDate() != null) {
            content.append("Değerlendirme Tarihi: ").append(application.getRejectionDate().format(DATE_FORMATTER)).append("\n\n");
        }
        
        content.append("Başvurunuzun değerlendirilmesi sırasında birçok faktör göz önünde bulundurulmuştur. ");
        content.append("Daha fazla bilgi için lütfen müşteri hizmetlerimizi arayın veya en yakın şubemizi ziyaret edin.\n\n");
        content.append("Anlayışınız için teşekkür ederiz.\n\n");
        content.append("Saygılarımızla,\n");
        content.append("Banka Kredi Ekibi");
        
        return content.toString();
    }
    
    /**
     * Red SMS içeriği oluşturur
     */
    private String createRejectionSmsContent(CreditApplication application) {
        return "Kredi basvurunuz (No: " + application.getId() + ") degerlendirilmis olup, " +
               "onaylanamamistir. Detaylar icin e-postanizi kontrol edin veya musteri hizmetlerimizi arayin.";
    }
    
    /**
     * Kampanya e-posta içeriği oluşturur
     */
    private String createCampaignEmailContent(
            Customer customer, 
            String campaignName, 
            String campaignDescription) {
        
        StringBuilder content = new StringBuilder();
        content.append("Sayın ").append(customer.getEmail()).append(",\n\n");
        content.append("Size özel kredi fırsatımız var!\n\n");
        content.append("Kampanya: ").append(campaignName).append("\n\n");
        content.append(campaignDescription).append("\n\n");
        content.append("Bu özel fırsattan yararlanmak için web sitemizi ziyaret edin veya mobil uygulamamızı kullanın.\n\n");
        content.append("Saygılarımızla,\n");
        content.append("Banka Kampanya Ekibi");
        
        return content.toString();
    }
    
    /**
     * Kampanya SMS içeriği oluşturur
     */
    private String createCampaignSmsContent(String campaignName) {
        return "Size ozel firsat: " + campaignName + ". Detaylar icin web sitemizi ziyaret edin veya mobil uygulamamizi kullanin.";
    }
    
    /**
     * Durum adını görüntülenecek şekilde döndürür
     */
    private String getStatusDisplayName(CreditApplicationStatus status) {
        switch (status) {
            case PENDING:
                return "Beklemede";
            case IN_REVIEW:
                return "İnceleniyor";
            case PENDING_APPROVAL:
                return "Onay Bekliyor";
            case APPROVED:
                return "Onaylandı";
            case REJECTED:
                return "Reddedildi";
            case CANCELLED:
                return "İptal Edildi";
            default:
                return status.name();
        }
    }
} 
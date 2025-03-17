package com.banking.business.services.tracking;

import com.banking.entities.CreditApplication;
import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.repositories.abstracts.CreditApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kredi başvuru takip servisi.
 * Başvuruların her aşamasını (başvuru alındı, değerlendirme, onay, ödeme) izler.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditApplicationTrackingService {

    private final CreditApplicationRepository creditApplicationRepository;
    
    /**
     * Müşterinin tüm kredi başvurularını durumlarına göre gruplandırır
     * 
     * @param customerId Müşteri ID
     * @return Durumlara göre gruplandırılmış kredi başvuruları
     */
    public Map<CreditApplicationStatus, List<CreditApplication>> getApplicationsByStatus(Long customerId) {
        List<CreditApplication> applications = creditApplicationRepository.findAll().stream()
                .filter(app -> app.getCustomer().getId().equals(customerId) && app.getDeletedDate() == null)
                .collect(Collectors.toList());
        
        return applications.stream()
                .collect(Collectors.groupingBy(app -> CreditApplicationStatus.valueOf(app.getStatus().name())));
    }
    
    /**
     * Belirli bir kredi başvurusunun aşama geçmişini oluşturur
     * 
     * @param applicationId Kredi başvurusu ID
     * @return Aşama geçmişi
     */
    public List<Map<String, Object>> getApplicationStageHistory(Long applicationId) {
        CreditApplication application = creditApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found: " + applicationId));
        
        List<Map<String, Object>> stageHistory = new ArrayList<>();
        
        // Başvuru alındı aşaması
        if (application.getCreatedDate() != null) {
            Map<String, Object> applicationReceivedStage = new HashMap<>();
            applicationReceivedStage.put("stage", "APPLICATION_RECEIVED");
            applicationReceivedStage.put("date", application.getCreatedDate());
            applicationReceivedStage.put("description", "Kredi başvurusu alındı");
            stageHistory.add(applicationReceivedStage);
        }
        
        // Değerlendirme aşaması
        if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.IN_REVIEW.name()) || 
                application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name()) ||
                application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name()) ||
                application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.REJECTED.name())) {
            
            Map<String, Object> inReviewStage = new HashMap<>();
            inReviewStage.put("stage", "IN_REVIEW");
            // Değerlendirme tarihi olarak başvuru tarihinden 1 gün sonrasını varsayalım
            inReviewStage.put("date", application.getCreatedDate().plus(1, ChronoUnit.DAYS));
            inReviewStage.put("description", "Kredi başvurusu değerlendirme aşamasında");
            stageHistory.add(inReviewStage);
        }
        
        // Onay bekliyor aşaması
        if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name()) ||
                application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name()) ||
                application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.REJECTED.name())) {
            
            Map<String, Object> pendingApprovalStage = new HashMap<>();
            pendingApprovalStage.put("stage", "PENDING_APPROVAL");
            // Onay bekleme tarihi olarak başvuru tarihinden 2 gün sonrasını varsayalım
            pendingApprovalStage.put("date", application.getCreatedDate().plus(2, ChronoUnit.DAYS));
            pendingApprovalStage.put("description", "Kredi başvurusu onay bekliyor");
            stageHistory.add(pendingApprovalStage);
        }
        
        // Onay aşaması
        if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name())) {
            Map<String, Object> approvedStage = new HashMap<>();
            approvedStage.put("stage", "APPROVED");
            approvedStage.put("date", application.getApprovalDate() != null ? 
                    application.getApprovalDate() : application.getCreatedDate().plus(3, ChronoUnit.DAYS));
            approvedStage.put("description", "Kredi başvurusu onaylandı");
            stageHistory.add(approvedStage);
        }
        
        // Red aşaması
        if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.REJECTED.name())) {
            Map<String, Object> rejectedStage = new HashMap<>();
            rejectedStage.put("stage", "REJECTED");
            rejectedStage.put("date", application.getRejectionDate() != null ? 
                    application.getRejectionDate() : application.getCreatedDate().plus(3, ChronoUnit.DAYS));
            rejectedStage.put("description", "Kredi başvurusu reddedildi");
            stageHistory.add(rejectedStage);
        }
        
        // İptal aşaması
        if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.CANCELLED.name())) {
            Map<String, Object> cancelledStage = new HashMap<>();
            cancelledStage.put("stage", "CANCELLED");
            // İptal tarihi olarak son güncelleme tarihini varsayalım
            cancelledStage.put("date", application.getUpdatedDate() != null ? 
                    application.getUpdatedDate() : application.getCreatedDate().plus(1, ChronoUnit.DAYS));
            cancelledStage.put("description", "Kredi başvurusu iptal edildi");
            stageHistory.add(cancelledStage);
        }
        
        return stageHistory;
    }
    
    /**
     * Belirli bir durumdaki tüm kredi başvurularını getirir
     * 
     * @param status Kredi başvurusu durumu
     * @return Belirtilen durumdaki kredi başvuruları
     */
    public List<CreditApplication> getApplicationsByStatus(CreditApplicationStatus status) {
        return creditApplicationRepository.findAll().stream()
                .filter(app -> app.getStatus() == CreditApplication.Status.valueOf(status.name()) && app.getDeletedDate() == null)
                .collect(Collectors.toList());
    }
    
    /**
     * Belirli bir süre içinde işlem yapılmamış kredi başvurularını getirir
     * 
     * @param days Gün sayısı
     * @return İşlem yapılmamış kredi başvuruları
     */
    public List<CreditApplication> getStaleApplications(int days) {
        LocalDateTime threshold = LocalDateTime.now().minus(days, ChronoUnit.DAYS);
        
        return creditApplicationRepository.findAll().stream()
                .filter(app -> (app.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING.name()) || 
                               app.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.IN_REVIEW.name()) || 
                               app.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.PENDING_APPROVAL.name())) && 
                               app.getDeletedDate() == null &&
                               app.getUpdatedDate().isBefore(threshold))
                .collect(Collectors.toList());
    }
    
    /**
     * Kredi başvurusunun durumunu günceller ve geçmiş kaydı oluşturur
     * 
     * @param applicationId Kredi başvurusu ID
     * @param newStatus Yeni durum
     * @param notes Notlar
     * @return Güncellenmiş kredi başvurusu
     */
    public CreditApplication updateApplicationStatus(Long applicationId, CreditApplicationStatus newStatus, String notes) {
        CreditApplication application = creditApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found: " + applicationId));
        
        CreditApplicationStatus oldStatus = CreditApplicationStatus.valueOf(application.getStatus().name());
        application.setStatus(CreditApplication.Status.valueOf(newStatus.name()));
        
        // Durum değişikliğine göre tarih alanlarını güncelle
        if (newStatus == CreditApplicationStatus.APPROVED) {
            application.setApprovalDate(LocalDateTime.now());
        } else if (newStatus == CreditApplicationStatus.REJECTED) {
            application.setRejectionDate(LocalDateTime.now());
        }
        
        // Notları güncelle
        if (notes != null && !notes.isEmpty()) {
            String existingNotes = application.getNotes();
            if (existingNotes != null && !existingNotes.isEmpty()) {
                application.setNotes(existingNotes + "\n" + notes);
            } else {
                application.setNotes(notes);
            }
        }
        
        // Güncelleme tarihini ayarla
        application.setUpdatedDate(LocalDateTime.now());
        
        log.info("Credit application status updated. ID: {}, Old status: {}, New status: {}", 
                applicationId, oldStatus, newStatus);
        
        return creditApplicationRepository.save(application);
    }
    
    /**
     * Kredi başvurusunun işlem süresini hesaplar
     * 
     * @param applicationId Kredi başvurusu ID
     * @return İşlem süresi (gün cinsinden)
     */
    public long calculateProcessingTime(Long applicationId) {
        CreditApplication application = creditApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit application not found: " + applicationId));
        
        LocalDateTime startDate = application.getCreatedDate();
        LocalDateTime endDate;
        
        if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.APPROVED.name())) {
            endDate = application.getApprovalDate();
        } else if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.REJECTED.name())) {
            endDate = application.getRejectionDate();
        } else if (application.getStatus() == CreditApplication.Status.valueOf(CreditApplicationStatus.CANCELLED.name())) {
            endDate = application.getUpdatedDate();
        } else {
            // Başvuru hala işlemde, şu ana kadar geçen süreyi hesapla
            endDate = LocalDateTime.now();
        }
        
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
} 
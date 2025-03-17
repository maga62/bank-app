package com.banking.business.abstracts;

import com.banking.entities.DigitalSignature;
import com.banking.business.dtos.response.LoanAgreementResponse;
import com.banking.business.dtos.request.LoanAgreementSignRequest;

import java.util.List;
import java.util.Optional;

/**
 * Kredi sözleşmesi yönetimi için servis arayüzü.
 * Dijital imza onayları, sözleşme oluşturma ve yönetme işlemlerini içerir.
 */
public interface LoanAgreementService {
    
    /**
     * Kredi başvurusu için sözleşme oluşturur.
     * 
     * @param applicationId Kredi başvuru ID
     * @return Oluşturulan sözleşme yanıtı
     */
    LoanAgreementResponse createAgreement(Long applicationId);
    
    /**
     * Kredi sözleşmesini dijital olarak imzalar.
     * 
     * @param request İmza isteği
     * @return İmzalanan dijital sözleşme
     */
    DigitalSignature signLoanAgreement(LoanAgreementSignRequest request);
    
    /**
     * Belirli bir müşteriye ait tüm imzalı sözleşmeleri getirir.
     * 
     * @param customerId Müşteri ID
     * @return İmzalı sözleşmeler listesi
     */
    List<LoanAgreementResponse> getSignedAgreementsByCustomerId(Long customerId);
    
    /**
     * Belirli bir kredi başvurusuna ait imzalı sözleşmeyi getirir.
     * 
     * @param applicationId Kredi başvuru ID
     * @return İmzalı sözleşme, yoksa boş Optional
     */
    Optional<LoanAgreementResponse> getSignedAgreementByApplicationId(Long applicationId);
    
    /**
     * Sözleşmenin imzalanıp imzalanmadığını kontrol eder.
     * 
     * @param applicationId Kredi başvuru ID
     * @return Sözleşme imzalanmışsa true, değilse false
     */
    boolean isAgreementSigned(Long applicationId);
    
    /**
     * Sözleşme PDF dosyasını oluşturur.
     * 
     * @param applicationId Kredi başvuru ID
     * @return PDF dosyasının byte dizisi
     */
    byte[] generateAgreementPdf(Long applicationId);
    
    /**
     * İmzalı sözleşmeyi arşivler.
     * 
     * @param signatureId Dijital imza ID
     * @return Arşivleme başarılıysa true, değilse false
     */
    boolean archiveSignedAgreement(Long signatureId);
} 
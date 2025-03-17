package com.banking.business.rules;

import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.entities.CreditApplication;
import com.banking.entities.Customer;
import com.banking.entities.DigitalSignature;
import com.banking.repositories.abstracts.CreditApplicationRepository;
import com.banking.repositories.abstracts.DigitalSignatureRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Kredi sözleşmesi işlemleri için iş kuralları.
 */
@Service
public class LoanAgreementBusinessRules extends BaseBusinessRules<DigitalSignature, Long> {

    private final CreditApplicationRepository creditApplicationRepository;
    private final DigitalSignatureRepository digitalSignatureRepository;
    
    public LoanAgreementBusinessRules(DigitalSignatureRepository digitalSignatureRepository, 
                                     CreditApplicationRepository creditApplicationRepository) {
        super(digitalSignatureRepository);
        this.digitalSignatureRepository = digitalSignatureRepository;
        this.creditApplicationRepository = creditApplicationRepository;
    }
    
    /**
     * Kredi başvurusunun varlığını kontrol eder.
     * 
     * @param applicationId Kredi başvuru ID
     */
    public void checkIfCreditApplicationExists(Long applicationId) {
        if (!creditApplicationRepository.existsById(applicationId)) {
            throw new BusinessException("Kredi başvurusu bulunamadı: " + applicationId);
        }
    }
    
    /**
     * Kredi başvurusunun onaylandığını kontrol eder.
     * 
     * @param application Kredi başvurusu
     */
    public void checkIfCreditApplicationApproved(CreditApplication application) {
        if (application.getStatus() != CreditApplication.Status.APPROVED) {
            throw new BusinessException("Kredi başvurusu onaylanmamış: " + application.getId());
        }
    }
    
    /**
     * Sözleşmenin daha önce imzalanıp imzalanmadığını kontrol eder.
     * 
     * @param applicationId Kredi başvuru ID
     */
    public void checkIfAgreementAlreadySigned(Long applicationId) {
        Optional<DigitalSignature> signature = digitalSignatureRepository.findFirstByDocumentTypeAndDocumentId(
                DigitalSignature.DocumentType.LOAN_AGREEMENT, applicationId);
        
        if (signature.isPresent()) {
            throw new BusinessException("Sözleşme zaten imzalanmış: " + applicationId);
        }
    }
    
    /**
     * Müşterinin kimlik doğrulamasının yapıldığını kontrol eder.
     * 
     * @param customer Müşteri
     */
    public void checkIfCustomerVerified(Customer customer) {
        if (!customer.isVerified()) {
            throw new BusinessException("Müşteri kimlik doğrulaması yapılmamış: " + customer.getId());
        }
    }
    
    /**
     * İmza süresinin geçerli olup olmadığını kontrol eder.
     * 
     * @param expirationTime Son geçerlilik zamanı
     */
    public void checkIfSignatureTimeValid(LocalDateTime expirationTime) {
        if (expirationTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException("İmza süresi dolmuş");
        }
    }
    
    /**
     * Dijital imzanın geçerli olup olmadığını kontrol eder.
     * 
     * @param signature Dijital imza
     */
    public void checkIfSignatureValid(DigitalSignature signature) {
        if (!signature.isVerified()) {
            throw new BusinessException("Dijital imza geçerli değil: " + signature.getId());
        }
    }
} 
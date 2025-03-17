package com.banking.core.security.signature;

import com.banking.entities.DigitalSignature;
import com.banking.entities.User;
import com.banking.repositories.abstracts.DigitalSignatureRepository;
import com.banking.repositories.abstracts.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Dijital imza işlemlerini yöneten servis.
 * Türkiye'nin KPS (Kimlik Paylaşım Sistemi) ve e-İmza API'si ile entegrasyon sağlar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalSignatureService {

    private final DigitalSignatureRepository digitalSignatureRepository;
    private final UserRepository userRepository;
    private final KpsService kpsService;
    private final ESignatureApiClient eSignatureApiClient;
    
    @Value("${digital.signature.enabled:true}")
    private boolean digitalSignatureEnabled;
    
    /**
     * Belge içeriğine göre dijital imza oluşturur.
     *
     * @param userId Kullanıcı ID
     * @param documentType Belge türü
     * @param documentId Belge ID
     * @param documentContent Belge içeriği
     * @return Oluşturulan dijital imza
     */
    @Transactional
    public DigitalSignature createSignature(Long userId, DigitalSignature.DocumentType documentType, 
                                          Long documentId, String documentContent) {
        if (!digitalSignatureEnabled) {
            log.info("Digital signature is disabled. Skipping signature creation.");
            throw new RuntimeException("Digital signature is disabled");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Belge hash'i oluştur
        String documentHash = calculateHash(documentContent);
        
        // KPS üzerinden kimlik doğrulama
        boolean identityVerified = kpsService.verifyIdentity(user.getFirstName(), user.getLastName(), 
                user.getIdentityNumber(), user.getBirthDate());
        
        if (!identityVerified) {
            throw new RuntimeException("Identity verification failed for user: " + userId);
        }
        
        // e-İmza API'si üzerinden imza oluştur
        SignatureResponse signatureResponse = eSignatureApiClient.createSignature(documentHash, user.getIdentityNumber());
        
        if (!signatureResponse.isSuccess()) {
            throw new RuntimeException("Signature creation failed: " + signatureResponse.getErrorMessage());
        }
        
        // Dijital imza kaydı oluştur
        DigitalSignature digitalSignature = new DigitalSignature();
        digitalSignature.setUser(user);
        digitalSignature.setDocumentType(documentType);
        digitalSignature.setDocumentId(documentId);
        digitalSignature.setDocumentHash(documentHash);
        digitalSignature.setSignatureData(signatureResponse.getSignatureData());
        digitalSignature.setSignatureDate(LocalDateTime.now());
        digitalSignature.setVerified(false);
        digitalSignature.setCertificateSerialNumber(signatureResponse.getCertificateSerialNumber());
        digitalSignature.setCertificateIssuer(signatureResponse.getCertificateIssuer());
        digitalSignature.setCertificateSubject(signatureResponse.getCertificateSubject());
        digitalSignature.setCertificateValidFrom(signatureResponse.getCertificateValidFrom());
        digitalSignature.setCertificateValidTo(signatureResponse.getCertificateValidTo());
        
        return digitalSignatureRepository.save(digitalSignature);
    }
    
    /**
     * Belge dosyası için dijital imza oluşturur.
     * 
     * @param userId Kullanıcı ID
     * @param documentType Belge türü
     * @param documentId Belge ID
     * @param documentFile Belge dosyası
     * @return Oluşturulan dijital imza
     */
    @Transactional
    public DigitalSignature createSignatureFromFile(Long userId, DigitalSignature.DocumentType documentType, 
                                                 Long documentId, MultipartFile documentFile) throws IOException {
        byte[] fileContent = documentFile.getBytes();
        String documentContent = Base64.getEncoder().encodeToString(fileContent);
        return createSignature(userId, documentType, documentId, documentContent);
    }
    
    /**
     * Dijital imzayı doğrular.
     * 
     * @param signatureId İmza ID
     * @param documentContent Belge içeriği
     * @return Doğrulama başarılı ise true, değilse false
     */
    @Transactional
    public boolean verifySignature(Long signatureId, String documentContent) {
        if (!digitalSignatureEnabled) {
            log.info("Digital signature is disabled. Skipping signature verification for signature: {}", signatureId);
            return true;
        }
        
        DigitalSignature digitalSignature = digitalSignatureRepository.findById(signatureId)
                .orElseThrow(() -> new RuntimeException("Digital signature not found with id: " + signatureId));
        
        // Belge hash'i oluştur ve karşılaştır
        String documentHash = calculateHash(documentContent);
        if (!documentHash.equals(digitalSignature.getDocumentHash())) {
            digitalSignature.setVerified(false);
            digitalSignature.setVerificationDate(LocalDateTime.now());
            digitalSignature.setVerificationResult("Document hash mismatch");
            digitalSignatureRepository.save(digitalSignature);
            return false;
        }
        
        // e-İmza API'si üzerinden imza doğrula
        VerificationResponse verificationResponse = eSignatureApiClient.verifySignature(
                digitalSignature.getSignatureData(), documentHash);
        
        digitalSignature.setVerified(verificationResponse.isValid());
        digitalSignature.setVerificationDate(LocalDateTime.now());
        digitalSignature.setVerificationResult(verificationResponse.getResultMessage());
        digitalSignatureRepository.save(digitalSignature);
        
        return verificationResponse.isValid();
    }
    
    /**
     * Dijital imzayı doğrular.
     * 
     * @param signatureId İmza ID
     * @param documentFile Belge dosyası
     * @return Doğrulama başarılı ise true, değilse false
     */
    @Transactional
    public boolean verifySignatureFromFile(Long signatureId, MultipartFile documentFile) throws IOException {
        byte[] fileContent = documentFile.getBytes();
        String documentContent = Base64.getEncoder().encodeToString(fileContent);
        return verifySignature(signatureId, documentContent);
    }
    
    /**
     * Belge türü ve belge ID'sine göre dijital imzaları bulur.
     * 
     * @param documentType Belge türü
     * @param documentId Belge ID
     * @return Dijital imza listesi
     */
    public List<DigitalSignature> getSignaturesByDocument(DigitalSignature.DocumentType documentType, Long documentId) {
        return digitalSignatureRepository.findByDocumentTypeAndDocumentId(documentType, documentId);
    }
    
    /**
     * Kullanıcı ID'sine göre dijital imzaları bulur.
     * 
     * @param userId Kullanıcı ID
     * @return Dijital imza listesi
     */
    public List<DigitalSignature> getSignaturesByUser(Long userId) {
        return digitalSignatureRepository.findByUserId(userId);
    }
    
    /**
     * Kullanıcı ID'sine göre doğrulanmış dijital imzaları bulur.
     * 
     * @param userId Kullanıcı ID
     * @return Doğrulanmış dijital imza listesi
     */
    public List<DigitalSignature> getVerifiedSignaturesByUser(Long userId) {
        return digitalSignatureRepository.findVerifiedSignaturesByUserId(userId);
    }
    
    /**
     * Belge içeriğinin hash'ini hesaplar.
     * 
     * @param content Belge içeriği
     * @return Hash değeri
     */
    private String calculateHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating document hash", e);
        }
    }
} 
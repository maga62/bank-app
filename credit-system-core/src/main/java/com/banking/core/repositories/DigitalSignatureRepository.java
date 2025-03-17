package com.banking.core.repositories;

import com.banking.entities.DigitalSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Dijital imza verileri için repository sınıfı.
 */
@Repository
public interface DigitalSignatureRepository extends JpaRepository<DigitalSignature, Long> {
    
    /**
     * Kullanıcı ID'sine göre dijital imzaları bulur.
     * 
     * @param userId Kullanıcı ID
     * @return Dijital imza listesi
     */
    List<DigitalSignature> findByUserId(Long userId);
    
    /**
     * Belge türü ve belge ID'sine göre dijital imzaları bulur.
     * 
     * @param documentType Belge türü
     * @param documentId Belge ID
     * @return Dijital imza listesi
     */
    List<DigitalSignature> findByDocumentTypeAndDocumentId(DigitalSignature.DocumentType documentType, Long documentId);
    
    /**
     * Belge türü, belge ID'si ve kullanıcı ID'sine göre dijital imzayı bulur.
     * 
     * @param documentType Belge türü
     * @param documentId Belge ID
     * @param userId Kullanıcı ID
     * @return Dijital imza
     */
    Optional<DigitalSignature> findByDocumentTypeAndDocumentIdAndUserId(
            DigitalSignature.DocumentType documentType, Long documentId, Long userId);
    
    /**
     * Belge hash'ine göre dijital imzayı bulur.
     * 
     * @param documentHash Belge hash'i
     * @return Dijital imza
     */
    Optional<DigitalSignature> findByDocumentHash(String documentHash);
    
    /**
     * Doğrulanmış dijital imzaları bulur.
     * 
     * @return Doğrulanmış dijital imza listesi
     */
    List<DigitalSignature> findByVerifiedTrue();
    
    /**
     * Doğrulanmamış dijital imzaları bulur.
     * 
     * @return Doğrulanmamış dijital imza listesi
     */
    List<DigitalSignature> findByVerifiedFalse();
    
    /**
     * Belirli bir belge türüne ait dijital imzaları bulur.
     * 
     * @param documentType Belge türü
     * @return Dijital imza listesi
     */
    List<DigitalSignature> findByDocumentType(DigitalSignature.DocumentType documentType);
    
    /**
     * Belirli bir sertifika seri numarasına sahip dijital imzaları bulur.
     * 
     * @param certificateSerialNumber Sertifika seri numarası
     * @return Dijital imza listesi
     */
    List<DigitalSignature> findByCertificateSerialNumber(String certificateSerialNumber);
    
    /**
     * Belirli bir kullanıcının doğrulanmış dijital imzalarını bulur.
     * 
     * @param userId Kullanıcı ID
     * @return Doğrulanmış dijital imza listesi
     */
    @Query("SELECT d FROM DigitalSignature d WHERE d.user.id = :userId AND d.verified = true")
    List<DigitalSignature> findVerifiedSignaturesByUserId(@Param("userId") Long userId);
} 
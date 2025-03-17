package com.banking.repositories.abstracts;

import com.banking.entities.DigitalSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalSignatureRepository extends JpaRepository<DigitalSignature, Long> {
    
    Optional<DigitalSignature> findFirstByDocumentTypeAndDocumentId(DigitalSignature.DocumentType documentType, Long documentId);
    
    List<DigitalSignature> findByCustomerId(Long customerId);
    
    Optional<DigitalSignature> findByApplicationId(Long applicationId);
    
    boolean existsByApplicationId(Long applicationId);
} 
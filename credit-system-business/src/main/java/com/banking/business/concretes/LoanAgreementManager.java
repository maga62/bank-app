package com.banking.business.concretes;

import com.banking.business.abstracts.LoanAgreementService;
import com.banking.business.dtos.request.LoanAgreementSignRequest;
import com.banking.business.dtos.response.LoanAgreementResponse;
import com.banking.entities.CreditApplication;
import com.banking.entities.DigitalSignature;
import com.banking.repositories.abstracts.CreditApplicationRepository;
import com.banking.repositories.abstracts.DigitalSignatureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LoanAgreementManager implements LoanAgreementService {

    private final CreditApplicationRepository creditApplicationRepository;
    private final DigitalSignatureRepository digitalSignatureRepository;

    @Override
    @Transactional
    public LoanAgreementResponse createAgreement(Long applicationId) {
        CreditApplication application = creditApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Credit application not found with id: " + applicationId));

        // Create agreement content based on application details
        String agreementContent = generateAgreementContent(application);
        
        // Create response
        return LoanAgreementResponse.builder()
                .applicationId(applicationId)
                .customerId(application.getCustomer().getId())
                .agreementContent(agreementContent)
                .createdDate(LocalDateTime.now())
                .isSigned(false)
                .build();
    }

    @Override
    @Transactional
    public DigitalSignature signLoanAgreement(LoanAgreementSignRequest request) {
        CreditApplication application = creditApplicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Credit application not found with id: " + request.getApplicationId()));

        // Verify customer identity (simplified for example)
        if (!application.getCustomer().getId().equals(request.getCustomerId())) {
            throw new RuntimeException("Customer ID mismatch");
        }

        // Generate agreement content
        String agreementContent = generateAgreementContent(application);
        
        // Create digital signature
        DigitalSignature signature = new DigitalSignature();
        signature.setApplicationId(request.getApplicationId());
        signature.setCustomerId(request.getCustomerId());
        signature.setSignatureHash(generateSignatureHash(agreementContent, request.getSignatureData()));
        signature.setSignatureDate(LocalDateTime.now());
        signature.setIpAddress(request.getIpAddress());
        signature.setDeviceInfo(request.getDeviceInfo());
        signature.setVerified(true);
        
        return digitalSignatureRepository.save(signature);
    }

    @Override
    public List<LoanAgreementResponse> getSignedAgreementsByCustomerId(Long customerId) {
        List<DigitalSignature> signatures = digitalSignatureRepository.findByCustomerId(customerId);
        
        return signatures.stream()
                .map(signature -> {
                    CreditApplication application = creditApplicationRepository.findById(signature.getApplicationId())
                            .orElseThrow(() -> new RuntimeException("Credit application not found"));
                    
                    return LoanAgreementResponse.builder()
                            .applicationId(signature.getApplicationId())
                            .customerId(customerId)
                            .agreementContent(generateAgreementContent(application))
                            .createdDate(application.getCreatedDate())
                            .signedDate(signature.getSignatureDate())
                            .isSigned(true)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LoanAgreementResponse> getSignedAgreementByApplicationId(Long applicationId) {
        Optional<DigitalSignature> signature = digitalSignatureRepository.findByApplicationId(applicationId);
        
        if (signature.isPresent()) {
            CreditApplication application = creditApplicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Credit application not found"));
            
            LoanAgreementResponse response = LoanAgreementResponse.builder()
                    .applicationId(applicationId)
                    .customerId(application.getCustomer().getId())
                    .agreementContent(generateAgreementContent(application))
                    .createdDate(application.getCreatedDate())
                    .signedDate(signature.get().getSignatureDate())
                    .isSigned(true)
                    .build();
            
            return Optional.of(response);
        }
        
        return Optional.empty();
    }

    @Override
    public boolean isAgreementSigned(Long applicationId) {
        return digitalSignatureRepository.existsByApplicationId(applicationId);
    }

    @Override
    public byte[] generateAgreementPdf(Long applicationId) {
        CreditApplication application = creditApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Credit application not found with id: " + applicationId));
        
        // In a real implementation, this would use a PDF generation library
        // For this example, we'll just return the agreement content as bytes
        String agreementContent = generateAgreementContent(application);
        return agreementContent.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public boolean archiveSignedAgreement(Long signatureId) {
        DigitalSignature signature = digitalSignatureRepository.findById(signatureId)
                .orElseThrow(() -> new RuntimeException("Digital signature not found with id: " + signatureId));
        
        // In a real implementation, this would archive the document to a secure storage
        signature.setArchived(true);
        signature.setArchivedDate(LocalDateTime.now());
        
        digitalSignatureRepository.save(signature);
        return true;
    }
    
    // Helper methods
    
    private String generateAgreementContent(CreditApplication application) {
        // In a real implementation, this would generate a proper legal document
        StringBuilder builder = new StringBuilder();
        builder.append("LOAN AGREEMENT\n\n");
        builder.append("Application ID: ").append(application.getId()).append("\n");
        builder.append("Customer ID: ").append(application.getCustomer().getId()).append("\n");
        builder.append("Credit Type: ").append(application.getCreditType()).append("\n");
        builder.append("Amount: ").append(application.getAmount()).append("\n");
        builder.append("Term: ").append(application.getTermMonths()).append(" months\n");
        builder.append("Interest Rate: ").append("8.5").append("%\n\n");
        builder.append("Terms and Conditions:\n");
        builder.append("1. The borrower agrees to repay the loan amount plus interest.\n");
        builder.append("2. Payments are due monthly on the same day of each month.\n");
        builder.append("3. Late payments may incur additional fees.\n");
        
        return builder.toString();
    }
    
    private String generateSignatureHash(String agreementContent, String signatureData) {
        try {
            // Combine agreement content and signature data
            String combined = agreementContent + signatureData;
            
            // Create SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            
            // Convert to Base64 string
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate signature hash", e);
        }
    }
} 
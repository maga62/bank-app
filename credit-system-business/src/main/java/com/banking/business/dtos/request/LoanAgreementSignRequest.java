package com.banking.business.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kredi sözleşmesi imzalama isteği için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanAgreementSignRequest {
    
    @NotNull(message = "Müşteri ID boş olamaz")
    private Long customerId;
    
    @NotNull(message = "Kredi başvuru ID boş olamaz")
    private Long applicationId;
    
    @NotBlank(message = "İmza verisi boş olamaz")
    private String signatureData;
    
    @NotBlank(message = "IP adresi boş olamaz")
    private String ipAddress;
    
    private String deviceInfo;
    
    private String userAgent;
    
    private String location;
} 
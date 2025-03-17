package com.banking.business.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * İşlem izleme isteği için veri transfer nesnesi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonitorRequest {
    
    @NotNull(message = "Müşteri ID boş olamaz")
    private Long customerId;
    
    @NotBlank(message = "İşlem türü boş olamaz")
    private String transactionType;
    
    private String transactionId;
    
    @NotNull(message = "İşlem tutarı boş olamaz")
    @Positive(message = "İşlem tutarı pozitif olmalıdır")
    private BigDecimal amount;
    
    @NotBlank(message = "IP adresi boş olamaz")
    private String ipAddress;
    
    private String userAgent;
    
    private String location;
    
    private String deviceId;
    
    private String accountNumber;
    
    private String recipientAccountNumber;
    
    private String recipientName;
    
    private String description;
} 
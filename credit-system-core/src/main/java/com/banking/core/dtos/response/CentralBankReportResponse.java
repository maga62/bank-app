package com.banking.core.dtos.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CentralBankReportResponse {
    private String reportId;
    private Long customerId;
    private String customerName;
    private String identityNumber;
    private LocalDateTime reportDate;
    private LocalDateTime cacheDate;
    private boolean cached;
    private String reportStatus;
    private String reportType;
    private String reportSource;
} 
package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kara liste durumu yanıtı için veri transfer nesnesi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistStatusResponse {
    private String identityNumber;
    private Boolean isBlacklisted;
    private String reason;
    private LocalDateTime blacklistDate;
    private LocalDateTime expectedRemovalDate;
    private String reportingInstitution;
    private LocalDateTime reportDate;
} 
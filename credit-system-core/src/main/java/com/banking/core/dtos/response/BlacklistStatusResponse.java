package com.banking.core.dtos.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlacklistStatusResponse {
    private Long customerId;
    private boolean blacklisted;
    private String reason;
    private LocalDateTime blacklistedDate;
    private LocalDateTime reviewDate;
    private String reviewedBy;
} 
package com.banking.business.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Ödeme gecikmesi yanıtı için veri transfer nesnesi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDelayResponse {
    private String identityNumber;
    private String creditType;
    private Double delayedAmount;
    private Integer delayDays;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private String status;
    private String institution;
    private LocalDateTime reportDate;
} 
package com.banking.business.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateCorporateCustomerRequest extends CreateCustomerRequest {
    @NotBlank(message = "Tax number is required")
    private String taxNumber;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Trade register number is required")
    private String tradeRegisterNumber;

    private String companyType;
    private String authorizedPerson;
} 
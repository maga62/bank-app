package com.banking.business.dtos.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CorporateCustomerResponse extends CustomerResponse {
    private String taxNumber;
    private String companyName;
    private String tradeRegisterNumber;
    private String companyType;
    private String authorizedPerson;
} 
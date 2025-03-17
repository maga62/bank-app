package com.banking.business.dtos.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class IndividualCustomerResponse extends CustomerResponse {
    private String identityNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String nationality;
} 
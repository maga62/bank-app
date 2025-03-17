package com.banking.business.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedIndividualCustomerResponse {
    private Long id;
    private String customerNumber;
    private String identityNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private String nationality;
    private LocalDate birthDate;
} 
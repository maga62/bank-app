package com.banking.business.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateIndividualCustomerRequest extends CreateCustomerRequest {
    @NotBlank
    private String identityNumber;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private String nationality;
    private LocalDate birthDate;
} 
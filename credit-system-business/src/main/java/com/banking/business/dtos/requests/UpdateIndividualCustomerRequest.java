package com.banking.business.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIndividualCustomerRequest {
    
    @NotNull
    private Long id;
    
    @NotBlank
    @Pattern(regexp = "^[0-9]{11}$", message = "Identity number must be 11 digits")
    private String identityNumber;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @NotBlank
    @Email
    private String email;
    
    private String phoneNumber;
    
    private String address;
    
    private String nationality;
    
    private LocalDate birthDate;
} 
package com.banking.business.abstracts;

import com.banking.business.dtos.requests.CreateIndividualCustomerRequest;
import com.banking.business.dtos.requests.UpdateIndividualCustomerRequest;
import com.banking.business.dtos.responses.IndividualCustomerResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface IndividualCustomerService {
    DataResult<IndividualCustomerResponse> add(@Valid @NotNull CreateIndividualCustomerRequest request);
    Result update(@Valid @NotNull UpdateIndividualCustomerRequest request);
    Result delete(@NotNull Long id);
    DataResult<IndividualCustomerResponse> getByCustomerNumber(@NotBlank String customerNumber);
    DataResult<IndividualCustomerResponse> getByIdentityNumber(@NotBlank String identityNumber);
    DataResult<Page<IndividualCustomerResponse>> getAll(Pageable pageable);
    DataResult<Page<IndividualCustomerResponse>> getAllByFirstNameContaining(@NotBlank String firstName, Pageable pageable);
    DataResult<Page<IndividualCustomerResponse>> getAllByLastNameContaining(@NotBlank String lastName, Pageable pageable);
} 
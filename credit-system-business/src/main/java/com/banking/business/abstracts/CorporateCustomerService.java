package com.banking.business.abstracts;

import com.banking.business.dtos.requests.CreateCorporateCustomerRequest;
import com.banking.business.dtos.responses.CorporateCustomerResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;

public interface CorporateCustomerService {
    DataResult<CorporateCustomerResponse> add(CreateCorporateCustomerRequest request);
    Result delete(Long id);
    DataResult<CorporateCustomerResponse> getByCustomerNumber(String customerNumber);
    DataResult<CorporateCustomerResponse> getByTaxNumber(String taxNumber);
} 
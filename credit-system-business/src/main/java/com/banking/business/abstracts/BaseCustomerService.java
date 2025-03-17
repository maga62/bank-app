package com.banking.business.abstracts;

import com.banking.business.dtos.requests.CreateCustomerRequest;
import com.banking.business.dtos.responses.CustomerResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;

public interface BaseCustomerService<TRequest extends CreateCustomerRequest, TResponse extends CustomerResponse> {
    DataResult<TResponse> add(TRequest request);
    Result delete(Long id);
} 
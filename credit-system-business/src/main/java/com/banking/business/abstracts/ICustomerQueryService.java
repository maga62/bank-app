package com.banking.business.abstracts;

import com.banking.business.dtos.responses.CustomerResponse;
import com.banking.core.utilities.results.DataResult;

public interface ICustomerQueryService {
    DataResult<CustomerResponse> getByCustomerNumber(String customerNumber);
} 
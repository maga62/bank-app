package com.banking.business.abstracts;

import com.banking.business.dtos.requests.CreateCreditApplicationRequest;
import com.banking.business.dtos.responses.CreditApplicationResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.entities.enums.CreditType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CreditApplicationService {
    DataResult<CreditApplicationResponse> apply(CreateCreditApplicationRequest request);
    Result cancel(Long id);
    DataResult<CreditApplicationResponse> getById(Long id);
    DataResult<Page<CreditApplicationResponse>> getAllByCustomerId(Long customerId, Pageable pageable);
    DataResult<Page<CreditApplicationResponse>> getAllByCreditType(CreditType creditType, Pageable pageable);
    DataResult<Page<CreditApplicationResponse>> getAll(Pageable pageable);
} 
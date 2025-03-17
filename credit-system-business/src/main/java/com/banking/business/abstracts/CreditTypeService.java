package com.banking.business.abstracts;

import com.banking.business.dtos.responses.CreditTypeResponse;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CreditTypeService {
    DataResult<CreditTypeResponse> getById(Long id);
    DataResult<CreditTypeResponse> getByName(String name);
    DataResult<List<CreditTypeResponse>> getAllByCustomerType(boolean isIndividual);
    DataResult<Page<CreditTypeResponse>> getAllActive(Pageable pageable);
    DataResult<Page<CreditTypeResponse>> getAllActiveByCustomerType(boolean isIndividual, Pageable pageable);
    Result activate(Long id);
    Result deactivate(Long id);
    Result updateInterestRates(Long id, double minRate, double maxRate);
    Result updateTermMonths(Long id, int minMonths, int maxMonths);
} 
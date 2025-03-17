package com.banking.business.concretes;

import com.banking.business.abstracts.CorporateCustomerService;
import com.banking.business.constants.Messages;
import com.banking.business.dtos.requests.CreateCorporateCustomerRequest;
import com.banking.business.dtos.responses.CorporateCustomerResponse;
import com.banking.business.rules.CorporateCustomerBusinessRules;
import com.banking.core.utilities.mappers.ModelMapperService;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.utilities.results.SuccessDataResult;
import com.banking.core.utilities.results.SuccessResult;
import com.banking.entities.CorporateCustomer;
import com.banking.repositories.abstracts.CorporateCustomerRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CorporateCustomerManager implements CorporateCustomerService {
    private final CorporateCustomerRepository repository;
    private final CorporateCustomerBusinessRules rules;
    private final ModelMapperService mapper;

    public CorporateCustomerManager(CorporateCustomerRepository repository,
                                  CorporateCustomerBusinessRules rules,
                                  ModelMapperService mapper) {
        this.repository = repository;
        this.rules = rules;
        this.mapper = mapper;
    }

    @Override
    public DataResult<CorporateCustomerResponse> add(CreateCorporateCustomerRequest request) {
        rules.checkIfTaxNumberExists(request.getTaxNumber());
        rules.checkIfCustomerNumberExists(request.getCustomerNumber());

        CorporateCustomer customer = mapper.forRequest().map(request, CorporateCustomer.class);
        customer.setStatus(true);
        customer.setCreatedDate(LocalDateTime.now());
        
        CorporateCustomer savedCustomer = repository.save(customer);
        CorporateCustomerResponse response = mapper.forResponse()
            .map(savedCustomer, CorporateCustomerResponse.class);
        
        return new SuccessDataResult<>(response, Messages.Corporate.ADDED);
    }

    @Override
    public Result delete(Long id) {
        rules.checkIfExists(id);
        repository.deleteById(id);
        return new SuccessResult(Messages.Corporate.DELETED);
    }

    @Override
    public DataResult<CorporateCustomerResponse> getByCustomerNumber(String customerNumber) {
        CorporateCustomer customer = rules.getByCustomerNumber(customerNumber);
        CorporateCustomerResponse response = mapper.forResponse()
            .map(customer, CorporateCustomerResponse.class);
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<CorporateCustomerResponse> getByTaxNumber(String taxNumber) {
        CorporateCustomer customer = rules.getByTaxNumber(taxNumber);
        CorporateCustomerResponse response = mapper.forResponse()
            .map(customer, CorporateCustomerResponse.class);
        return new SuccessDataResult<>(response);
    }
} 
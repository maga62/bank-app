package com.banking.business.concretes;

import com.banking.business.abstracts.IndividualCustomerService;
import com.banking.business.dtos.requests.CreateIndividualCustomerRequest;
import com.banking.business.dtos.requests.UpdateIndividualCustomerRequest;
import com.banking.business.dtos.responses.IndividualCustomerResponse;
import com.banking.business.rules.IndividualCustomerBusinessRules;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.utilities.results.SuccessDataResult;
import com.banking.core.utilities.results.SuccessResult;
import com.banking.entities.IndividualCustomer;
import com.banking.repositories.abstracts.IndividualCustomerRepository;
import com.banking.business.constants.Messages;
import com.banking.core.utilities.mappers.ModelMapperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class IndividualCustomerManager implements IndividualCustomerService {
    private final IndividualCustomerRepository repository;
    private final IndividualCustomerBusinessRules rules;
    private final ModelMapperService mapper;

    public IndividualCustomerManager(IndividualCustomerRepository repository,
                                   IndividualCustomerBusinessRules rules,
                                   ModelMapperService mapper) {
        this.repository = repository;
        this.rules = rules;
        this.mapper = mapper;
    }

    @Override
    public DataResult<IndividualCustomerResponse> add(CreateIndividualCustomerRequest request) {
        rules.checkIfIdentityNumberExists(request.getIdentityNumber());
        
        IndividualCustomer customer = mapper.forRequest().map(request, IndividualCustomer.class);
        customer.setCustomerNumber(generateCustomerNumber());
        customer.setCreatedDate(LocalDateTime.now());
        
        IndividualCustomer savedCustomer = repository.save(customer);
        
        IndividualCustomerResponse response = mapper.forResponse().map(savedCustomer, IndividualCustomerResponse.class);
        return new SuccessDataResult<>(response, Messages.Individual.ADDED);
    }
    
    @Override
    public Result update(UpdateIndividualCustomerRequest request) {
        rules.checkIfExists(request.getId());
        
        IndividualCustomer customer = repository.findById(request.getId()).get();
        mapper.forRequest().map(request, customer);
        customer.setUpdatedDate(LocalDateTime.now());
        
        repository.save(customer);
        
        return new SuccessResult(Messages.Individual.UPDATED);
    }

    @Override
    public Result delete(Long id) {
        rules.checkIfExists(id);
        
        IndividualCustomer customer = repository.findById(id).get();
        customer.setDeletedDate(LocalDateTime.now());
        repository.save(customer);
        
        return new SuccessResult(Messages.Individual.DELETED);
    }

    @Override
    public DataResult<IndividualCustomerResponse> getByCustomerNumber(String customerNumber) {
        IndividualCustomer customer = rules.getByCustomerNumber(customerNumber);
        
        IndividualCustomerResponse response = mapper.forResponse().map(customer, IndividualCustomerResponse.class);
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<IndividualCustomerResponse> getByIdentityNumber(String identityNumber) {
        IndividualCustomer customer = rules.getByIdentityNumber(identityNumber);
        
        IndividualCustomerResponse response = mapper.forResponse().map(customer, IndividualCustomerResponse.class);
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<Page<IndividualCustomerResponse>> getAll(Pageable pageable) {
        Page<IndividualCustomer> customers = repository.findAll(pageable);
        Page<IndividualCustomerResponse> responsePage = customers.map(customer -> 
            mapper.forResponse().map(customer, IndividualCustomerResponse.class));
        return new SuccessDataResult<>(responsePage);
    }

    @Override
    public DataResult<Page<IndividualCustomerResponse>> getAllByFirstNameContaining(String firstName, Pageable pageable) {
        Page<IndividualCustomer> customers = repository.findAllByFirstNameContaining(firstName, pageable);
        Page<IndividualCustomerResponse> responsePage = customers.map(customer -> 
            mapper.forResponse().map(customer, IndividualCustomerResponse.class));
        return new SuccessDataResult<>(responsePage);
    }

    @Override
    public DataResult<Page<IndividualCustomerResponse>> getAllByLastNameContaining(String lastName, Pageable pageable) {
        Page<IndividualCustomer> customers = repository.findAllByLastNameContaining(lastName, pageable);
        Page<IndividualCustomerResponse> responsePage = customers.map(customer -> 
            mapper.forResponse().map(customer, IndividualCustomerResponse.class));
        return new SuccessDataResult<>(responsePage);
    }
    
    private String generateCustomerNumber() {
        return "IC" + System.currentTimeMillis();
    }
} 
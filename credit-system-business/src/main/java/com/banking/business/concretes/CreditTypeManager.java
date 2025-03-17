package com.banking.business.concretes;

import com.banking.business.abstracts.CreditTypeService;
import com.banking.business.constants.Messages;
import com.banking.business.dtos.responses.CreditTypeResponse;
import com.banking.business.rules.CreditTypeBusinessRules;
import com.banking.core.utilities.mappers.ModelMapperService;
import com.banking.core.utilities.results.*;
import com.banking.entities.CreditType;
import com.banking.repositories.abstracts.CreditTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CreditTypeManager implements CreditTypeService {
    private final CreditTypeRepository repository;
    private final CreditTypeBusinessRules rules;
    private final ModelMapperService mapper;

    @Override
    public DataResult<CreditTypeResponse> getById(Long id) {
        rules.checkIfExists(id);
        CreditType creditType = repository.findById(id).orElseThrow();
        CreditTypeResponse response = mapper.forResponse().map(creditType, CreditTypeResponse.class);
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<CreditTypeResponse> getByName(String name) {
        CreditType creditType = repository.findByName(name)
            .orElseThrow(() -> new RuntimeException(Messages.CreditType.NOT_FOUND));
        CreditTypeResponse response = mapper.forResponse().map(creditType, CreditTypeResponse.class);
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<List<CreditTypeResponse>> getAllByCustomerType(boolean isIndividual) {
        List<CreditType> creditTypes = repository.findAllByIsIndividual(isIndividual);
        List<CreditTypeResponse> response = creditTypes.stream()
            .map(creditType -> mapper.forResponse().map(creditType, CreditTypeResponse.class))
            .collect(Collectors.toList());
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<Page<CreditTypeResponse>> getAllActive(Pageable pageable) {
        Page<CreditType> creditTypes = repository.findAllByIsActive(true, pageable);
        Page<CreditTypeResponse> response = creditTypes
            .map(creditType -> mapper.forResponse().map(creditType, CreditTypeResponse.class));
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<Page<CreditTypeResponse>> getAllActiveByCustomerType(boolean isIndividual, Pageable pageable) {
        Page<CreditType> creditTypes = repository.findAllByIsIndividualAndIsActive(isIndividual, true, pageable);
        Page<CreditTypeResponse> response = creditTypes
            .map(creditType -> mapper.forResponse().map(creditType, CreditTypeResponse.class));
        return new SuccessDataResult<>(response);
    }

    @Override
    public Result activate(Long id) {
        rules.checkIfExists(id);
        CreditType creditType = repository.findById(id).orElseThrow();
        creditType.setActive(true);
        repository.save(creditType);
        return new SuccessResult(Messages.CreditType.ACTIVATED);
    }

    @Override
    public Result deactivate(Long id) {
        rules.checkIfExists(id);
        CreditType creditType = repository.findById(id).orElseThrow();
        creditType.setActive(false);
        repository.save(creditType);
        return new SuccessResult(Messages.CreditType.DEACTIVATED);
    }

    @Override
    public Result updateInterestRates(Long id, double minRate, double maxRate) {
        rules.checkIfExists(id);
        rules.validateInterestRates(minRate, maxRate);
        
        CreditType creditType = repository.findById(id).orElseThrow();
        creditType.setMinInterestRate(minRate);
        creditType.setMaxInterestRate(maxRate);
        repository.save(creditType);
        
        return new SuccessResult(Messages.CreditType.INTEREST_RATES_UPDATED);
    }

    @Override
    public Result updateTermMonths(Long id, int minMonths, int maxMonths) {
        rules.checkIfExists(id);
        rules.validateTermMonths(minMonths, maxMonths);
        
        CreditType creditType = repository.findById(id).orElseThrow();
        creditType.setMinTermMonths(minMonths);
        creditType.setMaxTermMonths(maxMonths);
        repository.save(creditType);
        
        return new SuccessResult(Messages.CreditType.TERM_MONTHS_UPDATED);
    }
} 
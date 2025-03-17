package com.banking.business.concretes;

import com.banking.business.abstracts.CreditApplicationService;
import com.banking.business.constants.Messages;
import com.banking.business.dtos.requests.CreateCreditApplicationRequest;
import com.banking.business.dtos.responses.CreditApplicationResponse;
import com.banking.business.rules.CreditApplicationBusinessRules;
import com.banking.core.utilities.mappers.ModelMapperService;
import com.banking.core.utilities.results.DataResult;
import com.banking.core.utilities.results.Result;
import com.banking.core.utilities.results.SuccessDataResult;
import com.banking.core.utilities.results.SuccessResult;
import com.banking.entities.CreditApplication;
import com.banking.entities.enums.CreditApplicationStatus;
import com.banking.entities.enums.CreditType;
import com.banking.repositories.abstracts.CreditApplicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CreditApplicationManager implements CreditApplicationService {
    private final CreditApplicationRepository repository;
    private final CreditApplicationBusinessRules rules;
    private final ModelMapperService mapper;

    @Override
    public DataResult<CreditApplicationResponse> apply(CreateCreditApplicationRequest request) {
        rules.checkIfCustomerExists(request.getCustomerId());
        rules.checkIfCustomerCanApplyForCreditType(request.getCustomerId(), request.getCreditType());
        rules.checkIfCustomerHasActiveCreditApplication(request.getCustomerId(), request.getCreditType());

        CreditApplication application = mapper.forRequest().map(request, CreditApplication.class);
        application.setStatus(convertStatus(CreditApplicationStatus.PENDING));
        application.setCreatedDate(LocalDateTime.now());

        CreditApplication savedApplication = repository.save(application);
        CreditApplicationResponse response = mapper.forResponse()
            .map(savedApplication, CreditApplicationResponse.class);

        return new SuccessDataResult<>(response, Messages.CreditApplication.CREATED);
    }

    @Override
    public Result cancel(Long id) {
        CreditApplication application = repository.findById(id).orElseThrow();
        application.setStatus(convertStatus(CreditApplicationStatus.CANCELLED));
        repository.save(application);
        return new SuccessResult(Messages.CreditApplication.CANCELLED);
    }

    @Override
    public DataResult<CreditApplicationResponse> getById(Long id) {
        CreditApplication application = repository.findById(id).orElseThrow();
        CreditApplicationResponse response = mapper.forResponse()
            .map(application, CreditApplicationResponse.class);
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<Page<CreditApplicationResponse>> getAllByCustomerId(Long customerId, Pageable pageable) {
        Page<CreditApplication> applications = repository.findAllByCustomerId(customerId, pageable);
        Page<CreditApplicationResponse> response = applications
            .map(application -> mapper.forResponse().map(application, CreditApplicationResponse.class));
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<Page<CreditApplicationResponse>> getAllByCreditType(CreditType creditType, Pageable pageable) {
        Page<CreditApplication> applications = repository.findAllByCreditType(creditType, pageable);
        Page<CreditApplicationResponse> response = applications
            .map(application -> mapper.forResponse().map(application, CreditApplicationResponse.class));
        return new SuccessDataResult<>(response);
    }

    @Override
    public DataResult<Page<CreditApplicationResponse>> getAll(Pageable pageable) {
        Page<CreditApplication> applications = repository.findAll(pageable);
        Page<CreditApplicationResponse> response = applications
            .map(application -> mapper.forResponse().map(application, CreditApplicationResponse.class));
        return new SuccessDataResult<>(response);
    }

    private CreditApplication.Status convertStatus(CreditApplicationStatus status) {
        return CreditApplication.Status.valueOf(status.name());
    }
} 
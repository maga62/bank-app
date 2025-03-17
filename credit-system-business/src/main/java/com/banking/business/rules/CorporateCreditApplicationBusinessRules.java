package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.entities.Customer;
import com.banking.entities.CorporateCustomer;
import com.banking.entities.enums.CorporateCreditType;
import com.banking.repositories.abstracts.CorporateCreditApplicationRepository;
import com.banking.repositories.abstracts.BaseCustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CorporateCreditApplicationBusinessRules {
    private final CorporateCreditApplicationRepository repository;
    private final BaseCustomerRepository customerRepository;

    public void checkIfCustomerExists(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new BusinessException(Messages.Customer.NOT_FOUND);
        }
    }

    public void checkIfCustomerIsCorporate(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException(Messages.Customer.NOT_FOUND));

        if (!(customer instanceof CorporateCustomer)) {
            throw new BusinessException("Only corporate customers can apply for corporate credits");
        }
    }

    public void checkIfCustomerHasActiveCreditApplication(Long customerId, CorporateCreditType creditType) {
        if (repository.existsByCustomerIdAndCreditType(customerId, creditType)) {
            throw new BusinessException(Messages.CreditApplication.ALREADY_EXISTS);
        }
    }
} 
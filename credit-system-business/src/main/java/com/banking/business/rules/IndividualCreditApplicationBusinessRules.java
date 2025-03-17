package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.entities.Customer;
import com.banking.entities.IndividualCustomer;
import com.banking.entities.enums.IndividualCreditType;
import com.banking.repositories.abstracts.IndividualCreditApplicationRepository;
import com.banking.repositories.abstracts.BaseCustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IndividualCreditApplicationBusinessRules {
    private final IndividualCreditApplicationRepository repository;
    private final BaseCustomerRepository customerRepository;

    public void checkIfCustomerExists(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new BusinessException(Messages.Customer.NOT_FOUND);
        }
    }

    public void checkIfCustomerIsIndividual(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException(Messages.Customer.NOT_FOUND));

        if (!(customer instanceof IndividualCustomer)) {
            throw new BusinessException("Only individual customers can apply for individual credits");
        }
    }

    public void checkIfCustomerHasActiveCreditApplication(Long customerId, IndividualCreditType creditType) {
        if (repository.existsByCustomerIdAndCreditType(customerId, creditType)) {
            throw new BusinessException(Messages.CreditApplication.ALREADY_EXISTS);
        }
    }
} 
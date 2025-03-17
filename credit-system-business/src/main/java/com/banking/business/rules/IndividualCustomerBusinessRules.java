package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.entities.IndividualCustomer;
import com.banking.repositories.abstracts.IndividualCustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IndividualCustomerBusinessRules extends BaseBusinessRules<IndividualCustomer, Long> {
    private final IndividualCustomerRepository individualCustomerRepository;

    public IndividualCustomerBusinessRules(IndividualCustomerRepository individualCustomerRepository) {
        super(individualCustomerRepository);
        this.individualCustomerRepository = individualCustomerRepository;
    }

    public void checkIfIdentityNumberExists(String identityNumber) {
        if (individualCustomerRepository.existsByIdentityNumber(identityNumber)) {
            throw new BusinessException(Messages.Individual.IDENTITY_NUMBER_EXISTS);
        }
    }

    public void checkIfCustomerNumberExists(String customerNumber) {
        if (individualCustomerRepository.existsByCustomerNumber(customerNumber)) {
            throw new BusinessException(Messages.Individual.CUSTOMER_NUMBER_EXISTS);
        }
    }

    public IndividualCustomer getByIdentityNumber(String identityNumber) {
        Optional<IndividualCustomer> customer = individualCustomerRepository.findByIdentityNumber(identityNumber);
        if (!customer.isPresent()) {
            throw new BusinessException(Messages.Individual.NOT_FOUND);
        }
        return customer.get();
    }

    public IndividualCustomer getByCustomerNumber(String customerNumber) {
        Optional<IndividualCustomer> customer = individualCustomerRepository.findByCustomerNumber(customerNumber);
        if (!customer.isPresent()) {
            throw new BusinessException(Messages.Individual.NOT_FOUND);
        }
        return customer.get();
    }
} 
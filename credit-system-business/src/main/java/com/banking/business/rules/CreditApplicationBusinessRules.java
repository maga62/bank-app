package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.entities.Customer;
import com.banking.entities.IndividualCustomer;
import com.banking.entities.enums.CreditType;
import com.banking.repositories.abstracts.CreditApplicationRepository;
import com.banking.repositories.abstracts.BaseCustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreditApplicationBusinessRules {
    private final CreditApplicationRepository repository;
    private final BaseCustomerRepository customerRepository;

    public void checkIfCustomerExists(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new BusinessException(Messages.Customer.NOT_FOUND);
        }
    }

    public void checkIfCustomerCanApplyForCreditType(Long customerId, CreditType creditType) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new BusinessException(Messages.Customer.NOT_FOUND));

        boolean isIndividualCredit = creditType == CreditType.PERSONAL_FINANCE ||
                                   creditType == CreditType.MORTGAGE ||
                                   creditType == CreditType.AUTO_LOAN ||
                                   creditType == CreditType.EDUCATION_LOAN;

        if (isIndividualCredit && !(customer instanceof IndividualCustomer)) {
            throw new BusinessException("Corporate customers cannot apply for individual credit types");
        }

        if (!isIndividualCredit && customer instanceof IndividualCustomer) {
            throw new BusinessException("Individual customers cannot apply for corporate credit types");
        }
    }

    public void checkIfCustomerHasActiveCreditApplication(Long customerId, CreditType creditType) {
        if (repository.existsByCustomerIdAndCreditType(customerId, creditType)) {
            throw new BusinessException("Customer already has an active application for this credit type");
        }
    }
} 
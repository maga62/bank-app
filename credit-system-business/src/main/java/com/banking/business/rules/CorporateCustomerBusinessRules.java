package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.entities.CorporateCustomer;
import com.banking.repositories.abstracts.CorporateCustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CorporateCustomerBusinessRules extends BaseBusinessRules<CorporateCustomer, Long> {
    private final CorporateCustomerRepository corporateCustomerRepository;

    public CorporateCustomerBusinessRules(CorporateCustomerRepository corporateCustomerRepository) {
        super(corporateCustomerRepository);
        this.corporateCustomerRepository = corporateCustomerRepository;
    }

    public void checkIfTaxNumberExists(String taxNumber) {
        if (corporateCustomerRepository.existsByTaxNumber(taxNumber)) {
            throw new BusinessException(Messages.Corporate.TAX_NUMBER_EXISTS);
        }
    }

    public void checkIfCustomerNumberExists(String customerNumber) {
        if (corporateCustomerRepository.existsByCustomerNumber(customerNumber)) {
            throw new BusinessException(Messages.Corporate.CUSTOMER_NUMBER_EXISTS);
        }
    }

    public CorporateCustomer getByTaxNumber(String taxNumber) {
        Optional<CorporateCustomer> customer = corporateCustomerRepository.findByTaxNumber(taxNumber);
        if (!customer.isPresent()) {
            throw new BusinessException(Messages.Corporate.NOT_FOUND);
        }
        return customer.get();
    }

    public CorporateCustomer getByCustomerNumber(String customerNumber) {
        Optional<CorporateCustomer> customer = corporateCustomerRepository.findByCustomerNumber(customerNumber);
        if (!customer.isPresent()) {
            throw new BusinessException(Messages.Corporate.NOT_FOUND);
        }
        return customer.get();
    }
}
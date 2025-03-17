package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.repositories.abstracts.CreditTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreditTypeBusinessRules {
    private final CreditTypeRepository repository;

    public void checkIfExists(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(Messages.CreditType.NOT_FOUND);
        }
    }

    public void checkIfNameExists(String name) {
        if (repository.existsByName(name)) {
            throw new BusinessException(Messages.CreditType.NAME_EXISTS);
        }
    }

    public void checkIfActive(Long id) {
        repository.findById(id).ifPresent(creditType -> {
            if (!creditType.isActive()) {
                throw new BusinessException(Messages.CreditType.NOT_ACTIVE);
            }
        });
    }

    public void validateInterestRates(double minRate, double maxRate) {
        if (minRate < 0 || maxRate < 0) {
            throw new BusinessException("Interest rates cannot be negative");
        }
        if (minRate > maxRate) {
            throw new BusinessException("Minimum interest rate cannot be greater than maximum interest rate");
        }
    }

    public void validateTermMonths(int minMonths, int maxMonths) {
        if (minMonths < 1 || maxMonths < 1) {
            throw new BusinessException("Term months must be positive");
        }
        if (minMonths > maxMonths) {
            throw new BusinessException("Minimum term months cannot be greater than maximum term months");
        }
    }
} 
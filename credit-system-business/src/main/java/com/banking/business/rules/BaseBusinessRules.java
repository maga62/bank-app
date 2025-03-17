package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseBusinessRules<T, ID> {
    
    protected final JpaRepository<T, ID> repository;
    
    protected BaseBusinessRules(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }
    
    public void checkIfExists(ID id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(Messages.Common.ENTITY_NOT_FOUND);
        }
    }
    
    public void checkIfNotExists(ID id) {
        if (repository.existsById(id)) {
            throw new BusinessException(Messages.Common.ENTITY_ALREADY_EXISTS);
        }
    }
} 
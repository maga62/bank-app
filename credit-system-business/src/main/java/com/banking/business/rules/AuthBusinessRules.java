package com.banking.business.rules;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.repositories.abstracts.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthBusinessRules {
    private final UserRepository userRepository;

    public void checkIfEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(Messages.Auth.EMAIL_ALREADY_EXISTS);
        }
    }

    public void checkIfEmailNotExists(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new BusinessException(Messages.Auth.EMAIL_NOT_FOUND);
        }
    }
} 
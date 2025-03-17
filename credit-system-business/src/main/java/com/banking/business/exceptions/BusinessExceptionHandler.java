package com.banking.business.exceptions;

import com.banking.business.constants.Messages;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.core.crosscuttingconcerns.exceptions.problemdetails.BusinessProblemDetails;
import com.banking.core.crosscuttingconcerns.exceptions.problemdetails.ProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleBusinessException(BusinessException exception) {
        log.error("Business exception occurred: {}", exception.getMessage());
        return new BusinessProblemDetails(exception.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetails handleException(Exception exception) {
        log.error("Unexpected exception occurred: {}", exception.getMessage(), exception);
        return new ProblemDetails(Messages.Common.OPERATION_FAILED);
    }
} 
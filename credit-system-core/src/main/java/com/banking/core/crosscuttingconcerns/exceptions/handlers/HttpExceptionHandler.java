package com.banking.core.crosscuttingconcerns.exceptions.handlers;

import com.banking.core.crosscuttingconcerns.exceptions.AuthorizationException;
import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.core.crosscuttingconcerns.exceptions.problemdetails.*;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class HttpExceptionHandler extends BaseExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Override
    public BusinessProblemDetails handleBusinessException(BusinessException exception) {
        log.error("[BusinessException] " + exception.getMessage());
        return new BusinessProblemDetails(exception.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetails handleAuthorizationException(AuthorizationException exception) {
        log.error("[AuthorizationException] " + exception.getMessage());
        return new AuthorizationProblemDetails();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleValidationException(ValidationException exception) {
        log.error("[ValidationException] " + exception.getMessage());
        return new ValidationProblemDetails(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
            .forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));
        
        ValidationProblemDetails problemDetails = new ValidationProblemDetails(exception.getMessage());
        problemDetails.setValidationErrors(validationErrors);
        return problemDetails;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetails handleException(Exception exception) {
        log.error("[Exception] " + exception.getMessage());
        return new InternalServerErrorProblemDetails();
    }
} 
package com.banking.core.crosscuttingconcerns.exceptions.handlers;

import com.banking.core.crosscuttingconcerns.exceptions.problemdetails.ProblemDetails;
import com.banking.core.crosscuttingconcerns.exceptions.problemdetails.ValidationProblemDetails;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Component
@Order(1)
public class ValidationExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleValidationException(MethodArgumentNotValidException exception) {
        ValidationProblemDetails problemDetails = new ValidationProblemDetails();
        problemDetails.setTitle("Validation Error");
        problemDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        problemDetails.setDetail("One or more validation errors occurred");
        problemDetails.setTimestamp(LocalDateTime.now());
        problemDetails.setValidationErrors(getValidationErrors(exception));
        
        log.error("Validation error: {}", problemDetails.getValidationErrors());
        return problemDetails;
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleConstraintViolationException(ConstraintViolationException exception) {
        ValidationProblemDetails problemDetails = new ValidationProblemDetails();
        problemDetails.setTitle("Constraint Violation");
        problemDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        problemDetails.setDetail("One or more constraints were violated");
        problemDetails.setTimestamp(LocalDateTime.now());
        
        Map<String, String> validationErrors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> getPropertyPath(violation),
                        ConstraintViolation::getMessage,
                        (error1, error2) -> error1 + ", " + error2
                ));
        
        problemDetails.setValidationErrors(validationErrors);
        log.error("Constraint violation: {}", validationErrors);
        return problemDetails;
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        ValidationProblemDetails problemDetails = new ValidationProblemDetails();
        problemDetails.setTitle("Invalid Request Body");
        problemDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        problemDetails.setDetail("The request body is invalid or malformed");
        problemDetails.setTimestamp(LocalDateTime.now());
        
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("requestBody", "Invalid JSON format or type mismatch");
        problemDetails.setValidationErrors(validationErrors);
        
        log.error("Invalid request body: {}", exception.getMessage());
        return problemDetails;
    }
    
    @SuppressWarnings("null")
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        ValidationProblemDetails problemDetails = new ValidationProblemDetails();
        problemDetails.setTitle("Type Mismatch");
        problemDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        problemDetails.setDetail("Parameter type mismatch");
        problemDetails.setTimestamp(LocalDateTime.now());
        
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put(exception.getName(), "Expected type: " + exception.getRequiredType().getSimpleName());
        problemDetails.setValidationErrors(validationErrors);
        
        log.error("Type mismatch: Parameter '{}' should be of type '{}'", 
                exception.getName(), exception.getRequiredType().getSimpleName());
        return problemDetails;
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetails handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        ValidationProblemDetails problemDetails = new ValidationProblemDetails();
        problemDetails.setTitle("Missing Parameter");
        problemDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        problemDetails.setDetail("Required parameter is missing");
        problemDetails.setTimestamp(LocalDateTime.now());
        
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put(exception.getParameterName(), "Parameter is required");
        problemDetails.setValidationErrors(validationErrors);
        
        log.error("Missing parameter: '{}' of type '{}'", 
                exception.getParameterName(), exception.getParameterType());
        return problemDetails;
    }

    private Map<String, String> getValidationErrors(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();
        exception.getBindingResult().getAllErrors()
            .forEach(error -> {
                String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                String errorMessage = error.getDefaultMessage();
                validationErrors.put(fieldName, errorMessage);
            });
        return validationErrors;
    }
    
    private String getPropertyPath(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        // Remove method name from property path if present
        int lastDotIndex = propertyPath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            propertyPath = propertyPath.substring(lastDotIndex + 1);
        }
        return propertyPath;
    }
} 
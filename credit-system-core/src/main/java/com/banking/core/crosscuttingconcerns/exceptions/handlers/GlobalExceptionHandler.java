package com.banking.core.crosscuttingconcerns.exceptions.handlers;

import com.banking.core.crosscuttingconcerns.exceptions.BusinessException;
import com.banking.core.crosscuttingconcerns.exceptions.problemdetails.BusinessProblemDetails;
import com.banking.core.crosscuttingconcerns.exceptions.problemdetails.ValidationProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Tüm uygulama genelinde hataları yakalayıp uygun şekilde formatlamak için kullanılan global exception handler.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * İş mantığı hatalarını ele alır.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessProblemDetails handleBusinessException(BusinessException exception) {
        log.error("Business exception occurred: {}", exception.getMessage(), exception);
        return new BusinessProblemDetails(exception.getMessage());
    }
    
    /**
     * Validasyon hatalarını ele alır.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationProblemDetails handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();
        
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        log.error("Validation exception occurred: {}", validationErrors);
        return new ValidationProblemDetails(validationErrors);
    }
    
    /**
     * Metot argümanı tip uyuşmazlığı hatalarını ele alır.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BusinessProblemDetails handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = String.format("'%s' parametresi için geçersiz değer: '%s'", 
                exception.getName(), exception.getValue());
        log.error("Type mismatch exception: {}", message, exception);
        return new BusinessProblemDetails(message);
    }
    
    /**
     * Veri bütünlüğü ihlali hatalarını ele alır.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public BusinessProblemDetails handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String message = "Veri bütünlüğü hatası oluştu. Lütfen girdiğiniz verileri kontrol edin.";
        log.error("Data integrity violation: {}", exception.getMessage(), exception);
        return new BusinessProblemDetails(message);
    }
    
    /**
     * Kayıt bulunamadı hatalarını ele alır.
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BusinessProblemDetails handleNoSuchElementException(NoSuchElementException exception) {
        log.error("Resource not found: {}", exception.getMessage(), exception);
        return new BusinessProblemDetails("Aradığınız kayıt bulunamadı.");
    }
    
    /**
     * Yetkilendirme hatalarını ele alır.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BusinessProblemDetails handleAccessDeniedException(AccessDeniedException exception) {
        log.error("Access denied: {}", exception.getMessage(), exception);
        return new BusinessProblemDetails("Bu işlemi gerçekleştirmek için yetkiniz bulunmamaktadır.");
    }
    
    /**
     * Kimlik doğrulama hatalarını ele alır.
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BusinessProblemDetails handleBadCredentialsException(BadCredentialsException exception) {
        log.error("Authentication failed: {}", exception.getMessage());
        return new BusinessProblemDetails("Kullanıcı adı veya şifre hatalı.");
    }
    
    /**
     * Diğer tüm beklenmeyen hataları ele alır.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BusinessProblemDetails handleException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return new BusinessProblemDetails("Beklenmeyen bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
    }
} 
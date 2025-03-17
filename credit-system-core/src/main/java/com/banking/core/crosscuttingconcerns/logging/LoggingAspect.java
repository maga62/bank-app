package com.banking.core.crosscuttingconcerns.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.banking.business.concretes.*.*(..))")
    private void businessMethods() {}
    
    @Pointcut("execution(* com.banking.core.security.jwt.*.*(..))")
    private void securityMethods() {}
    
    @Pointcut("execution(* com.banking.webapi.controllers.*.*(..))")
    private void controllerMethods() {}
    
    @Pointcut("businessMethods() || securityMethods() || controllerMethods()")
    private void allApplicationMethods() {}

    @Around("allApplicationMethods()")
    public Object logAroundAllMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = UUID.randomUUID().toString();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("[{}] Executing {}::{} with arguments: {}", 
                requestId, className, methodName, Arrays.toString(args));
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[{}] {}::{} executed in {}ms", 
                    requestId, className, methodName, (endTime - startTime));
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("[{}] {}::{} failed after {}ms with exception: {}", 
                    requestId, className, methodName, (endTime - startTime), e.getMessage(), e);
            throw e;
        }
    }

    @AfterThrowing(pointcut = "allApplicationMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String requestId = UUID.randomUUID().toString();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        StringBuilder parameters = new StringBuilder();
        for (int i = 0; i < parameterNames.length; i++) {
            parameters.append(parameterNames[i]).append("=").append(args[i]);
            if (i < parameterNames.length - 1) {
                parameters.append(", ");
            }
        }
        
        log.error("[{}] Exception in {}::{} with parameters: [{}]", 
                requestId, className, methodName, parameters, exception);
        
        // Log the full stack trace with detailed information
        log.error("[{}] Detailed exception information:", requestId, exception);
        
        // Log the cause chain
        Throwable cause = exception.getCause();
        int level = 1;
        while (cause != null) {
            log.error("[{}] Cause level {}: {} - {}", 
                    requestId, level++, cause.getClass().getName(), cause.getMessage(), cause);
            cause = cause.getCause();
        }
    }
} 
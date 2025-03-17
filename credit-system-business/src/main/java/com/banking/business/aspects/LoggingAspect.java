package com.banking.business.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.banking.business.concretes.*.*(..))")
    private void businessMethods() {}

    @Around("businessMethods()")
    public Object logAroundBusinessMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("Executing {}.{}() with arguments: {}", className, methodName, Arrays.toString(args));
        
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        
        log.info("{}.{}() executed in {}ms", className, methodName, executionTime);
        
        return result;
    }

    @AfterThrowing(pointcut = "businessMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        log.error("Exception in {}.{}() with cause = {}", className, methodName, 
                exception.getCause() != null ? exception.getCause() : "NULL");
        log.error("Exception message: {}", exception.getMessage());
    }
} 
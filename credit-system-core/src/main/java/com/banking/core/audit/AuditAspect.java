package com.banking.core.audit;

import com.banking.entities.Customer;
import com.banking.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * İşlem izleme aspect'i.
 * Belirli metotların çağrılmasını otomatik olarak loglar.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;

    /**
     * Kredi başvurusu işlemleri için pointcut
     */
    @Pointcut("execution(* com.banking.business.concretes.CreditApplication*.*(..)) && " +
              "!execution(* com.banking.business.concretes.CreditApplication*.get*(..))")
    public void creditApplicationMethods() {}

    /**
     * Müşteri işlemleri için pointcut
     */
    @Pointcut("(execution(* com.banking.business.concretes.IndividualCustomer*.*(..)) || " +
              "execution(* com.banking.business.concretes.CorporateCustomer*.*(..))) && " +
              "!execution(* com.banking.business.concretes.*Customer*.get*(..))")
    public void customerMethods() {}

    /**
     * Kullanıcı işlemleri için pointcut
     */
    @Pointcut("execution(* com.banking.business.concretes.User*.*(..)) && " +
              "!execution(* com.banking.business.concretes.User*.get*(..))")
    public void userMethods() {}

    /**
     * Kredi başvurusu işlemlerini loglar
     */
    @AfterReturning(pointcut = "creditApplicationMethods()", returning = "result")
    public void auditCreditApplicationMethods(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = method.getName();
            
            String action = determineAction(methodName);
            Object[] args = joinPoint.getArgs();
            
            // Varlık ID'sini ve detayları belirle
            Long entityId = null;
            Map<String, Object> details = new HashMap<>();
            
            // Metot parametrelerini detaylara ekle
            String[] parameterNames = signature.getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                if (i < args.length && args[i] != null) {
                    details.put(parameterNames[i], args[i].toString());
                }
            }
            
            // Sonucu detaylara ekle
            if (result != null) {
                details.put("result", result.toString());
                
                // Sonuçtan varlık ID'sini çıkarmaya çalış
                if (result.toString().contains("id=")) {
                    String idStr = result.toString().split("id=")[1].split(",")[0];
                    try {
                        entityId = Long.parseLong(idStr);
                    } catch (NumberFormatException e) {
                        log.debug("Could not parse entity ID from result: {}", idStr);
                    }
                }
            }
            
            // Mevcut kullanıcıyı al
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                String userRole = user.getAuthorities().stream()
                        .findFirst()
                        .map(Object::toString)
                        .orElse("UNKNOWN");
                
                auditService.logUserAction(
                        user.getId(),
                        userRole,
                        action,
                        "CREDIT_APPLICATION",
                        entityId,
                        details);
            } else {
                auditService.logSystemAction(
                        "CreditApplicationService",
                        action,
                        "CREDIT_APPLICATION",
                        entityId,
                        details);
            }
        } catch (Exception e) {
            log.error("Error in audit aspect for credit application methods", e);
        }
    }

    /**
     * Müşteri işlemlerini loglar
     */
    @AfterReturning(pointcut = "customerMethods()", returning = "result")
    public void auditCustomerMethods(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = method.getName();
            
            String action = determineAction(methodName);
            Object[] args = joinPoint.getArgs();
            
            // Varlık ID'sini ve detayları belirle
            Long entityId = null;
            Map<String, Object> details = new HashMap<>();
            
            // Metot parametrelerini detaylara ekle
            String[] parameterNames = signature.getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                if (i < args.length && args[i] != null) {
                    details.put(parameterNames[i], args[i].toString());
                }
            }
            
            // Sonucu detaylara ekle
            if (result != null) {
                details.put("result", result.toString());
                
                // Sonuçtan varlık ID'sini çıkarmaya çalış
                if (result.toString().contains("id=")) {
                    String idStr = result.toString().split("id=")[1].split(",")[0];
                    try {
                        entityId = Long.parseLong(idStr);
                    } catch (NumberFormatException e) {
                        log.debug("Could not parse entity ID from result: {}", idStr);
                    }
                }
            }
            
            // Müşteri tipini belirle
            String customerType = joinPoint.getSignature().getDeclaringType().getSimpleName().contains("Individual") ? 
                    "INDIVIDUAL" : "CORPORATE";
            
            // Mevcut kullanıcıyı al
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                
                // Kullanıcı bir müşteri mi?
                if (user instanceof Customer) {
                    auditService.logCustomerAction(
                            user.getId(),
                            customerType,
                            action,
                            "CUSTOMER",
                            entityId,
                            details);
                } else {
                    String userRole = user.getAuthorities().stream()
                            .findFirst()
                            .map(Object::toString)
                            .orElse("UNKNOWN");
                    
                    auditService.logUserAction(
                            user.getId(),
                            userRole,
                            action,
                            "CUSTOMER",
                            entityId,
                            details);
                }
            } else {
                auditService.logSystemAction(
                        "CustomerService",
                        action,
                        "CUSTOMER",
                        entityId,
                        details);
            }
        } catch (Exception e) {
            log.error("Error in audit aspect for customer methods", e);
        }
    }

    /**
     * Kullanıcı işlemlerini loglar
     */
    @AfterReturning(pointcut = "userMethods()", returning = "result")
    public void auditUserMethods(JoinPoint joinPoint, Object result) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = method.getName();
            
            String action = determineAction(methodName);
            Object[] args = joinPoint.getArgs();
            
            // Varlık ID'sini ve detayları belirle
            Long entityId = null;
            Map<String, Object> details = new HashMap<>();
            
            // Metot parametrelerini detaylara ekle
            String[] parameterNames = signature.getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                if (i < args.length && args[i] != null) {
                    details.put(parameterNames[i], args[i].toString());
                }
            }
            
            // Sonucu detaylara ekle
            if (result != null) {
                details.put("result", result.toString());
                
                // Sonuçtan varlık ID'sini çıkarmaya çalış
                if (result.toString().contains("id=")) {
                    String idStr = result.toString().split("id=")[1].split(",")[0];
                    try {
                        entityId = Long.parseLong(idStr);
                    } catch (NumberFormatException e) {
                        log.debug("Could not parse entity ID from result: {}", idStr);
                    }
                }
            }
            
            // Mevcut kullanıcıyı al
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                String userRole = user.getAuthorities().stream()
                        .findFirst()
                        .map(Object::toString)
                        .orElse("UNKNOWN");
                
                auditService.logUserAction(
                        user.getId(),
                        userRole,
                        action,
                        "USER",
                        entityId,
                        details);
            } else {
                auditService.logSystemAction(
                        "UserService",
                        action,
                        "USER",
                        entityId,
                        details);
            }
        } catch (Exception e) {
            log.error("Error in audit aspect for user methods", e);
        }
    }

    /**
     * Metot adına göre işlem tipini belirler
     */
    private String determineAction(String methodName) {
        if (methodName.startsWith("add") || methodName.startsWith("create") || methodName.startsWith("save")) {
            return "CREATE";
        } else if (methodName.startsWith("update") || methodName.startsWith("edit") || methodName.startsWith("modify")) {
            return "UPDATE";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "DELETE";
        } else if (methodName.startsWith("approve")) {
            return "APPROVE";
        } else if (methodName.startsWith("reject")) {
            return "REJECT";
        } else if (methodName.startsWith("cancel")) {
            return "CANCEL";
        } else {
            return "OTHER";
        }
    }
} 
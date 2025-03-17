package com.banking.entities.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enum representing user roles in the system.
 * Implements GrantedAuthority for Spring Security integration.
 * All role names start with ROLE_ prefix as required by Spring Security.
 */
public enum Role implements GrantedAuthority {
    ROLE_ADMIN("Sistem yöneticisi"),
    ROLE_SUPERVISOR("Takım yöneticisi"),
    ROLE_USER("Kullanıcı"),
    ROLE_INDIVIDUAL_CUSTOMER("Bireysel müşteri"),
    ROLE_CORPORATE_CUSTOMER("Kurumsal müşteri"),
    ROLE_FRAUD_ANALYST("Dolandırıcılık analisti"),
    ROLE_COMPLIANCE_OFFICER("Uyum görevlisi"),
    ROLE_EMPLOYEE("Çalışan");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the authority string representation of this role.
     * This method is required by the GrantedAuthority interface.
     *
     * @return the name of the role as the authority string
     */
    @Override
    public String getAuthority() {
        return name();
    }
} 
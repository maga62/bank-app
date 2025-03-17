package com.banking.business.constants;

public class Messages {
    public static class Individual {
        public static final String IDENTITY_NUMBER_EXISTS = "Identity number already exists";
        public static final String NOT_FOUND = "Individual customer not found";
        public static final String ADDED = "Individual customer added successfully";
        public static final String DELETED = "Individual customer deleted successfully";
        public static final String CUSTOMER_NUMBER_EXISTS = "Customer number already exists";
        public static final String CREATED = "Individual customer created successfully";
        public static final String UPDATED = "Individual customer updated successfully";
    }

    public static class Corporate {
        public static final String TAX_NUMBER_EXISTS = "Tax number already exists";
        public static final String NOT_FOUND = "Corporate customer not found";
        public static final String ADDED = "Corporate customer added successfully";
        public static final String DELETED = "Corporate customer deleted successfully";
        public static final String CUSTOMER_NUMBER_EXISTS = "Corporate number already exists";
        public static final String UPDATED = "Corporate customer updated successfully";
    }

    public static class Customer {
        public static final String CUSTOMER_NUMBER_EXISTS = "Customer number already exists";
        public static final String NOT_FOUND = "Customer not found";
    }

    public static class CreditApplication {
        public static final String CREATED = "Credit application created successfully";
        public static final String CANCELLED = "Credit application cancelled successfully";
        public static final String NOT_FOUND = "Credit application not found";
        public static final String ALREADY_EXISTS = "Customer already has an active application for this credit type";
        public static final String INVALID_CREDIT_TYPE = "Customer cannot apply for this type of credit";
    }

    public static class CreditType {
        public static final String NOT_FOUND = "Credit type not found";
        public static final String NAME_EXISTS = "Credit type with this name already exists";
        public static final String NOT_ACTIVE = "Credit type is not active";
        public static final String ACTIVATED = "Credit type activated successfully";
        public static final String DEACTIVATED = "Credit type deactivated successfully";
        public static final String INTEREST_RATES_UPDATED = "Credit type interest rates updated successfully";
        public static final String TERM_MONTHS_UPDATED = "Credit type term months updated successfully";
    }

    public static class Auth {
        public static final String EMAIL_ALREADY_EXISTS = "Email is already in use";
        public static final String EMAIL_NOT_FOUND = "Email not found";
        public static final String INVALID_CREDENTIALS = "Invalid email or password";
        public static final String REGISTER_SUCCESS = "User registered successfully";
        public static final String LOGIN_SUCCESS = "User logged in successfully";
    }

    public static class Common {
        public static final String ENTITY_NOT_FOUND = "Entity not found";
        public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
        public static final String VALIDATION_ERROR = "Validation error";
        public static final String OPERATION_SUCCESSFUL = "Operation successful";
        public static final String OPERATION_FAILED = "Operation failed";
    }
} 
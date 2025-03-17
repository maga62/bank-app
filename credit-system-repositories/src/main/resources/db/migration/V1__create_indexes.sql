-- Indexes for User table
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_deleted_date ON users(deleted_date);

-- Indexes for Customer table
CREATE INDEX IF NOT EXISTS idx_customer_customer_number ON customers(customer_number);
CREATE INDEX IF NOT EXISTS idx_customer_deleted_date ON customers(deleted_date);
CREATE INDEX IF NOT EXISTS idx_customer_status ON customers(status);

-- Indexes for IndividualCustomer table
CREATE INDEX IF NOT EXISTS idx_individual_customer_identity_number ON individual_customers(identity_number);
CREATE INDEX IF NOT EXISTS idx_individual_customer_first_name ON individual_customers(first_name);
CREATE INDEX IF NOT EXISTS idx_individual_customer_last_name ON individual_customers(last_name);
CREATE INDEX IF NOT EXISTS idx_individual_customer_nationality ON individual_customers(nationality);

-- Indexes for CorporateCustomer table
CREATE INDEX IF NOT EXISTS idx_corporate_customer_tax_number ON corporate_customers(tax_number);
CREATE INDEX IF NOT EXISTS idx_corporate_customer_company_name ON corporate_customers(company_name);
CREATE INDEX IF NOT EXISTS idx_corporate_customer_trade_register_number ON corporate_customers(trade_register_number);
CREATE INDEX IF NOT EXISTS idx_corporate_customer_company_type ON corporate_customers(company_type);

-- Indexes for CreditApplication table
CREATE INDEX IF NOT EXISTS idx_credit_application_customer_id ON credit_applications(customer_id);
CREATE INDEX IF NOT EXISTS idx_credit_application_credit_type ON credit_applications(credit_type);
CREATE INDEX IF NOT EXISTS idx_credit_application_status ON credit_applications(status);
CREATE INDEX IF NOT EXISTS idx_credit_application_created_date ON credit_applications(created_date);
CREATE INDEX IF NOT EXISTS idx_credit_application_deleted_date ON credit_applications(deleted_date);
CREATE INDEX IF NOT EXISTS idx_credit_application_amount ON credit_applications(amount);

-- Indexes for CreditType table
CREATE INDEX IF NOT EXISTS idx_credit_type_name ON credit_types(name);
CREATE INDEX IF NOT EXISTS idx_credit_type_is_individual ON credit_types(is_individual);
CREATE INDEX IF NOT EXISTS idx_credit_type_is_active ON credit_types(is_active);
CREATE INDEX IF NOT EXISTS idx_credit_type_deleted_date ON credit_types(deleted_date);
CREATE INDEX IF NOT EXISTS idx_credit_type_min_interest_rate ON credit_types(min_interest_rate);
CREATE INDEX IF NOT EXISTS idx_credit_type_max_interest_rate ON credit_types(max_interest_rate);
CREATE INDEX IF NOT EXISTS idx_credit_type_min_term_months ON credit_types(min_term_months);
CREATE INDEX IF NOT EXISTS idx_credit_type_max_term_months ON credit_types(max_term_months);

-- Composite indexes for frequently used query combinations
CREATE INDEX IF NOT EXISTS idx_credit_application_customer_id_credit_type ON credit_applications(customer_id, credit_type);
CREATE INDEX IF NOT EXISTS idx_credit_application_customer_id_status ON credit_applications(customer_id, status);
CREATE INDEX IF NOT EXISTS idx_credit_type_is_individual_is_active ON credit_types(is_individual, is_active);
CREATE INDEX IF NOT EXISTS idx_individual_customer_first_name_last_name ON individual_customers(first_name, last_name); 
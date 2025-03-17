-- Add indexes for payment_histories table
CREATE INDEX idx_payment_histories_customer_id ON payment_histories(customer_id);
CREATE INDEX idx_payment_histories_credit_application_id ON payment_histories(credit_application_id);
CREATE INDEX idx_payment_histories_repayment_plan_id ON payment_histories(repayment_plan_id);
CREATE INDEX idx_payment_histories_due_date ON payment_histories(due_date);
CREATE INDEX idx_payment_histories_payment_date ON payment_histories(payment_date);
CREATE INDEX idx_payment_histories_payment_status ON payment_histories(payment_status);

-- Add indexes for repayment_plans table
CREATE INDEX idx_repayment_plans_customer_id ON repayment_plans(customer_id);
CREATE INDEX idx_repayment_plans_credit_application_id ON repayment_plans(credit_application_id);
CREATE INDEX idx_repayment_plans_start_date ON repayment_plans(start_date);
CREATE INDEX idx_repayment_plans_end_date ON repayment_plans(end_date);
CREATE INDEX idx_repayment_plans_repayment_frequency ON repayment_plans(repayment_frequency);

-- Add indexes for collaterals table
CREATE INDEX idx_collaterals_customer_id ON collaterals(customer_id);
CREATE INDEX idx_collaterals_credit_application_id ON collaterals(credit_application_id);
CREATE INDEX idx_collaterals_collateral_type ON collaterals(collateral_type);
CREATE INDEX idx_collaterals_collateral_status ON collaterals(collateral_status);
CREATE INDEX idx_collaterals_valuation_date ON collaterals(valuation_date);

-- Add indexes for real_estate_collaterals table
CREATE INDEX idx_real_estate_collaterals_property_type ON real_estate_collaterals(property_type);
CREATE INDEX idx_real_estate_collaterals_city ON real_estate_collaterals(city);
CREATE INDEX idx_real_estate_collaterals_district ON real_estate_collaterals(district);

-- Add indexes for vehicle_collaterals table
CREATE INDEX idx_vehicle_collaterals_vehicle_type ON vehicle_collaterals(vehicle_type);
CREATE INDEX idx_vehicle_collaterals_license_plate ON vehicle_collaterals(license_plate);
CREATE INDEX idx_vehicle_collaterals_make_model_year ON vehicle_collaterals(make, model, year);

-- Add indexes for insurances table
CREATE INDEX idx_insurances_customer_id ON insurances(customer_id);
CREATE INDEX idx_insurances_credit_application_id ON insurances(credit_application_id);
CREATE INDEX idx_insurances_policy_number ON insurances(policy_number);
CREATE INDEX idx_insurances_insurance_type ON insurances(insurance_type);
CREATE INDEX idx_insurances_insurance_status ON insurances(insurance_status);
CREATE INDEX idx_insurances_start_date ON insurances(start_date);
CREATE INDEX idx_insurances_end_date ON insurances(end_date);

-- Add indexes for income_sources table
CREATE INDEX idx_income_sources_customer_id ON income_sources(customer_id);
CREATE INDEX idx_income_sources_income_type ON income_sources(income_type);
CREATE INDEX idx_income_sources_income_frequency ON income_sources(income_frequency);
CREATE INDEX idx_income_sources_is_verified ON income_sources(is_verified);

-- Add indexes for expenses table
CREATE INDEX idx_expenses_customer_id ON expenses(customer_id);
CREATE INDEX idx_expenses_expense_type ON expenses(expense_type);
CREATE INDEX idx_expenses_expense_frequency ON expenses(expense_frequency);
CREATE INDEX idx_expenses_is_mandatory ON expenses(is_mandatory);
CREATE INDEX idx_expenses_is_verified ON expenses(is_verified);

-- Add indexes for credit_histories table
CREATE INDEX idx_credit_histories_customer_id ON credit_histories(customer_id);
CREATE INDEX idx_credit_histories_credit_type ON credit_histories(credit_type);
CREATE INDEX idx_credit_histories_status ON credit_histories(status);
CREATE INDEX idx_credit_histories_start_date ON credit_histories(start_date);
CREATE INDEX idx_credit_histories_end_date ON credit_histories(end_date);
CREATE INDEX idx_credit_histories_is_closed ON credit_histories(is_closed);

-- Add composite indexes for frequently used queries
CREATE INDEX idx_payment_histories_customer_payment_status ON payment_histories(customer_id, payment_status);
CREATE INDEX idx_payment_histories_customer_due_date ON payment_histories(customer_id, due_date);
CREATE INDEX idx_credit_histories_customer_status ON credit_histories(customer_id, status);
CREATE INDEX idx_income_sources_customer_income_type ON income_sources(customer_id, income_type);
CREATE INDEX idx_expenses_customer_expense_type ON expenses(customer_id, expense_type);
CREATE INDEX idx_expenses_customer_is_mandatory ON expenses(customer_id, is_mandatory);

-- Add indexes for credit applications table
CREATE INDEX idx_credit_applications_customer_id ON credit_applications(customer_id);
CREATE INDEX idx_credit_applications_status ON credit_applications(status);
CREATE INDEX idx_credit_applications_credit_type ON credit_applications(credit_type);

-- Add indexes for customers table
CREATE INDEX idx_customers_customer_number ON customers(customer_number);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_phone_number ON customers(phone_number);

-- Add indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- Optimize tables
ANALYZE TABLE payment_histories;
ANALYZE TABLE repayment_plans;
ANALYZE TABLE collaterals;
ANALYZE TABLE real_estate_collaterals;
ANALYZE TABLE vehicle_collaterals;
ANALYZE TABLE insurances;
ANALYZE TABLE income_sources;
ANALYZE TABLE expenses;
ANALYZE TABLE credit_histories;
ANALYZE TABLE credit_applications;
ANALYZE TABLE customers;
ANALYZE TABLE users; 
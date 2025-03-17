-- Customers tablosu
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    customer_number VARCHAR(20) UNIQUE NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
    status BOOLEAN NOT NULL DEFAULT TRUE
);

-- Individual Customers tablosu
CREATE TABLE IF NOT EXISTS individual_customers (
    id BIGINT PRIMARY KEY REFERENCES customers(id),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    identity_number VARCHAR(11) UNIQUE NOT NULL,
    birth_date DATE,
    CONSTRAINT uk_individual_identity_number UNIQUE (identity_number)
);

-- Corporate Customers tablosu
CREATE TABLE IF NOT EXISTS corporate_customers (
    id BIGINT PRIMARY KEY REFERENCES customers(id),
    company_name VARCHAR(100) NOT NULL,
    tax_number VARCHAR(10) UNIQUE NOT NULL,
    CONSTRAINT uk_corporate_tax_number UNIQUE (tax_number)
);

-- İndeksler
CREATE INDEX idx_customer_number ON customers(customer_number);
CREATE INDEX idx_identity_number ON individual_customers(identity_number);
CREATE INDEX idx_tax_number ON corporate_customers(tax_number); 

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255) not null,
        customer_number varchar(255) not null unique,
        email varchar(255) not null,
        phone_number varchar(255) not null,
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255) not null,
        customer_number varchar(255) not null unique,
        email varchar(255) not null,
        phone_number varchar(255) not null,
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255) not null,
        customer_number varchar(255) not null unique,
        email varchar(255) not null,
        phone_number varchar(255) not null,
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255) not null,
        customer_number varchar(255) not null unique,
        email varchar(255) not null,
        phone_number varchar(255) not null,
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255) not null,
        customer_number varchar(255) not null unique,
        email varchar(255) not null,
        phone_number varchar(255) not null,
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

    create table corporate_customers (
        id bigint not null,
        authorized_person varchar(255),
        company_name varchar(255),
        company_type varchar(255),
        tax_number varchar(255) unique,
        trade_register_number varchar(255) not null unique,
        primary key (id)
    );

    create table customers (
        status boolean,
        created_date timestamp(6),
        deleted_date timestamp(6),
        id bigserial not null,
        updated_date timestamp(6),
        address varchar(255),
        customer_number varchar(255) not null unique,
        email varchar(255),
        phone_number varchar(255),
        primary key (id)
    );

    create table individual_customers (
        birth_date date not null,
        customer_id bigint not null,
        first_name varchar(255) not null,
        identity_number varchar(255) not null unique,
        last_name varchar(255) not null,
        nationality varchar(255),
        primary key (customer_id)
    );

    alter table if exists corporate_customers 
       add constraint FKsuc4t12rv8pqy0tschmuf19c 
       foreign key (id) 
       references customers;

    alter table if exists individual_customers 
       add constraint FKll0lkkxbkxko8whbdn7x7rwfe 
       foreign key (customer_id) 
       references customers;

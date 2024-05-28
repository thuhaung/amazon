create type gender_enum as enum('Male', 'Female');
create type address_enum as enum('Work', 'Home');
create type user_action as enum('CREATE', 'DELETE', 'DEACTIVATE');
create type transaction_status as enum ('PENDING', 'PROCESSING', 'DONE', 'FAILED');

drop table if exists users;
create table users (
    id bigserial primary key,
    name varchar(255),
    birth_date timestamp,
    gender gender_enum,
    mobile varchar(255) unique,
    name_changed_at timestamp,
    birth_date_changed_at timestamp,
    is_vendor boolean default false not null
);

drop table if exists user_transaction;
create table user_transaction (
    id bigserial primary key,
    user_id bigserial not null references users(id) on delete cascade on update cascade,
    referenced_auth_id bigint not null unique,
    type user_action not null,
    status transaction_status not null default 'PENDING'
);

drop table if exists bank_account;
create table bank_account (
    id bigserial primary key,
    user_id bigserial not null references users(id) on delete cascade,
    account_number varchar(17) not null,
    bank_name varchar(255) not null,
    expiry timestamp not null,
    is_default boolean default false not null,
    is_valid boolean default true not null
);

drop table if exists address;
create table address (
    id bigserial primary key,
    user_id bigserial not null references users(id) on delete cascade,
    building varchar(255),
    street varchar(255),
    city varchar(255) not null,
    region varchar(255) not null,
    country varchar(255) not null,
    postal_code varchar(255),
    address_type address_enum,
    is_default boolean default false not null
);

drop table if exists shop;
create table shop (
    id bigserial primary key,
    vendor_id bigserial not null references users(id) on delete cascade,
    default_bank_id bigserial references bank_account(id) on delete set null,
    default_address_id bigserial references address(id) on delete set null,
    name varchar(255) not null,
    description varchar(255) not null,
    mobile varchar(255) not null,
    email varchar(255) not null
);

insert into users (name, birth_date, gender, mobile)
values ('Ha Ung', '2001-08-02T00:00:00+00', 'Female', '0891169420');
insert into users (name, birth_date, gender, mobile)
values ('Duy Dinh', '2001-01-08T00:00:00+00', 'Male', '0123456789');

insert into address (user_id, city, region, country, address_type, is_default)
values (1, 'HCMC', 'HCMC', 'Vietnam', 'Home', true);
insert into address (user_id, street, city, region, country, address_type)
values (1, 'CMT8', 'HCMC', 'HCMC', 'Vietnam', 'Work');



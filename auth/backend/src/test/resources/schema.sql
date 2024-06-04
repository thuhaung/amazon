create type auth_user_action as enum('CREATE', 'DELETE', 'DEACTIVATE');
create type transaction_status as enum ('PENDING', 'PROCESSING', 'DONE', 'FAILED');

drop table if exists role;
create table role (
    id serial primary key,
    type varchar(11) not null
);

drop table if exists auth_user;
create table auth_user (
    id bigserial primary key,
    email varchar(254) unique not null,
    password varchar(255) not null,
    is_verified boolean not null default false,
    is_active boolean not null default true,
    deletion_date timestamp
);


drop table if exists auth_user_role;
create table auth_user_role (
    id bigserial primary key,
    auth_user_id bigserial not null references auth_user(id) on update cascade on delete cascade,
    role_id integer not null references role(id) on update cascade
);

drop table if exists refresh_token;
create table refresh_token (
    id bigserial primary key,
    token varchar(500) not null,
    expiration timestamp,
    auth_user_id bigserial not null references auth_user(id) on delete cascade on update cascade
);

drop table if exists email_verification_token;
create table email_verification_token (
   id bigserial primary key,
   token varchar(500) not null,
   expiration timestamp,
   auth_user_id bigserial not null references auth_user(id) on delete cascade on update cascade
);

drop table if exists auth_user_transaction;
create table auth_user_transaction (
    id bigserial primary key,
    auth_user_id bigserial not null references auth_user(id) on delete cascade on update cascade,
    type auth_user_action not null,
    status transaction_status not null default 'PENDING'
);
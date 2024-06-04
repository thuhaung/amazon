insert into role (type) values ('Admin');
insert into role (type) values ('Employee');
insert into role (type) values ('User');

insert into auth_user (email, password)
values ('thu.ha2897@gmail.com', '$2a$10$pY51xPZe4Hau1sBNJmgBiOptw0dzP/oE6PlKfU94/E01H1IMLpJBW');
insert into auth_user (email, password)
values ('duy.dinh@email.com', '$2a$10$pY51xPZe4Hau1sBNJmgBiOptw0dzP/oE6PlKfU94/E01H1IMLpJBW');

insert into auth_user_role (auth_user_id, role_id) values (1, 1);
insert into auth_user_role (auth_user_id, role_id) values (2, 1);
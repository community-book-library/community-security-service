drop table communitylibrary.users;
drop table communitylibrary.login_info;
drop table communitylibrary.roles;
drop table communitylibrary.user_auth;

create table communitylibrary.roles(role_id BIT(10) primary key,role varchar(50));

create table communitylibrary.login_info(id int(255) AUTO_INCREMENT PRIMARY KEY,last_login_time TIMESTAMP,
login_status enum("CREATED","ACTIVE","LOCKED","DISABLED"),login_attempt BIT(10),user_id smallint, foreign key(user_id) references 
communitylibrary.users(id));

CREATE TABLE communitylibrary.users(id smallint AUTO_INCREMENT PRIMARY KEY,first_name varchar(50),last_name varchar(50), role_id BIT(10),
foreign key(role_id) references communitylibrary.roles(role_id),created_timestamp TIMESTAMP,updated_timestamp TIMESTAMP,
created_by varchar(50),updated_by varchar(50));

create table communitylibrary.user_auth(id smallint AUTO_INCREMENT PRIMARY KEY, user_id smallint, foreign key(user_id) references 
communitylibrary.users(id), username varchar(50), password varchar(255),created_timestamp TIMESTAMP,updated_timestamp TIMESTAMP,created_by varchar(50),updated_by 
varchar(50));

insert into communitylibrary.roles values(1,"USER");
insert into communitylibrary.roles values(2,"ADMIN");
insert into communitylibrary.roles values(3,"MANAGER");


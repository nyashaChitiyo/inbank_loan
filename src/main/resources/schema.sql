drop table if exists segmentation;
drop table if exists users;

create table if not exists segmentation(
segment_id varchar(255) not null unique primary key,
segment_name varchar(255),
credit_modifier int
);

create table if not exists users(
personal_code varchar(255) not null unique primary key,
username varchar(255),
segment_id varchar(255)
);
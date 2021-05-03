create table cv(
    id varchar(36) primary key,
    created timestamp(6),
    email_address varchar(256),
    phone_number varchar(20),
    name varchar(256),
    content varchar(1000),
    is_published_to_kafka boolean,
    is_published_to_client boolean,
    is_fully_published boolean
);
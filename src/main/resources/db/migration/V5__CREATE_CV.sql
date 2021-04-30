create table cv(
    id varchar(36) primary key,
    created timestamp(6),
    emailAddress varchar(256),
    phoneNumber varchar(20),
    name varchar(256),
    content varchar(1000)
);
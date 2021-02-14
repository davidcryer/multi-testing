create table address(
    id varchar(36) primary key,
    building_number varchar(20),
    organisation varchar(50),
    addressLine1 varchar(200),
    addressLine2 varchar(200),
    county varchar(50),
    town varchar(50),
    postcode varchar(10) not null
);

create table letter(
    id varchar(36) primary key,
    sender varchar(200),
    recipient varchar(200),
    recipient_address varchar(36) references address on delete cascade,
    message varchar(2000)
);
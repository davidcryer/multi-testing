create table address(
    id varchar(36) primary key,
    building_number varchar(20),
    organisation varchar(50),
    address_line_1 varchar(200),
    address_line_2 varchar(200),
    county varchar(50),
    town varchar(50),
    postcode varchar(10) not null
);

create table letter(
    id varchar(36) primary key,
    sender varchar(200),
    recipient varchar(200),
    recipient_address varchar(36) references address,
    message varchar(2000)
);
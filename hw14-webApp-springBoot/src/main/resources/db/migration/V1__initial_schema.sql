create table clients
(
    id   bigserial not null primary key, -- BIGSERIAL для автоматической генерации long ID в БД
    name varchar(50) not null
);

create table addresses
(
    id          bigserial not null primary key,
    street      varchar(50) not null,
    client_id   bigint unique,
    constraint  fk_address_client foreign key (client_id) references clients (id) on delete cascade
);

create table phones
(
    id              bigserial not null primary key,
    number          varchar(50) not null,
    client_id       bigint,
    order_column    int not null,
    constraint  fk_phone_client foreign key (client_id) references clients (id) on delete cascade
);


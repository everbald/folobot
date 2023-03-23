create table order_info (
    id serial,
    status varchar(64),
    payment jsonb
);

comment on table order_info is 'Заказы';
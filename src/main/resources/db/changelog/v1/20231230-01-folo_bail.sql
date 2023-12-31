create table folo_bail (
    id uuid primary key,
    chat_id bigint not null,
    date_time timestamp with time zone default now() not null,
    message jsonb not null
);

comment on table order_info is 'Сливы';
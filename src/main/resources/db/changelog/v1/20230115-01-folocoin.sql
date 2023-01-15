create table if not exists folo_coin (
    user_id bigint primary key,
    points integer not null default 0,
    coins integer not null default 0
);
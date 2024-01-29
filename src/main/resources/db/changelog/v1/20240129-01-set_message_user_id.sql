update message set user_id = ((message ->> 'from')::jsonb ->> 'id')::bigint;

alter table message alter column user_id set not null;
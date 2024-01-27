alter table message add column user_id bigint;

update message set user_id = ((message ->> 'from')::jsonb ->> 'id')::bigint;

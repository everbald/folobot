alter table folo_bail rename to message;

comment on table message is 'Сообщения';

comment on table order_info is 'Заказы';

alter table message add column message_id int;

update message set message_id = (message ->> 'message_id')::integer;

alter table message alter column message_id set not null;

create unique index message_index ON message (chat_id, message_id);

alter table message add column reaction_count int not null default 0;
alter table folo_pidor drop column last_active_date;
alter table folo_pidor drop column messages_per_day;
alter table folo_pidor add constraint dv_score default 0 for score;
alter table folo_pidor alter column score set not null;

update folo_pidor set last_win_date = null where score = 0;

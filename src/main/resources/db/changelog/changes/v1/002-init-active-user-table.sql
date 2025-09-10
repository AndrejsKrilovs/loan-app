create table active_user_table (
  active_user_id bigint not null,
  active_user_logged_time timestamp(6) default CURRENT_TIMESTAMP not null,
  primary key (active_user_id)
);

alter table if exists active_user_table
  add constraint active_user_fk
  foreign key (active_user_id)
  references user_table;

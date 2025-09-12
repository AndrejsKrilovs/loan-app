create table customer_profile_table (
  profile_birth_date date not null,
  profile_user_id bigint not null,
  profile_name varchar(30) not null,
  profile_personal_code varchar(15) not null unique,
  profile_phone varchar(16) not null,
  profile_bank_card_number varchar(19) not null unique,
  profile_surname varchar(30) not null,
  profile_address varchar(255),
  profile_version integer,
  primary key (profile_user_id)
);

alter table if exists customer_profile_table
  add constraint profile_user_fk
  foreign key (profile_user_id)
  references user_table;

create table loan_table (
	loan_status varchar(10) default 'ACTIVE' not null,
	loan_start_date date not null,
	loan_end_date date not null,
	loan_outstanding_amount numeric(6,2) not null,
  loan_id bigint unique not null,
  primary key (loan_id)
);

alter table if exists loan_table
	add constraint loan_application_fk
  foreign key (loan_id)
  references loan_application_table;

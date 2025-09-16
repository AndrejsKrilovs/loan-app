create table risk_assessments_table (
  assessment_existing_loan boolean,
  assessment_blacklist_hit boolean,
  assessment_loan_application_id bigint unique not null,
  primary key (assessment_loan_application_id)
);

alter table if exists risk_assessments_table
	add constraint assessment_loan_application_fk
  foreign key (assessment_loan_application_id)
  references loan_application_table;

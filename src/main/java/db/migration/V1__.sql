--Auto id columns start with 100 to prevent id collision as in this thread http://stackoverflow.com/questions/37410841/the-statement-was-aborted-because-it-would-have-caused-a-duplicate-key
--We insert ids with values for tests

create table kwery_user (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  password varchar(255) not null,
  username varchar(255) not null,
  primary key (id)
);

create table datasource (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  label varchar(255) not null,
  password varchar(255),
  port integer not null check (port>=1),
  type varchar(255),
  url varchar(255) not null,
  username varchar(255) not null,
  primary key (id)
);

create table query_run (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  cron_expression varchar(255),
  label varchar(255) not null,
  query varchar(255) not null,
  datasource_id_fk integer not null,
  primary key (id)
);

create table query_run_dependent (
  query_run_id_fk integer,
  dependent_query_run_id_fk integer,
  primary key (query_run_id_fk, dependent_query_run_id_fk)
);

create table job (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  cron_expression varchar(255),
  label varchar(255) not null,
  primary key (id)
);

create table sql_query (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  label varchar(255) not null,
  query varchar(255) not null,
  datasource_id_fk integer not null,
  primary key (id)
);

create table sql_query_execution (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  execution_id varchar(255),
  execution_start bigint,
  execution_end bigint,
  result long varchar,
  status varchar(255),
  sql_query_id_fk integer,
  job_execution_id_fk integer,
  primary key (id)
);

create table job_sql_query (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  sql_query_id_fk integer not null,
  job_id_fk integer not null,
  primary key (id)
);

create table job_execution (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  execution_id varchar(255),
  execution_start bigint,
  execution_end bigint,
  status varchar(255),
  job_id_fk integer,
  primary key (id)
);

create table smtp_configuration (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  host varchar(1024),
  port int,
  ssl boolean,
  username varchar(1024),
  password varchar(1024),
  primary key (id)
);

create table email_configuration (
  id integer generated by default as identity (START WITH 100, INCREMENT BY 1),
  from_email varchar(1024),
  reply_to varchar(1024),
  bcc varchar(1024),
  primary key (id)
);

create table query_run_email_recipient (
  query_run_id_fk integer,
  email varchar(256),
  primary key (query_run_id_fk, email)
);

alter table kwery_user add constraint uc_kwery_user_username  unique (username);

alter table datasource add constraint uc_datasource_label  unique (label);

alter table query_run add constraint uc_query_run_label  unique (label);
alter table query_run add constraint fk_query_run_datasource_fk_id foreign key (datasource_id_fk) references datasource;


alter table query_run_dependent add constraint fk_query_run_dependent_query_run_id_fk foreign key (query_run_id_fk) references query_run;
alter table query_run_dependent add constraint fk_query_run_dependent_dependent_query_run_id_fk foreign key (dependent_query_run_id_fk) references query_run;

alter table query_run_email_recipient add CONSTRAINT fk_query_run_email_recipient_query_run_id_fk foreign key (query_run_id_fk) references query_run;

alter table job add constraint uc_job_label unique (label);
alter table job_sql_query add constraint fk_job_sql_query_sql_query_id_fk foreign key (sql_query_id_fk) references sql_query;
alter table job_sql_query add constraint fk_job_sql_query_job_id_fk foreign key (job_id_fk) references job;
alter table job_sql_query add constraint uc_sql_query_id_fk_job_id_fk unique (sql_query_id_fk, job_id_fk);

alter table sql_query add constraint uc_sql_query_label unique (label);
alter table sql_query add constraint fk_sql_query_datasource_fk_id foreign key (datasource_id_fk) references datasource;

alter table sql_query_execution add constraint fk_sql_query_execution_sql_query_id_fk foreign key (sql_query_id_fk) references sql_query;
alter table sql_query_execution add constraint fk_sql_query_execution_job_execution_id_fk foreign key (job_execution_id_fk) references job_execution;

alter table job_execution add constraint fk_job_execution_job_id_fk foreign key (job_id_fk) references job;

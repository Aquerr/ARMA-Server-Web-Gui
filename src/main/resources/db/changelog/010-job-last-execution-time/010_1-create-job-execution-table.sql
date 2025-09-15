--liquibase formatted sql

--changeset aquerr:create-job-execution-table

CREATE TABLE job_execution (
   id IDENTITY PRIMARY KEY NOT NULL,
   job_name VARCHAR(255) NOT NULL,
   last_execution_date TIMESTAMP WITH TIME ZONE NOT NULL
)
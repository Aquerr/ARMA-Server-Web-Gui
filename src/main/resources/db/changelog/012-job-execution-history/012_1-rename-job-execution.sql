--liquibase formatted sql

--changeset aquerr:create-job-execution-table
ALTER TABLE job_execution RENAME TO job_execution_history;

ALTER TABLE job_execution_history ALTER COLUMN last_execution_date RENAME TO execution_date;

ALTER TABLE job_execution_history ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS';
ALTER TABLE job_execution_history ADD COLUMN message VARCHAR(255);

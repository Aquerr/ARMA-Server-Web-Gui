--liquibase formatted sql

--changeset aquerr:add-last-login-date-time

ALTER TABLE aswg_user ADD COLUMN last_success_login_date_time TIMESTAMP WITH TIME ZONE;
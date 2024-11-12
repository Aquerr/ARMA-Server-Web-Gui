--liquibase formatted sql
--changeset author:aquerr

CREATE TABLE IF NOT EXISTS mod_settings (
    id IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE
);
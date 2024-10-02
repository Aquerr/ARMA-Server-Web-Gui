--liquibase formatted sql
--changeset author:aquerr

CREATE TABLE IF NOT EXISTS mission (
    id IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    template VARCHAR(255) NOT NULL,
    difficulty VARCHAR(7) NOT NULL DEFAULT 'REGULAR',
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    parameters CHARACTER LARGE OBJECT DEFAULT NULL
);
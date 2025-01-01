--liquibase formatted sql
--changeset author:aquerr

CREATE TABLE IF NOT EXISTS invalid_jwt_token (
    id IDENTITY PRIMARY KEY,
    jwt VARCHAR(255) NOT NULL,
    expiration_date_time  TIMESTAMP WITH TIME ZONE NOT NULL,
    invalidated_date_time TIMESTAMP WITH TIME ZONE NOT NULL
);
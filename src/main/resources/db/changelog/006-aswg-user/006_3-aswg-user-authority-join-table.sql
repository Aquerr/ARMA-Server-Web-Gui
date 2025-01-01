--liquibase formatted sql
--changeset author:aquerr

CREATE TABLE IF NOT EXISTS user_authority (
    user_id INTEGER NOT NULL,
    authority_id INTEGER NOT NULL,
    CONSTRAINT fk_user_authority_user_id FOREIGN KEY (user_id) REFERENCES aswg_user (id),
    CONSTRAINT fk_user_authority_authority_id FOREIGN KEY (authority_id) REFERENCES aswg_authority (id),
    PRIMARY KEY (user_id, authority_id)
);
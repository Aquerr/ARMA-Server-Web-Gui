--liquibase formatted sql
--changeset author:aquerr

INSERT INTO aswg_authority (id, code)
SELECT MAX(id) + 1, 'UNSAFE_OVERWRITE_STARTUP_PARAMS' FROM aswg_authority;

--liquibase formatted sql

--changeset aquerr:add-purge-permissions

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'MOD_PRESETS_PURGE');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'MISSIONS_PURGE');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'MODS_PURGE');
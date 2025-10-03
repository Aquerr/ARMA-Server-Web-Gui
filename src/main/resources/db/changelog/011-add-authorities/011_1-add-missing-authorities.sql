--liquibase formatted sql

--changeset aquerr:add-missing-authorities

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'WORKSHOP_VIEW');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'DISCORD_SETTINGS_UPDATE');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'STEAM_SETTINGS_UPDATE');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'JOBS_SETTINGS_UPDATE');
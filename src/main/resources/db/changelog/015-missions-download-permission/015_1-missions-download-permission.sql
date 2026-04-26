--liquibase formatted sql

--changeset aquerr:add-missions-download-permission

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'MISSIONS_DOWNLOAD');
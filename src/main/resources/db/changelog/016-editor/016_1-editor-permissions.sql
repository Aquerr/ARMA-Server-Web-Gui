--liquibase formatted sql

--changeset aquerr:add-editor-permissions

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'EDITOR_SERVER_CONFIG_VIEW');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'EDITOR_SERVER_CONFIG_SAVE');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'EDITOR_NETWORK_CONFIG_VIEW');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'EDITOR_NETWORK_CONFIG_SAVE');
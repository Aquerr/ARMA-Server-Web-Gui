--liquibase formatted sql

--changeset aquerr:add-cdlc

CREATE TABLE cdlc (
  id IDENTITY PRIMARY KEY NOT NULL,
  name VARCHAR(255) NOT NULL,
  directory_name VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO cdlc (name, directory_name, enabled)
VALUES
('Global Mobilization', 'gm', false),
('S.O.G. Prairie Fire', 'vn', false),
('CSLA Iron Curtain', 'csla', false),
('Western Sahara', 'ws', false),
('Spearhead 1944', 'spe', false),
('Reaction Forces', 'rf', false),
('Expeditionary Forces', 'ef', false);


-- New authorities
INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'CDLC_VIEW');

INSERT INTO aswg_authority (id, code)
VALUES ((SELECT MAX(id) + 1 FROM aswg_authority), 'CDLC_UPDATE');


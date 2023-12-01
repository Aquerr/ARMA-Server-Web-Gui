CREATE TABLE IF NOT EXISTS installed_mod (
    id IDENTITY PRIMARY KEY NOT NULL,
    workshop_file_id BIGINT UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    directory_path VARCHAR(255) NOT NULL,
    preview_url VARCHAR(255),
    created_date TIMESTAMP WITH TIME ZONE NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    server_mod BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS mod_preset (
    id IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS mod_preset_entry (
    id IDENTITY PRIMARY KEY NOT NULL,
    mod_preset_id BIGINT NOT NULL,
    mod_id BIGINT NOT NULL,
    mod_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (mod_preset_id) REFERENCES mod_preset(id)
);

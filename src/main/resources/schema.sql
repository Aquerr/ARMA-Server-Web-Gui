CREATE TABLE IF NOT EXISTS installed_mod (
    id IDENTITY PRIMARY KEY NOT NULL,
    workshop_file_id BIGINT UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    directory_path VARCHAR(255) NOT NULL,
    preview_url VARCHAR(255),
    created_date TIMESTAMP WITH TIME ZONE NOT NULL
);

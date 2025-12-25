ALTER TABLE installed_mod ALTER COLUMN last_workshop_update RENAME TO last_workshop_update_date;
ALTER TABLE installed_mod ADD COLUMN last_workshop_update_attempt_date TIMESTAMP WITH TIME ZONE;
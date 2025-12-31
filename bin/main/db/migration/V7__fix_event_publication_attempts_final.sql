-- Definitive fix for event_publication completion_attempts error
-- This migration resets the column to ensure it is nullable and has a proper default.

-- 1. Drop the column to start fresh (in case of weird constraint states)
ALTER TABLE event_publication DROP COLUMN IF EXISTS completion_attempts;

-- 2. Add it back as nullable with a default value
-- Hibernate in some versions of Spring Modulith doesn't include this column in the INSERT statement,
-- so the database must provide the default and ALLOW it to be missing (NULL in the insert but defaulted in DB).
ALTER TABLE event_publication ADD COLUMN completion_attempts INTEGER DEFAULT 0;

-- 3. Explicitly ensure it's NULLABLE (dropping any NOT NULL that might be inferred)
ALTER TABLE event_publication ALTER COLUMN completion_attempts DROP NOT NULL;

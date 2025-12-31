-- Robust fix for missing completion_attempts in event_publication
-- 1. Add column if it doesn't exist (Nullable first to avoid any immediate data issues)
ALTER TABLE event_publication 
ADD COLUMN IF NOT EXISTS completion_attempts INTEGER;

-- 2. Backfill existing nulls to 0
UPDATE event_publication 
SET completion_attempts = 0 
WHERE completion_attempts IS NULL;

-- 3. Set the default for new rows
ALTER TABLE event_publication 
ALTER COLUMN completion_attempts SET DEFAULT 0;

-- 4. Keep it NULLABLE for now to survive if the application doesn't provide a value
-- and the database default is ignored by some ORMs/drivers in specific contexts.
-- If you want it NOT NULL again, you can do it in a later migration once verified.

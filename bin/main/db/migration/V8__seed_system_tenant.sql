-- Seed System Tenant for Management
-- 1. If there's a 'system' tenant with a DIFFERENT id, we try to clear it
-- Note: This might fail if there are existing users/roles linked to it (FK violation)
-- but in that case, the error will be clear.
DELETE FROM tenants WHERE code = 'system' AND id != '00000000-0000-0000-0000-000000000000';

-- 2. Insert the correct System Tenant with UUID Zero
INSERT INTO tenants (id, code, name, created_at, is_active)
VALUES ('00000000-0000-0000-0000-000000000000', 'system', 'System Management', NOW(), true)
ON CONFLICT (id) DO NOTHING;

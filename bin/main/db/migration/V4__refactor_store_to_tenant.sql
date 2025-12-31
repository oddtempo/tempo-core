-- V4__refactor_store_to_tenant.sql
-- Rename Store to Tenant for consistency

-- 1. Rename stores table to tenants
ALTER TABLE stores RENAME TO tenants;

-- 2. Rename store_id columns to tenant_id
ALTER TABLE users RENAME COLUMN store_id TO tenant_id;
ALTER TABLE roles RENAME COLUMN store_id TO tenant_id;

-- 3. Update sequences if any (PostgreSQL gen_random_uuid doesn't use sequences for IDs here)

-- 4. Update index names if they were explicitly named
ALTER INDEX IF EXISTS idx_users_store_id RENAME TO idx_users_tenant_id;
ALTER INDEX IF EXISTS idx_users_store_username RENAME TO idx_users_tenant_username;
ALTER INDEX IF EXISTS idx_roles_store_id RENAME TO idx_roles_tenant_id;

-- 5. Update foreign key constraints (Optional, but good for consistency)
-- Get constraint names first if not standard, but assuming standard from V2
-- PostgreSQL often names them table_column_fkey
ALTER TABLE users RENAME CONSTRAINT users_store_id_fkey TO users_tenant_id_fkey;
ALTER TABLE roles RENAME CONSTRAINT roles_store_id_fkey TO roles_tenant_id_fkey;

-- V3__add_audit_columns.sql
-- Synchronize database schema with BaseEntity audit fields

-- Permissions table
ALTER TABLE permissions 
    ADD COLUMN version BIGINT DEFAULT 0,
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMPTZ,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

-- Roles table
ALTER TABLE roles 
    ADD COLUMN version BIGINT DEFAULT 0,
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMPTZ,
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

-- Users table (adding missing created_by and updated_by)
ALTER TABLE users 
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_by VARCHAR(100);

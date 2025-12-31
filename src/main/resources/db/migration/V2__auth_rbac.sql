-- V2__auth_rbac.sql
-- Authentication and RBAC tables

-- Stores (Tenants)
CREATE TABLE stores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT true NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Users
-- NOTE: username is unique per store (not globally)
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id UUID NOT NULL REFERENCES stores(id),
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    full_name VARCHAR(255),
    is_active BOOLEAN DEFAULT true NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    UNIQUE(store_id, username)  -- Username unique per store
);

-- Permissions
CREATE TABLE permissions (
    code VARCHAR(100) PRIMARY KEY,
    description VARCHAR(255)
);

-- Roles (scoped to store)
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id UUID NOT NULL REFERENCES stores(id),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    UNIQUE(store_id, name)
);

-- User-Role mapping
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Role-Permission mapping
CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_code VARCHAR(100) NOT NULL REFERENCES permissions(code) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_code)
);

-- Indexes
CREATE INDEX idx_users_store_id ON users(store_id);
CREATE INDEX idx_users_store_username ON users(store_id, username);
CREATE INDEX idx_roles_store_id ON roles(store_id);

-- Default permissions
INSERT INTO permissions (code, description) VALUES
    -- Product permissions
    ('product:create', 'Create products'),
    ('product:read', 'View products'),
    ('product:update', 'Update products'),
    ('product:delete', 'Delete products'),
    -- Order permissions
    ('order:create', 'Create orders'),
    ('order:read', 'View orders'),
    ('order:update', 'Update orders'),
    ('order:cancel', 'Cancel orders'),
    -- Inventory permissions
    ('inventory:read', 'View inventory'),
    ('inventory:adjust', 'Adjust inventory'),
    -- Report permissions
    ('report:view', 'View reports'),
    ('report:export', 'Export reports'),
    -- Admin permissions
    ('user:manage', 'Manage users'),
    ('role:manage', 'Manage roles'),
    ('store:manage', 'Manage store settings');

-- ============================================================
-- ⚠️  DEMO DATA - REMOVE OR CHANGE IN PRODUCTION ⚠️
-- ============================================================
-- Demo store and user (password: "admin123")
-- TODO: Use environment variables or remove before production deployment
INSERT INTO stores (id, code, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'demo', 'Demo Store');

INSERT INTO roles (id, store_id, name, description) VALUES
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Admin', 'Full access');

-- Grant all permissions to Admin role
INSERT INTO role_permissions (role_id, permission_code)
SELECT '00000000-0000-0000-0000-000000000001', code FROM permissions;

-- Create demo admin user (password: admin123 - BCrypt hash)
-- ⚠️ CHANGE PASSWORD IN PRODUCTION!
INSERT INTO users (id, store_id, username, password_hash, full_name) VALUES
    ('00000000-0000-0000-0000-000000000001', 
     '00000000-0000-0000-0000-000000000001', 
     'admin', 
     '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4H5VQ/QzA6YtmHwqFj7YxOdMq.T6',
     'Demo Admin');

INSERT INTO user_roles (user_id, role_id) VALUES
    ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001');


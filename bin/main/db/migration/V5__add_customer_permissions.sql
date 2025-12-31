-- V5__add_customer_permissions.sql
-- Add customer management permissions for e-commerce

INSERT INTO permissions (code, description) VALUES
    ('customer:read', 'View customers'),
    ('customer:create', 'Create customers'),
    ('customer:update', 'Update customers'),
    ('customer:delete', 'Delete customers');

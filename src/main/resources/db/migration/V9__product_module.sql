-- =====================================================
-- V9__product_module.sql
-- Product Module - Database Schema
-- =====================================================

-- =====================================================
-- PRODUCTS TABLE
-- =====================================================
CREATE TABLE products (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

COMMENT ON TABLE products IS 'Product catalog - Aggregate Root';

-- =====================================================
-- PRODUCT OPTIONS TABLE
-- =====================================================
CREATE TABLE product_options (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    position INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_product_option UNIQUE (product_id, name)
);

COMMENT ON TABLE product_options IS 'Product options like Color, Size (max 3 per product)';

-- =====================================================
-- OPTION VALUES TABLE
-- =====================================================
CREATE TABLE option_values (
    id UUID PRIMARY KEY,
    option_id UUID NOT NULL REFERENCES product_options(id) ON DELETE CASCADE,
    value VARCHAR(255) NOT NULL,
    position INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_option_value UNIQUE (option_id, value)
);

COMMENT ON TABLE option_values IS 'Values for each option like Red, Blue, S, M, L';

-- =====================================================
-- PRODUCT VARIANTS TABLE
-- =====================================================
CREATE TABLE product_variants (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku VARCHAR(100) NOT NULL,
    price DECIMAL(19,2) NOT NULL CHECK (price >= 0),
    compare_at_price DECIMAL(19,2) CHECK (compare_at_price >= 0),
    option1_value VARCHAR(255),
    option2_value VARCHAR(255),
    option3_value VARCHAR(255),
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    version BIGINT DEFAULT 0
);

COMMENT ON TABLE product_variants IS 'Product variants with unique SKU';

-- =====================================================
-- INDEXES
-- =====================================================

-- Products
CREATE INDEX idx_products_tenant ON products(tenant_id);
CREATE INDEX idx_products_title ON products(tenant_id, title);

-- Product Options
CREATE INDEX idx_product_options_product ON product_options(product_id);

-- Option Values
CREATE INDEX idx_option_values_option ON option_values(option_id);

-- Product Variants
CREATE INDEX idx_variants_tenant ON product_variants(tenant_id);
CREATE INDEX idx_variants_product ON product_variants(product_id);
CREATE UNIQUE INDEX idx_variants_sku ON product_variants(sku);
CREATE INDEX idx_variants_option1 ON product_variants(option1_value);

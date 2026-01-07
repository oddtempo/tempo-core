-- =====================================================
-- V10__fix_product_variant_option_columns.sql
-- Rename option columns to match entity field names
-- =====================================================

-- Rename option value columns to match ProductVariant entity
ALTER TABLE product_variants RENAME COLUMN option1_value TO option1;
ALTER TABLE product_variants RENAME COLUMN option2_value TO option2;
ALTER TABLE product_variants RENAME COLUMN option3_value TO option3;

-- Drop image_url column (not in entity)
ALTER TABLE product_variants DROP COLUMN IF EXISTS image_url;

-- Drop version column (removed from BaseEntity, only AggregateRoot has it)
ALTER TABLE product_variants DROP COLUMN IF EXISTS version;

-- Drop created_by, updated_by (not in Audit embeddable, only created_at, updated_at)
ALTER TABLE product_variants DROP COLUMN IF EXISTS created_by;
ALTER TABLE product_variants DROP COLUMN IF EXISTS updated_by;

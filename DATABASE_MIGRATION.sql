-- FarmFeed Database Migration Script
-- Run this script to update the database schema for the new features

-- ============================================================
-- 1. UPDATE ORDERS TABLE WITH NEW COLUMNS
-- ============================================================

-- Add farmer details columns if they don't exist
ALTER TABLE orders ADD COLUMN IF NOT EXISTS farmer_name VARCHAR(255) AFTER farmer_id;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS farmer_phone VARCHAR(20) AFTER farmer_name;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS farmer_address VARCHAR(500) AFTER farmer_phone;

-- Add product details columns if they don't exist
ALTER TABLE orders ADD COLUMN IF NOT EXISTS product_name VARCHAR(255) AFTER product_id;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS product_quantity INT AFTER product_name;

-- Update status column to use new ENUM values (pending, shifting, delivered, cancelled)
-- First, let's update existing statuses
UPDATE orders SET status = 'pending' WHERE status = 'ordered';
UPDATE orders SET status = 'pending' WHERE status = 'unshipped';
UPDATE orders SET status = 'delivered' WHERE status = 'completed' OR status = 'shipped';

-- Now update the column definition
ALTER TABLE orders MODIFY COLUMN status ENUM('pending', 'shifting', 'delivered', 'cancelled') DEFAULT 'pending';

-- Make delivery_address optional (since we now use farmer_address)
ALTER TABLE orders MODIFY COLUMN delivery_address VARCHAR(500);

-- ============================================================
-- 2. CREATE INDEX FOR BETTER QUERY PERFORMANCE
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_vendor_id ON orders(vendor_id);
CREATE INDEX IF NOT EXISTS idx_orders_farmer_id ON orders(farmer_id);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

-- ============================================================
-- 3. VERIFY VENDOR_INVENTORY TABLE STRUCTURE
-- ============================================================

-- Ensure vendor_inventory table has all required columns
ALTER TABLE vendor_inventory ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE vendor_inventory ADD COLUMN IF NOT EXISTS added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE vendor_inventory ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- ============================================================
-- 4. ENSURE FARMER TABLE HAS ADDRESS COLUMNS
-- ============================================================

ALTER TABLE farmer ADD COLUMN IF NOT EXISTS address VARCHAR(500);
ALTER TABLE farmer ADD COLUMN IF NOT EXISTS city VARCHAR(100);
ALTER TABLE farmer ADD COLUMN IF NOT EXISTS state VARCHAR(100);

-- ============================================================
-- 5. ENSURE SHOPKEEPER (VENDOR) TABLE IS CORRECT
-- ============================================================

-- Verify shopkeeper table columns
ALTER TABLE shopkeeper MODIFY COLUMN password TEXT;  -- Ensure password field is large enough for bcrypt hash

-- ============================================================
-- 6. ENSURE ADMIN TABLE HAS NECESSARY COLUMNS
-- ============================================================

ALTER TABLE admins MODIFY COLUMN password TEXT;  -- Ensure password field is large enough for bcrypt hash
ALTER TABLE admins ADD COLUMN IF NOT EXISTS last_login TIMESTAMP NULL;

-- ============================================================
-- 7. CREATE OR VERIFY PRODUCT TABLE
-- ============================================================

-- Ensure Product table has image_link
ALTER TABLE Product ADD COLUMN IF NOT EXISTS image_link VARCHAR(2000);
ALTER TABLE Product ADD COLUMN IF NOT EXISTS vendor_id BIGINT;

-- ============================================================
-- 8. DATA POPULATION EXAMPLES (Optional)
-- ============================================================

-- Example: Update existing orders with farmer and product details (if you want to backfill)
-- This is optional and depends on your current data
/*
UPDATE orders o 
JOIN farmer f ON o.farmer_id = f.farmer_id
SET o.farmer_name = f.full_name,
    o.farmer_phone = f.phone,
    o.farmer_address = COALESCE(f.address, o.delivery_address)
WHERE o.farmer_name IS NULL OR o.farmer_name = '';

UPDATE orders o
JOIN Product p ON o.product_id = p.id
SET o.product_name = p.product_name,
    o.product_quantity = o.quantity
WHERE o.product_name IS NULL OR o.product_name = '';
*/

-- ============================================================
-- 9. VERIFICATION QUERIES
-- ============================================================

-- Verify table structures
DESCRIBE orders;
DESCRIBE vendor_inventory;
DESCRIBE farmer;
DESCRIBE shopkeeper;
DESCRIBE admins;
DESCRIBE Product;

-- Verify indexes
SHOW INDEX FROM orders;

-- ============================================================
-- 10. BACKUP REMINDER
-- ============================================================

-- IMPORTANT: Before running this migration, create a backup:
-- mysqldump -u root -p FarmFeed > FarmFeed_backup_$(date +%Y%m%d_%H%M%S).sql

-- ============================================================
-- MIGRATION COMPLETE
-- ============================================================

-- All tables are now updated with the new schema
-- The application is ready to use the new features:
-- - Vendor order management with acceptance/rejection
-- - Farmer order tracking with status updates
-- - Admin order dashboard
-- - Password encryption with BCrypt

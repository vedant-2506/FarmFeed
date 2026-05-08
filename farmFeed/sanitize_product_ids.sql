-- SQL script to sanitize product IDs and resolve 'pr997' style issues
-- This script extracts the numeric part from 'prXXXX' and 'bighaat_XXXX' keys

-- 1. Identify problematic records (Audit only)
SELECT mysql_import_key, product_name 
FROM bighaat_products_raw 
WHERE mysql_import_key NOT REGEXP '^[0-9]+$' AND mysql_import_key NOT LIKE 'bighaat_%';

-- 2. Clean up 'pr' prefix if it exists (hypothetical based on error message)
-- We convert 'pr997' to 'bighaat_997' to maintain consistency with the application's bighaat prefix logic
UPDATE bighaat_products_raw
SET mysql_import_key = CONCAT('bighaat_', REGEXP_REPLACE(mysql_import_key, '[^0-9]', ''))
WHERE mysql_import_key LIKE 'pr%';

-- 3. Ensure all non-numeric keys have the 'bighaat_' prefix if they don't already
UPDATE bighaat_products_raw
SET mysql_import_key = CONCAT('bighaat_', mysql_import_key)
WHERE mysql_import_key REGEXP '^[0-9]+$';

-- 4. Verify after cleanup
SELECT COUNT(*) as malformed_count 
FROM bighaat_products_raw 
WHERE mysql_import_key NOT LIKE 'bighaat_%';

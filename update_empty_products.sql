UPDATE products SET product_name = 'Unnamed Product' WHERE product_name = '' OR product_name IS NULL;
UPDATE products SET primary_category = 'General' WHERE primary_category = '' OR primary_category IS NULL;
UPDATE products SET price_inr = 0.0 WHERE price_inr IS NULL OR price_inr <= 0;
UPDATE products SET description_clean = 'No description available' WHERE description_clean = '' OR description_clean IS NULL;
UPDATE products SET vendor_id = 1 WHERE vendor_id IS NULL;

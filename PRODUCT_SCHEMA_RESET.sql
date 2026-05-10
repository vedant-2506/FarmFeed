-- FarmFeed clean product schema reset
-- Drop the legacy product table variants first, then recreate a clean schema

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS Product;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE products (
    product_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(120) NOT NULL,
    subcategory VARCHAR(120) DEFAULT NULL,
    price DECIMAL(12,2) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(2000) DEFAULT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    rating DECIMAL(4,2) NOT NULL DEFAULT 0.00,
    total_reviews INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    UNIQUE KEY uq_products_name (name),
    KEY idx_products_category (category),
    KEY idx_products_stock (stock_quantity),
    KEY idx_products_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optional vendor inventory bridge remains separate from the product table.
-- If you need a fresh vendor inventory table as well, recreate it separately so the
-- vendor-specific price and stock data stays out of the product schema.
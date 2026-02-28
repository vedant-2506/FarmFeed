-- ========================================================
-- FarmFeed Database Cleanup & Schema Reset
-- ========================================================
-- Run this script in MySQL to fix your database
-- ========================================================

USE farmfeed;

-- Step 1: Drop all indexes that depend on farmer/shopkeeper
ALTER TABLE cart DROP FOREIGN KEY fk_cart_farmer;
ALTER TABLE cart DROP FOREIGN KEY fk_cart_fertilizer;
ALTER TABLE payment DROP FOREIGN KEY fk_payment_farmer;
ALTER TABLE orders DROP FOREIGN KEY fk_orders_farmer;
ALTER TABLE orders DROP FOREIGN KEY fk_orders_payment;

-- Step 2: Drop existing tables (with old columns)
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS farmer;
DROP TABLE IF EXISTS shopkeeper;

-- ========================================================
-- Step 3: CREATE NEW TABLES WITH CORRECT SCHEMA
-- ========================================================

-- Fertilizers Table (NO CHANGES)
CREATE TABLE fertilizers (
    fertilizer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    image LONGBLOB,
    price INT NOT NULL,
    description VARCHAR(255),
    feedback VARCHAR(255),
    rating_review INT CHECK (rating_review BETWEEN 1 AND 5),
    stock INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- FARMER TABLE (UPDATED - phone-based login)
CREATE TABLE farmer (
    farmer_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone (phone)
);

-- SHOPKEEPER TABLE (UPDATED - email-based login + password added)
CREATE TABLE shopkeeper (
    shop_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL,
    shop_name VARCHAR(100) NOT NULL,
    licence_number VARCHAR(50) UNIQUE NOT NULL,
    shop_address VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_licence (licence_number)
);

-- CART TABLE (with foreign keys)
CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    farmer_id INT NOT NULL,
    fertilizer_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_farmer
        FOREIGN KEY (farmer_id)
        REFERENCES farmer(farmer_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cart_fertilizer
        FOREIGN KEY (fertilizer_id)
        REFERENCES fertilizers(fertilizer_id)
        ON DELETE CASCADE,
    INDEX idx_farmer (farmer_id),
    INDEX idx_fertilizer (fertilizer_id)
);

-- PAYMENT TABLE (with foreign key)
CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    farmer_id INT NOT NULL,
    amount DECIMAL(10,2),
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_farmer
        FOREIGN KEY (farmer_id)
        REFERENCES farmer(farmer_id)
        ON DELETE CASCADE,
    INDEX idx_farmer (farmer_id)
);

-- ORDERS TABLE (with foreign keys)
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_date DATE NOT NULL,
    farmer_id INT NOT NULL,
    delivery_id INT,
    payment_id INT,
    total_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_farmer
        FOREIGN KEY (farmer_id)
        REFERENCES farmer(farmer_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_orders_payment
        FOREIGN KEY (payment_id)
        REFERENCES payment(payment_id)
        ON DELETE SET NULL,
    INDEX idx_farmer (farmer_id),
    INDEX idx_order_date (order_date)
);

-- ========================================================
-- Step 4: INSERT SAMPLE DATA
-- ========================================================

-- Sample Fertilizers
INSERT INTO fertilizers (name, price, description, stock) VALUES
('Urea Fertilizer', 600, 'High-nitrogen fertilizer used for boosting plant growth and improving crop yields.', 100),
('DAP Fertilizer', 1200, 'Diammonium phosphate, rich in nitrogen and phosphorus, ideal for crop root development.', 80),
('Potash Fertilizer', 950, 'Helps in improving crop resistance against diseases and enhances water retention.', 120),
('NPK Fertilizer', 1100, 'Balanced fertilizer with Nitrogen, Phosphorus, and Potassium for overall crop health.', 90),
('Organic Compost', 400, 'Eco-friendly compost that improves soil fertility and promotes sustainable farming.', 150),
('Bio Fertilizer', 750, 'Contains living microorganisms that enhance soil nutrient availability and plant growth.', 70),
('Superphosphate', 850, 'Phosphorus-rich fertilizer for root growth and early plant development.', 110),
('Vermicompost', 500, 'Organic fertilizer produced using earthworms, improves soil structure and fertility.', 130),
('Zinc Sulphate', 700, 'Micronutrient fertilizer essential for chlorophyll production and plant metabolism.', 95),
('Ammonium Sulphate', 950, 'Rich source of nitrogen and sulfur, improves leafy vegetable growth and yields.', 85);

-- Sample Farmers (using PHONE for login - matching SignUp form)
INSERT INTO farmer (full_name, phone, password, address) VALUES
('Rajesh Kumar', '9876543210', 'pass123', 'Pune, Maharashtra'),
('Priya Sharma', '9876543211', 'pass123', 'Mumbai, Maharashtra'),
('Amit Patel', '9876543212', 'pass123', 'Nashik, Maharashtra');

-- Sample Shopkeepers (using EMAIL for login - matching SignUp form)
INSERT INTO shopkeeper (owner_name, shop_name, licence_number, shop_address, email, password) VALUES
('Suresh Patil', 'Green Agro Store', 'LIC001', 'Shop 12, Market Road, Pune', 'greenagro@example.com', 'pass123'),
('Meena Singh', 'Farm Fresh Supplies', 'LIC002', 'Plot 45, Agricultural Market, Mumbai', 'farmfresh@example.com', 'pass123');

-- ========================================================
-- Step 5: VERIFY TABLES CREATED
-- ========================================================

SELECT 'Farmers' as TableName, COUNT(*) as RecordCount FROM farmer
UNION ALL
SELECT 'Shopkeepers', COUNT(*) FROM shopkeeper
UNION ALL
SELECT 'Fertilizers', COUNT(*) FROM fertilizers;

-- Show table structures
DESCRIBE farmer;
DESCRIBE shopkeeper;
DESCRIBE fertilizers;

SHOW TABLES;

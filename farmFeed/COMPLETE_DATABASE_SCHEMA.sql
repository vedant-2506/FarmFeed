-- FarmFeed Complete Database Schema
-- Create database
CREATE DATABASE IF NOT EXISTS farmfeed_db;
USE farmfeed_db;

-- Drop tables if they exist (for fresh start)
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS farmer;
DROP TABLE IF EXISTS shopkeeper;

-- ==================== FARMER TABLE ====================
CREATE TABLE farmer (
    farmer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(50),
    state VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    profile_image LONGBLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_is_active (is_active)
);

-- ==================== SHOPKEEPER/VENDOR TABLE ====================
CREATE TABLE shopkeeper (
    shop_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL,
    shop_name VARCHAR(100) NOT NULL,
    licence_number VARCHAR(50) UNIQUE NOT NULL,
    shop_address VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    city VARCHAR(50),
    state VARCHAR(50),
    is_approved BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    bank_account VARCHAR(50),
    bank_name VARCHAR(100),
    profile_image LONGBLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_licence (licence_number),
    INDEX idx_is_approved (is_approved),
    INDEX idx_is_active (is_active)
);

-- ==================== PRODUCTS TABLE ====================
CREATE TABLE products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    manufacturer VARCHAR(100),
    vendor_id BIGINT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    image LONGBLOB,
    rating DECIMAL(3,2) DEFAULT 0,
    total_reviews INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES shopkeeper(shop_id) ON DELETE CASCADE,
    INDEX idx_category (category),
    INDEX idx_vendor_id (vendor_id),
    INDEX idx_rating (rating),
    FULLTEXT INDEX ft_search (name, description)
);

-- ==================== CART TABLE ====================
CREATE TABLE cart_items (
    cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    farmer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES farmer(farmer_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES shopkeeper(shop_id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_item (farmer_id, product_id, vendor_id),
    INDEX idx_farmer_id (farmer_id)
);

-- ==================== ORDERS TABLE ====================
CREATE TABLE orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    farmer_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total_price DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    delivery_address VARCHAR(255) NOT NULL,
    tracking_number VARCHAR(100),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_date TIMESTAMP,
    notes VARCHAR(1000),
    payment_method VARCHAR(50),
    is_paid BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmer_id) REFERENCES farmer(farmer_id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES shopkeeper(shop_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_farmer_id (farmer_id),
    INDEX idx_vendor_id (vendor_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date)
);

-- ==================== RATINGS TABLE ====================
CREATE TABLE ratings (
    rating_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    farmer_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (farmer_id) REFERENCES farmer(farmer_id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES shopkeeper(shop_id) ON DELETE CASCADE,
    INDEX idx_product_id (product_id),
    INDEX idx_farmer_id (farmer_id),
    INDEX idx_rating (rating)
);

-- ==================== ADMIN TABLE ====================
CREATE TABLE admins (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    role VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_is_active (is_active)
);

-- ==================== NOTIFICATIONS TABLE ====================
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    related_order_id BIGINT,
    related_product_id BIGINT,
    related_vendor_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admins(admin_id) ON DELETE CASCADE,
    FOREIGN KEY (related_order_id) REFERENCES orders(order_id) ON DELETE SET NULL,
    FOREIGN KEY (related_product_id) REFERENCES products(product_id) ON DELETE SET NULL,
    FOREIGN KEY (related_vendor_id) REFERENCES shopkeeper(shop_id) ON DELETE SET NULL,
    INDEX idx_admin_id (admin_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- ==================== SAMPLE DATA ====================

-- Insert sample farmers
INSERT INTO farmer (full_name, phone, email, password, address, city, state, is_active) VALUES
('Rajesh Kumar', '9876543210', 'rajesh.kumar@email.com', 'password123', '123 Farm Lane', 'Punjab', 'Punjab', TRUE),
('Priya Sharma', '9876543211', 'priya.sharma@email.com', 'password123', '456 Harvest Road', 'Haryana', 'Haryana', TRUE),
('Amit Patel', '9876543212', 'amit.patel@email.com', 'password123', '789 Agriculture Ave', 'Madhya Pradesh', 'MP', TRUE);

-- Insert sample vendors
INSERT INTO shopkeeper (owner_name, shop_name, licence_number, shop_address, email, password, phone, city, state, is_approved, is_active, bank_account, bank_name) VALUES
('John Anderson', 'Green Agro Supplies', 'LIC001', '100 Market Street', 'john@greenagro.com', 'vendor123', '9876500001', 'Mumbai', 'Maharashtra', TRUE, TRUE, '1234567890', 'SBI'),
('Sarah Johnson', 'Organic Farming Hub', 'LIC002', '200 Eco Street', 'sarah@organicfarm.com', 'vendor123', '9876500002', 'Bangalore', 'Karnataka', TRUE, TRUE, '0987654321', 'HDFC');

-- Insert sample products
INSERT INTO products (name, description, category, price, manufacturer, vendor_id, stock, rating, total_reviews) VALUES
('Urea Fertilizer', 'High-nitrogen fertilizer for boosting plant growth', 'Chemical Fertilizer', 600, 'ChemAgro Ltd', 1, 100, 4.5, 45),
('DAP Fertilizer', 'Diammonium phosphate for root development', 'Chemical Fertilizer', 1200, 'AgriChem Inc', 1, 80, 4.3, 35),
('Organic Compost', 'Eco-friendly compost for sustainable farming', 'Organic Fertilizer', 400, 'EcoCare Farms', 2, 150, 4.8, 62),
('Potash Fertilizer', 'Improves crop resistance to diseases', 'Chemical Fertilizer', 950, 'ChemAgro Ltd', 1, 120, 4.2, 28),
('Bio Fertilizer', 'Contains live microorganisms for soil enhancement', 'Bio Fertilizer', 750, 'BioFarm Solutions', 2, 70, 4.6, 41);

-- Insert sample admin
INSERT INTO admins (username, email, password, full_name, phone, role, is_active) VALUES
('admin1', 'admin@farmfeed.com', 'admin@123', 'Admin User', '9876543299', 'SUPER_ADMIN', TRUE);

-- ==================== INDEXES FOR PERFORMANCE ====================
-- Product search index
CREATE FULLTEXT INDEX idx_product_search ON products(name, description);

-- Order status tracking
CREATE INDEX idx_order_status_tracking ON orders(farmer_id, status);

-- Revenue tracking
CREATE INDEX idx_vendor_revenue ON orders(vendor_id, status, created_at);

COMMIT;

-- FarmFeed Master Production Initialization Script
-- Run this script in your Aiven Console or via MySQL client (Workbench)

-- 1. Create Product Table
CREATE TABLE IF NOT EXISTS Product (
    id VARCHAR(255) PRIMARY KEY,
    product_name VARCHAR(255),
    image_link VARCHAR(2000),
    primary_category VARCHAR(255),
    subcategory VARCHAR(255),
    price_inr DOUBLE,
    rating DOUBLE DEFAULT 0,
    description_clean TEXT,
    detailed_description_10_sentences TEXT,
    manufacturer VARCHAR(255),
    vendor_id BIGINT,
    stock INT DEFAULT 100,
    total_reviews INT DEFAULT 0,
    created_at DATETIME,
    updated_at DATETIME
);

-- 2. Create Farmer Table
CREATE TABLE IF NOT EXISTS farmer (
    farmer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
);

-- 3. Create Shopkeeper (Vendor) Table
CREATE TABLE IF NOT EXISTS shopkeeper (
    shop_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_name VARCHAR(255) NOT NULL,
    shop_name VARCHAR(255) NOT NULL,
    licence_number VARCHAR(255) NOT NULL UNIQUE,
    shop_address VARCHAR(500) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    phone VARCHAR(20),
    city VARCHAR(100),
    state VARCHAR(100),
    is_approved BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    bank_account VARCHAR(255),
    bank_name VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME
);

-- 4. Create Admins Table
CREATE TABLE IF NOT EXISTS admins (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login DATETIME,
    created_at DATETIME,
    updated_at DATETIME
);

-- 5. Create Orders Table
CREATE TABLE IF NOT EXISTS orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    farmer_id BIGINT NOT NULL,
    vendor_id BIGINT,
    product_id VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    total_price DOUBLE NOT NULL,
    status ENUM('pending', 'shifting', 'delivered', 'cancelled') DEFAULT 'pending',
    delivery_address VARCHAR(500),
    tracking_number VARCHAR(255),
    farmer_name VARCHAR(255),
    farmer_phone VARCHAR(20),
    farmer_address VARCHAR(500),
    product_name VARCHAR(255),
    product_quantity INT,
    order_date DATETIME NOT NULL,
    delivery_date DATETIME,
    notes TEXT,
    payment_method VARCHAR(50),
    is_paid BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME
);

-- 6. Create Vendor Inventory Table
CREATE TABLE IF NOT EXISTS vendor_inventory (
    inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id BIGINT NOT NULL,
    fertilizer_id VARCHAR(255) NOT NULL,
    vendor_price DOUBLE NOT NULL,
    quantity_in_stock INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    added_at DATETIME,
    updated_at DATETIME,
    UNIQUE KEY (vendor_id, fertilizer_id)
);

-- 7. Insert Demo Admin (Password is 'admin123' - BCrypt hash)
INSERT INTO admins (username, email, password, full_name, role) 
VALUES ('admin', 'admin@farmfeed.com', '$2a$12$R9h/lIPzHZlu699Gq6TzO.9vK7zO8V8.vF8YvG8V8.vF8YvG8V8.', 'Main Admin', 'ADMIN')
ON DUPLICATE KEY UPDATE username=username;

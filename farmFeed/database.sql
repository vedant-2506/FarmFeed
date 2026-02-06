-- Create database
CREATE DATABASE IF NOT EXISTS farmfeed;
USE farmfeed;

-- Drop tables if they exist (for fresh start)
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS fertilizers;
DROP TABLE IF EXISTS farmer;
DROP TABLE IF EXISTS shopkeeper;

-- Create fertilizers table
CREATE TABLE fertilizers (
    fertilizer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    image LONGBLOB,
    price INT NOT NULL,
    description VARCHAR(255),
    feedback VARCHAR(255),
    rating_review INT CHECK (rating_review BETWEEN 1 AND 5),
    stock INT NOT NULL
);

-- Create farmer table
CREATE TABLE farmer (
    farmer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    gender VARCHAR(30),
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(30) NOT NULL,
    address VARCHAR(100),
    age INT
);

-- Create cart table
CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    farmer_id INT NOT NULL,
    fertilizer_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_cart_farmer
        FOREIGN KEY (farmer_id)
        REFERENCES farmer(farmer_id)
        ON DELETE CASCADE,
        CONSTRAINT fk_cart_fertilizer
        FOREIGN KEY (fertilizer_id)
        REFERENCES fertilizers(fertilizer_id)
        ON DELETE CASCADE
);

-- Create payment table
CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    farmer_id INT NOT NULL,
    amount DECIMAL(10,2),
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_farmer
        FOREIGN KEY (farmer_id)
        REFERENCES farmer(farmer_id)
        ON DELETE CASCADE
);

-- Create orders table
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_date DATE NOT NULL,
    farmer_id INT NOT NULL,
    delivery_id INT,
    payment_id INT,
    total_amount DECIMAL(10,2),
    CONSTRAINT fk_orders_farmer
        FOREIGN KEY (farmer_id)
        REFERENCES farmer(farmer_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_orders_payment
        FOREIGN KEY (payment_id)
        REFERENCES payment(payment_id)
        ON DELETE SET NULL
);

-- Create shopkeeper table
CREATE TABLE shopkeeper (
    shop_id INT AUTO_INCREMENT PRIMARY KEY,
    shop_name VARCHAR(50) NOT NULL,
    owner_name VARCHAR(50) NOT NULL,
    licence_number VARCHAR(30) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    address VARCHAR(150) NOT NULL
);

-- Insert sample fertilizers data
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

-- Insert sample farmer data
INSERT INTO farmer (name, contact_number, gender, email, password, address, age) VALUES
('Rajesh Kumar', '9876543210', 'Male', 'rajesh@example.com', 'pass123', 'Pune, Maharashtra', 35),
('Priya Sharma', '9876543211', 'Female', 'priya@example.com', 'pass123', 'Mumbai, Maharashtra', 28),
('Amit Patel', '9876543212', 'Male', 'amit@example.com', 'pass123', 'Nashik, Maharashtra', 42);

-- Insert sample shopkeeper data
INSERT INTO shopkeeper (shop_name, owner_name, licence_number, email, address) VALUES
('Green Agro Store', 'Suresh Patil', 'LIC001', 'greenagro@example.com', 'Shop 12, Market Road, Pune'),
('Farm Fresh Supplies', 'Meena Singh', 'LIC002', 'farmfresh@example.com', 'Plot 45, Agricultural Market, Mumbai');

-- Show all fertilizers to verify
SELECT * FROM fertilizers;

-- Show counts
SELECT 'Fertilizers' as TableName, COUNT(*) as RecordCount FROM fertilizers
UNION ALL
SELECT 'Farmers', COUNT(*) FROM farmer
UNION ALL
SELECT 'Shopkeepers', COUNT(*) FROM shopkeeper;
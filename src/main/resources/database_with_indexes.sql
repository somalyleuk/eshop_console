-- Enhanced Database Schema for 10 Million Products
-- Drop existing tables if they exist
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE SCHEMA IF NOT EXISTS eshop;
SET SEARCH_PATH = eshop;

-- Create tables with optimized structure for large datasets
CREATE TABLE users (
    id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Optimized products table for 10M+ records
CREATE TABLE products (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    category_id VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE cart_items (
    id VARCHAR(10) PRIMARY KEY,
    user_id VARCHAR(10) NOT NULL,
    product_id VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE orders (
    id VARCHAR(10) PRIMARY KEY,
    user_id VARCHAR(10) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id VARCHAR(10) PRIMARY KEY,
    order_id VARCHAR(10) NOT NULL,
    product_id VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- PERFORMANCE INDEXES FOR 10 MILLION PRODUCTS
-- These indexes will dramatically improve query performance

-- Index for product name searches (most common search)
CREATE INDEX idx_products_name ON products(name);

-- Index for category filtering
CREATE INDEX idx_products_category ON products(category_id);

-- Index for price range queries
CREATE INDEX idx_products_price ON products(price);

-- Index for date-based queries
CREATE INDEX idx_products_created_at ON products(created_at);

-- Index for stock quantity queries
CREATE INDEX idx_products_stock ON products(stock_quantity);

-- Composite index for category + price queries
CREATE INDEX idx_products_category_price ON products(category_id, price);

-- Composite index for category + stock queries
CREATE INDEX idx_products_category_stock ON products(category_id, stock_quantity);

-- Index for case-insensitive name searches
CREATE INDEX idx_products_name_lower ON products(LOWER(name));

-- Index for partial name searches (LIKE queries)
CREATE INDEX idx_products_name_pattern ON products(name text_pattern_ops);

-- Index for user lookups
CREATE INDEX idx_users_username ON users(username);

-- Index for order lookups by user
CREATE INDEX idx_orders_user ON orders(user_id);

-- Index for order items by order
CREATE INDEX idx_order_items_order ON order_items(order_id);

-- Index for cart items by user
CREATE INDEX idx_cart_items_user ON cart_items(user_id);

-- Insert sample data
INSERT INTO categories (id, name, description) VALUES
('CAT001', 'Electronics', 'Electronic devices and gadgets'),
('CAT002', 'Clothing', 'Apparel and fashion items'),
('CAT003', 'Books', 'Books and educational materials'),
('CAT004', 'Home & Garden', 'Home improvement and garden supplies'),
('CAT005', 'Sports', 'Sports equipment and accessories'),
('CAT006', 'Automotive', 'Automotive parts and accessories'),
('CAT007', 'Health & Beauty', 'Health and beauty products'),
('CAT008', 'Toys & Games', 'Toys and games for all ages'),
('CAT009', 'Food & Beverages', 'Food and beverage products'),
('CAT010', 'Jewelry', 'Jewelry and accessories');

-- Insert sample products (just a few for testing)
INSERT INTO products (id, name, description, price, stock_quantity, category_id) VALUES
('PRD0000001', 'Smartphone X1', 'Latest Android smartphone with advanced features', 599.99, 50, 'CAT001'),
('PRD0000002', 'Laptop Pro', 'High-performance laptop for professionals', 999.99, 30, 'CAT001'),
('PRD0000003', 'T-Shirt Classic', 'Comfortable cotton t-shirt', 19.99, 100, 'CAT002'),
('PRD0000004', 'Denim Jeans', 'Stylish denim jeans', 49.99, 75, 'CAT002'),
('PRD0000005', 'Java Programming Book', 'Comprehensive guide to Java programming', 39.99, 25, 'CAT003');

-- ANALYZE tables for query optimization
ANALYZE products;
ANALYZE categories;
ANALYZE users;
ANALYZE orders;
ANALYZE order_items;
ANALYZE cart_items;

-- Display table statistics
SELECT 
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats 
WHERE schemaname = 'eshop' 
ORDER BY tablename, attname; 
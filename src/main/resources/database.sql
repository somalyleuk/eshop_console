-- Drop existing tables if they exist
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE SCHEMA IF NOT EXISTS eshop;
SET SEARCH_PATH = eshop;

-- Create tables with VARCHAR IDs
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

CREATE TABLE products (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
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

-- Insert sample data
INSERT INTO categories (id, name, description) VALUES
('CAT001', 'Electronics', 'Electronic devices and gadgets'),
('CAT002', 'Clothing', 'Apparel and fashion items'),
('CAT003', 'Books', 'Books and educational materials');

INSERT INTO products (id, name, description, price, stock_quantity, category_id) VALUES
('PRD001', 'Smartphone', 'Latest Android smartphone', 599.99, 50, 'CAT001'),
('PRD002', 'Laptop', 'High-performance laptop', 999.99, 30, 'CAT001'),
('PRD003', 'T-Shirt', 'Cotton t-shirt', 19.99, 100, 'CAT002'),
('PRD004', 'Jeans', 'Denim jeans', 49.99, 75, 'CAT002'),
('PRD005', 'Java Programming Book', 'Learn Java programming', 39.99, 25, 'CAT003');
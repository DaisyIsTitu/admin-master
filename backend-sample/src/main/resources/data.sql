-- Insert sample users
INSERT INTO users (username, email, password, first_name, last_name, role, active, created_at, updated_at) VALUES
('admin', 'admin@example.com', '$2a$10$YxlJY5BxwYl3XqZ8bY7hDO7sFGBxH3HqxGKPhBPyCuHYh9nQn/Fze', 'Admin', 'User', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('manager', 'manager@example.com', '$2a$10$YxlJY5BxwYl3XqZ8bY7hDO7sFGBxH3HqxGKPhBPyCuHYh9nQn/Fze', 'Manager', 'User', 'MANAGER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('john.doe', 'john.doe@example.com', '$2a$10$YxlJY5BxwYl3XqZ8bY7hDO7sFGBxH3HqxGKPhBPyCuHYh9nQn/Fze', 'John', 'Doe', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane.smith', 'jane.smith@example.com', '$2a$10$YxlJY5BxwYl3XqZ8bY7hDO7sFGBxH3HqxGKPhBPyCuHYh9nQn/Fze', 'Jane', 'Smith', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Note: All passwords are 'password123' encrypted with BCrypt

-- Insert sample products
INSERT INTO products (name, description, sku, price, stock, category, active, created_at, updated_at) VALUES
('MacBook Pro 16"', 'High-performance laptop for professionals', 'MBP-16-2023', 2499.99, 25, 'Electronics', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('iPhone 15 Pro', 'Latest flagship smartphone', 'IPH-15P-2023', 1199.99, 50, 'Electronics', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('iPad Air', 'Versatile tablet for work and play', 'IPA-AIR-2023', 799.99, 35, 'Electronics', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AirPods Pro', 'Premium wireless earbuds', 'APP-PRO-2023', 249.99, 100, 'Accessories', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Magic Keyboard', 'Wireless keyboard with Touch ID', 'MKB-TD-2023', 149.99, 75, 'Accessories', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dell XPS 15', 'Premium Windows laptop', 'DXP-15-2023', 1899.99, 20, 'Electronics', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Samsung Galaxy S23', 'Android flagship smartphone', 'SGS-23-2023', 999.99, 45, 'Electronics', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sony WH-1000XM5', 'Noise-cancelling headphones', 'SWH-XM5-2023', 399.99, 60, 'Accessories', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Logitech MX Master 3', 'Advanced wireless mouse', 'LMX-M3-2023', 99.99, 120, 'Accessories', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('LG 27" 4K Monitor', 'Professional 4K display', 'LG-27-4K-2023', 599.99, 15, 'Electronics', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample orders with items
INSERT INTO orders (order_number, user_id, status, total_amount, shipping_address, notes, created_at, updated_at) VALUES
('ORD-20240101-001', 3, 'DELIVERED', 3699.98, '123 Main St, New York, NY 10001', 'Please deliver between 9 AM - 5 PM', DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -25, CURRENT_TIMESTAMP)),
('ORD-20240105-002', 4, 'SHIPPED', 1449.98, '456 Oak Ave, Los Angeles, CA 90001', NULL, DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP)),
('ORD-20240110-003', 3, 'PROCESSING', 2249.97, '789 Pine Rd, Chicago, IL 60601', 'Gift wrapping requested', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP)),
('ORD-20240115-004', 4, 'PENDING', 999.99, '321 Elm St, Houston, TX 77001', NULL, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP));

-- Insert order items
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 2499.99),
(1, 2, 1, 1199.99),
(2, 3, 1, 799.99),
(2, 4, 1, 249.99),
(2, 8, 1, 399.99),
(3, 2, 1, 1199.99),
(3, 4, 2, 249.99),
(3, 9, 6, 99.99),
(4, 7, 1, 999.99);
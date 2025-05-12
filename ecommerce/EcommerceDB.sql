create database ecommerce_db;
use ecommerce_db;
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    description TEXT,
    stock_quantity INT NOT NULL
);
CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    product_id INT,
    quantity INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    shipping_address VARCHAR(255) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
INSERT INTO customers (name, email, password) VALUES
('John Doe', 'john@example.com', 'password123'),
('Jane Smith', 'jane@example.com', 'password123');
INSERT INTO products (name, price, description, stock_quantity) VALUES
('Laptop', 1500.00, 'High-end gaming laptop', 10),
('Smartphone', 700.00, 'Latest smartphone model', 50),
('Headphones', 150.00, 'Wireless noise-canceling headphones', 30);
DESCRIBE products;
DESCRIBE cart;
-- ALTER TABLE orders ADD COLUMN total_price DECIMAL(10, 2);

CREATE USER 'ecom_user'@'localhost' identified BY 'ecom_pass';
GRANT ALL privileges ON ecommerce_db.* to 'ecom_user'@'localhost';
flush privileges;

select * from customers;
ALTER TABLE products
DROP COLUMN stockQuantity;  -- Replace with the actual column name


SELECT 
    o.order_id,
    o.order_date,
    o.total_price,
    o.shipping_address,
    p.product_id,
    p.name AS product_name,
    p.description,
    oi.quantity,
    oi.price AS price_per_unit,
    (oi.quantity * oi.price) AS item_total
FROM 
    orders o
JOIN 
    order_items oi ON o.order_id = oi.order_id
JOIN 
    products p ON oi.product_id = p.product_id
WHERE 
    o.customer_id = 4
ORDER BY 
    o.order_id, p.product_id;

select * from customers;
select * from cart;
select * from products;
select * from orders;
select * from order_items;

CREATE TABLE categories (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    delete_flag INTEGER DEFAULT 0,
    insert_date DATE
);

CREATE TABLE items (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER,
    description VARCHAR(255),
    stock INTEGER,
    image VARCHAR(255),
    category_id INTEGER,
    delete_flag INTEGER DEFAULT 0,
    insert_date DATE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10),
    address VARCHAR(255),
    phone_number VARCHAR(15),
    authority INTEGER DEFAULT 2,
    delete_flag INTEGER DEFAULT 0,
    insert_date DATE
);

CREATE TABLE orders (
    id INTEGER PRIMARY KEY,
    postal_code VARCHAR(10),
    address VARCHAR(255),
    name VARCHAR(255),
    phone_number VARCHAR(15),
    pay_method INTEGER,
    user_id INTEGER,
    insert_date DATE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id INTEGER PRIMARY KEY,
    quantity INTEGER,
    price INTEGER,
    order_id INTEGER,
    item_id INTEGER,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE SEQUENCE seq_categories START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE seq_items START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE seq_users START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE seq_orders START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE seq_order_items START WITH 100 INCREMENT BY 1;

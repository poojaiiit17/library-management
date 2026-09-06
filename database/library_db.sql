CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL DEFAULT 'General',
    total_copies INT NOT NULL,
    available_copies INT NOT NULL,
    CONSTRAINT chk_total_copies CHECK (total_copies > 0),
    CONSTRAINT chk_available_copies CHECK (available_copies >= 0 AND available_copies <= total_copies)
);

CREATE TABLE IF NOT EXISTS issued_books (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE NULL,
    CONSTRAINT fk_issue_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_issue_book FOREIGN KEY (book_id) REFERENCES books(book_id)
);

INSERT INTO users (name, email, password, role) VALUES
('Library User', 'user@library.com', 'user123', 'USER'),
('Library Admin', 'admin@library.com', 'admin123', 'ADMIN')
ON DUPLICATE KEY UPDATE name = VALUES(name), role = VALUES(role);

INSERT INTO books (title, author, category, total_copies, available_copies) VALUES
('Clean Code', 'Robert C. Martin', 'Programming', 3, 3),
('The Alchemist', 'Paulo Coelho', 'Fiction', 2, 2),
('Database System Concepts', 'Abraham Silberschatz', 'Database', 2, 2),
('Effective Java', 'Joshua Bloch', 'Programming', 2, 2);

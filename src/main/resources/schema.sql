-- ============================================
-- Library Management System - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Books Table
CREATE TABLE IF NOT EXISTS books (
    book_id      INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    author       VARCHAR(150) NOT NULL,
    genre        VARCHAR(100),
    total_copies INT          NOT NULL DEFAULT 1,
    available_copies INT      NOT NULL DEFAULT 1,
    added_on     DATE         NOT NULL
);

-- Members Table
CREATE TABLE IF NOT EXISTS members (
    member_id   INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    phone       VARCHAR(15),
    joined_on   DATE         NOT NULL
);

-- Issue Records Table
CREATE TABLE IF NOT EXISTS issue_records (
    record_id   INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT  NOT NULL,
    member_id   INT  NOT NULL,
    issue_date  DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE DEFAULT NULL,
    status      ENUM('ISSUED', 'RETURNED') NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (book_id)   REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);

-- Sample Data
INSERT INTO books (title, author, genre, total_copies, available_copies, added_on) VALUES
('Clean Code',               'Robert C. Martin', 'Programming',  3, 3, CURDATE()),
('The Pragmatic Programmer', 'Andrew Hunt',       'Programming',  2, 2, CURDATE()),
('Introduction to Algorithms','Thomas H. Cormen', 'DSA',          2, 2, CURDATE()),
('Database System Concepts',  'Silberschatz',     'Database',     2, 2, CURDATE());

INSERT INTO members (name, email, phone, joined_on) VALUES
('Mohit Morande', 'mohitmorande@gmail.com', '7420911354', CURDATE()),
('Rahul Sharma',  'rahul.sharma@email.com', '9876543210', CURDATE());

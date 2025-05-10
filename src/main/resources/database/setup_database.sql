-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS email_platform;

-- Use the database
USE email_platform;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL
);

-- Create emails table
CREATE TABLE IF NOT EXISTS emails (
    id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    sent_date DATETIME NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    is_spam BOOLEAN DEFAULT FALSE,
    is_snoozed BOOLEAN DEFAULT FALSE,
    snooze_date DATETIME,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create email_labels table
CREATE TABLE IF NOT EXISTS email_labels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    email_id INT,
    label VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (email_id) REFERENCES emails(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_label (user_id, label),
    UNIQUE KEY unique_email_label (email_id, label)
); 
-- Terramail Email Application Database Schema
-- MySQL 8.x

CREATE DATABASE IF NOT EXISTS terramail CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE terramail;

-- Accounts table
CREATE TABLE IF NOT EXISTS ACCOUNT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    imap_host VARCHAR(255) NOT NULL,
    imap_port INT NOT NULL DEFAULT 993,
    imap_user VARCHAR(255) NOT NULL,
    imap_password VARCHAR(500) NOT NULL,
    smtp_host VARCHAR(255) NOT NULL,
    smtp_port INT NOT NULL DEFAULT 587,
    smtp_user VARCHAR(255) NOT NULL,
    smtp_password VARCHAR(500) NOT NULL,
    imap_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    smtp_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    display_name VARCHAR(255),
    organization VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_imap_host (imap_host)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Folders table
CREATE TABLE IF NOT EXISTS FOLDER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    path VARCHAR(500),
    unique_id_prefix VARCHAR(50),
    message_count INT NOT NULL DEFAULT 0,
    is_subscribed BOOLEAN NOT NULL DEFAULT TRUE,
    is_synched BOOLEAN NOT NULL DEFAULT FALSE,
    last_synched TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES ACCOUNT(id) ON DELETE CASCADE,
    UNIQUE KEY uk_account_folder (account_id, name),
    INDEX idx_account (account_id),
    INDEX idx_synched (is_synched)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Messages table
CREATE TABLE IF NOT EXISTS MESSAGE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    folder_id BIGINT,
    message_id VARCHAR(255),
    in_reply_to VARCHAR(255),
    subject VARCHAR(1000),
    body_plain TEXT,
    body_html LONGTEXT,
    received_date TIMESTAMP NULL,
    sent_date TIMESTAMP NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_flagged BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    has_attachments BOOLEAN NOT NULL DEFAULT FALSE,
    attachment_count INT NOT NULL DEFAULT 0,
    size_bytes BIGINT,
    local_received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (folder_id) REFERENCES FOLDER(id) ON DELETE SET NULL,
    INDEX idx_message_id (message_id),
    INDEX idx_folder (folder_id),
    INDEX idx_received_date (received_date),
    INDEX idx_is_read (is_read),
    INDEX idx_is_flagged (is_flagged),
    INDEX idx_is_deleted (is_deleted),
    FULLTEXT idx_subject (subject),
    FULLTEXT idx_body_plain (body_plain)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Recipients table
CREATE TABLE IF NOT EXISTS RECIPIENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    type ENUM('TO', 'CC', 'BCC', 'FROM') NOT NULL DEFAULT 'TO',
    FOREIGN KEY (message_id) REFERENCES MESSAGE(id) ON DELETE CASCADE,
    INDEX idx_message (message_id),
    INDEX idx_email (email),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Attachments table
CREATE TABLE IF NOT EXISTS ATTACHMENT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT,
    filename VARCHAR(500) NOT NULL,
    content_type VARCHAR(255),
    size_bytes BIGINT,
    storage_path VARCHAR(1000),
    charset VARCHAR(50),
    FOREIGN KEY (message_id) REFERENCES MESSAGE(id) ON DELETE CASCADE,
    INDEX idx_message (message_id),
    INDEX idx_filename (filename)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default account for testing (optional)
-- INSERT INTO ACCOUNT (name, email, imap_host, imap_port, imap_user, imap_password, smtp_host, smtp_port, smtp_user, smtp_password)
-- VALUES ('Test Account', 'test@example.com', 'imap.example.com', 993, 'test@example.com', 'password', 'smtp.example.com', 587, 'test@example.com', 'password');

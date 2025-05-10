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
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
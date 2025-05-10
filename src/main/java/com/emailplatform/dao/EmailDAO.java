package com.emailplatform.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.emailplatform.model.Email;
import com.emailplatform.model.User;
import com.emailplatform.util.DatabaseConnection;

public class EmailDAO {

    public boolean sendEmail(int senderId, String recipientEmail, String subject, String body) throws SQLException {
        String query = "INSERT INTO emails (sender_id, recipient_email, subject, body, sent_date) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, senderId);
            stmt.setString(2, recipientEmail);
            stmt.setString(3, subject);
            stmt.setString(4, body);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Email> getInboxEmails(String userEmail) throws SQLException {
        String query = "SELECT e.* FROM emails e " +
                "JOIN users u ON e.recipient_email = u.email " +
                "WHERE u.email = ? AND e.is_deleted = false AND e.is_spam = false " +
                "ORDER BY e.sent_date DESC";

        List<Email> emails = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(extractEmailFromResultSet(rs));
                }
            }
        }
        return emails;
    }

    public List<Email> getSentEmails(String userEmail) throws SQLException {
        String query = "SELECT e.* FROM emails e " +
                "JOIN users u ON e.sender_id = u.id " +
                "WHERE u.email = ? AND e.is_deleted = false " +
                "ORDER BY e.sent_date DESC";

        List<Email> emails = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(extractEmailFromResultSet(rs));
                }
            }
        }
        return emails;
    }

    public List<Email> getTrashEmails(String userEmail) throws SQLException {
        String query = "SELECT e.* FROM emails e " +
                "JOIN users u ON e.recipient_email = u.email " +
                "WHERE u.email = ? AND e.is_deleted = true " +
                "ORDER BY e.sent_date DESC";

        List<Email> emails = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(extractEmailFromResultSet(rs));
                }
            }
        }
        return emails;
    }

    public List<Email> getSpamEmails(String userEmail) throws SQLException {
        String query = "SELECT e.* FROM emails e " +
                "JOIN users u ON e.recipient_email = u.email " +
                "WHERE u.email = ? AND e.is_spam = true AND e.is_deleted = false " +
                "ORDER BY e.sent_date DESC";

        List<Email> emails = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(extractEmailFromResultSet(rs));
                }
            }
        }
        return emails;
    }

    public List<Email> getSnoozedEmails(String userEmail) throws SQLException {
        String query = "SELECT e.* FROM emails e " +
                "JOIN users u ON e.recipient_email = u.email " +
                "WHERE u.email = ? AND e.is_snoozed = true AND e.is_deleted = false " +
                "ORDER BY e.snooze_date ASC";

        List<Email> emails = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(extractEmailFromResultSet(rs));
                }
            }
        }
        return emails;
    }

    public boolean markAsRead(int emailId) throws SQLException {
        String query = "UPDATE emails SET is_read = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, emailId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean moveToTrash(int emailId) throws SQLException {
        String query = "UPDATE emails SET is_deleted = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, emailId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean markAsSpam(int emailId) throws SQLException {
        String query = "UPDATE emails SET is_spam = true WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, emailId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean snoozeEmail(int emailId, Date snoozeDate) throws SQLException {
        String query = "UPDATE emails SET is_snoozed = true, snooze_date = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, new java.sql.Date(snoozeDate.getTime()));
            stmt.setInt(2, emailId);
            return stmt.executeUpdate() > 0;
        }
    }

    public User getEmailSender(int emailId) throws SQLException {
        String query = "SELECT u.* FROM users u " +
                "JOIN emails e ON u.id = e.sender_id " +
                "WHERE e.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, emailId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("full_name"));
                }
            }
        }
        return null;
    }

    public boolean addLabel(String userEmail, String label) throws SQLException {
        String query = "INSERT INTO email_labels (user_id, label) " +
                "SELECT id, ? FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, label);
            stmt.setString(2, userEmail);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<String> getLabels(String userEmail) throws SQLException {
        String query = "SELECT DISTINCT el.label FROM email_labels el " +
                "JOIN users u ON el.user_id = u.id " +
                "WHERE u.email = ?";

        List<String> labels = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    labels.add(rs.getString("label"));
                }
            }
        }
        return labels;
    }

    public boolean deleteLabel(String label) throws SQLException {
        String query = "DELETE FROM email_labels WHERE label = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, label);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean addLabelToEmail(int emailId, String label) throws SQLException {
        String query = "INSERT INTO email_labels (email_id, label) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, emailId);
            stmt.setString(2, label);

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeLabelFromEmail(int emailId, String label) throws SQLException {
        String query = "DELETE FROM email_labels WHERE email_id = ? AND label = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, emailId);
            stmt.setString(2, label);

            return stmt.executeUpdate() > 0;
        }
    }

    private Email extractEmailFromResultSet(ResultSet rs) throws SQLException {
        return new Email(
                rs.getInt("id"),
                rs.getInt("sender_id"),
                rs.getString("recipient_email"),
                rs.getString("subject"),
                rs.getString("body"),
                rs.getTimestamp("sent_date"),
                rs.getBoolean("is_read"),
                rs.getBoolean("is_deleted"),
                rs.getBoolean("is_spam"),
                rs.getBoolean("is_snoozed"),
                rs.getTimestamp("snooze_date"));
    }
}
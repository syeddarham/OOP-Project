package com.emailplatform.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.emailplatform.model.Email;
import com.emailplatform.model.User;
import com.emailplatform.util.DatabaseConnection;

public class EmailDAO {

    // In-memory label storage: userId -> set of labels
    private static Map<Integer, Set<String>> userLabels = new HashMap<>();
    // In-memory label assignments: emailId -> set of labels
    private static Map<Integer, Set<String>> emailLabels = new HashMap<>();

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
        String sql = "SELECT * FROM emails WHERE recipient_email = ? AND is_snoozed = true AND snoozed_until > NOW()";
        List<Email> emails = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                emails.add(extractEmailFromResultSet(rs));
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
        String sql = "UPDATE emails SET is_spam = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, emailId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean snoozeEmail(int emailId) throws SQLException {
        String sql = "UPDATE emails SET is_snoozed = true WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, emailId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean unsnoozeEmail(int emailId) throws SQLException {
        String sql = "UPDATE emails SET snoozed_until = NULL, is_snoozed = false WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, emailId);
            return pstmt.executeUpdate() > 0;
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

    // Add a label for a user
    public boolean addLabel(int userId, String label) {
        userLabels.putIfAbsent(userId, new HashSet<>());
        return userLabels.get(userId).add(label);
    }

    // Get all labels for a user
    public List<String> getLabels(int userId) {
        Set<String> labels = userLabels.getOrDefault(userId, new HashSet<>());
        return new ArrayList<>(labels);
    }

    // Delete a label for a user (and from all emails)
    public boolean deleteLabel(int userId, String label) {
        boolean removed = false;
        if (userLabels.containsKey(userId)) {
            removed = userLabels.get(userId).remove(label);
        }
        // Remove from all emails
        for (Set<String> labels : emailLabels.values()) {
            labels.remove(label);
        }
        return removed;
    }

    // Assign a label to an email for a user
    public boolean addLabelToEmail(int userId, int emailId, String label) {
        if (!userLabels.containsKey(userId) || !userLabels.get(userId).contains(label)) {
            return false; // Label must exist for user
        }
        emailLabels.putIfAbsent(emailId, new HashSet<>());
        return emailLabels.get(emailId).add(label);
    }

    // Remove a label from an email
    public boolean removeLabelFromEmail(int emailId, String label) {
        if (!emailLabels.containsKey(emailId))
            return false;
        return emailLabels.get(emailId).remove(label);
    }

    // Check if a label is already assigned to an email for a user
    public boolean isLabelAssignedToEmail(int userId, int emailId, String label) {
        return emailLabels.containsKey(emailId) && emailLabels.get(emailId).contains(label);
    }

    public boolean restoreEmail(int emailId) throws SQLException {
        String query = "UPDATE emails SET is_deleted = false, is_spam = false WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, emailId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteForever(int emailId) throws SQLException {
        String query = "DELETE FROM emails WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, emailId);
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
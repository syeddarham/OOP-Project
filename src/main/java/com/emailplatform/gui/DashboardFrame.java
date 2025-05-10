package com.emailplatform.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.emailplatform.dao.EmailDAO;
import com.emailplatform.dao.UserDAO;
import com.emailplatform.model.Email;
import com.emailplatform.model.User;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private EmailDAO emailDAO;
    private UserDAO userDAO;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Map<String, JButton> navButtons = new HashMap<>();
    private String[] sections = { "Compose", "Inbox", "Snoozed", "Sent", "Trash", "Spam", "Labels", "Logout" };

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.emailDAO = new EmailDAO();
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Email Platform - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(createSectionPanel("Compose"), "Compose");
        contentPanel.add(createSectionPanel("Inbox"), "Inbox");
        contentPanel.add(createSectionPanel("Snoozed"), "Snoozed");
        contentPanel.add(createSectionPanel("Sent"), "Sent");
        contentPanel.add(createSectionPanel("Trash"), "Trash");
        contentPanel.add(createSectionPanel("Spam"), "Spam");
        contentPanel.add(createSectionPanel("Labels"), "Labels");
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "Inbox");
        highlightNavButton("Inbox");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(45, 45, 45));

        // Navigation buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(45, 45, 45));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        for (String section : sections) {
            JButton button = new JButton(section);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 35));
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(60, 60, 60));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addActionListener(e -> {
                if (section.equals("Logout")) {
                    handleLogout();
                } else {
                    cardLayout.show(contentPanel, section);
                    highlightNavButton(section);
                }
            });
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!button.getBackground().equals(new Color(30, 144, 255)))
                        button.setBackground(new Color(80, 80, 80));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!button.getBackground().equals(new Color(30, 144, 255)))
                        button.setBackground(new Color(60, 60, 60));
                }
            });
            navButtons.put(section, button);
            navPanel.add(button);
            navPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        // User info at the bottom
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(45, 45, 45));
        userPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));
        userPanel.add(Box.createVerticalGlue());
        JLabel nameLabel = new JLabel(currentUser.getFullName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel emailLabel = new JLabel(currentUser.getEmail());
        emailLabel.setForeground(Color.LIGHT_GRAY);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userPanel.add(nameLabel);
        userPanel.add(emailLabel);

        sidebar.add(navPanel, BorderLayout.NORTH);
        sidebar.add(userPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    private void highlightNavButton(String section) {
        for (String sec : navButtons.keySet()) {
            JButton btn = navButtons.get(sec);
            if (sec.equals(section)) {
                btn.setBackground(new Color(30, 144, 255)); // Highlight color
            } else {
                btn.setBackground(new Color(60, 60, 60));
            }
        }
    }

    private JPanel createSectionPanel(String section) {
        switch (section) {
            case "Compose":
                return createComposePanel();
            case "Inbox":
                return createInboxPanel();
            case "Snoozed":
                return createSnoozedPanel();
            case "Sent":
                return createSentPanel();
            case "Trash":
                return createTrashPanel();
            case "Spam":
                return createSpamPanel();
            case "Labels":
                return createLabelsPanel();
            default:
                JPanel panel = new JPanel();
                panel.setBackground(Color.WHITE);
                panel.setLayout(new BorderLayout());
                JLabel label = new JLabel(section);
                label.setFont(new Font("Segoe UI", Font.BOLD, 24));
                label.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));
                panel.add(label, BorderLayout.NORTH);
                return panel;
        }
    }

    private JPanel createComposePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        JLabel toLabel = new JLabel("To:");
        JTextField toField = new JTextField(30);
        toField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formPanel.add(toLabel);
        formPanel.add(toField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subjectLabel = new JLabel("Subject:");
        JTextField subjectField = new JTextField(30);
        subjectField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formPanel.add(subjectLabel);
        formPanel.add(subjectField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel bodyLabel = new JLabel("Message:");
        JTextArea bodyArea = new JTextArea(10, 30);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        formPanel.add(bodyLabel);
        formPanel.add(bodyScroll);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton sendButton = new JButton("Send");
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendButton.addActionListener(e -> {
            String recipient = toField.getText().trim();
            String subject = subjectField.getText().trim();
            String body = bodyArea.getText().trim();
            if (recipient.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                boolean sent = emailDAO.sendEmail(currentUser.getId(), recipient, subject, body);
                if (sent) {
                    JOptionPane.showMessageDialog(this, "Email sent successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    toField.setText("");
                    subjectField.setText("");
                    bodyArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to send email.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(sendButton);

        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    // INBOX PANEL
    private JPanel createInboxPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        refreshInbox(emailListPanel);
        return panel;
    }

    private void refreshInbox(JPanel emailListPanel) {
        emailListPanel.removeAll();
        try {
            List<Email> emails = emailDAO.getInboxEmails(currentUser.getEmail());
            for (Email email : emails) {
                emailListPanel.add(createEmailPanel(email));
                emailListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading emails: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        emailListPanel.revalidate();
        emailListPanel.repaint();
    }

    private JPanel createEmailPanel(Email email) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        JLabel subjectLabel = new JLabel(email.getSubject());
        subjectLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(subjectLabel, BorderLayout.WEST);
        JLabel dateLabel = new JLabel(email.getSentDate().toString());
        panel.add(dateLabel, BorderLayout.EAST);
        JLabel previewLabel = new JLabel(email.getBody().substring(0, Math.min(100, email.getBody().length())) + "...");
        panel.add(previewLabel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewButton = new JButton("View");
        JButton deleteButton = new JButton("Delete");
        viewButton.addActionListener(e -> viewEmail(email));
        deleteButton.addActionListener(e -> deleteEmail(email));
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void viewEmail(Email email) {
        try {
            emailDAO.markAsRead(email.getId());
            User sender = emailDAO.getEmailSender(email.getId());
            if (sender == null) {
                JOptionPane.showMessageDialog(this, "Could not find sender information", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            JDialog dialog = new JDialog(this, "View Email", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JPanel headerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            headerPanel.add(new JLabel("From: " + sender.getFullName() + " <" + sender.getEmail() + ">"));
            headerPanel.add(new JLabel("To: " + email.getRecipientEmail()));
            headerPanel.add(new JLabel("Subject: " + email.getSubject()));
            headerPanel.add(new JLabel("Date: " + email.getSentDate()));
            panel.add(headerPanel, BorderLayout.NORTH);
            JTextArea bodyArea = new JTextArea(email.getBody());
            bodyArea.setEditable(false);
            bodyArea.setLineWrap(true);
            bodyArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(bodyArea);
            panel.add(scrollPane, BorderLayout.CENTER);
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error viewing email: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmail(Email email) {
        try {
            if (emailDAO.moveToTrash(email.getId())) {
                refreshInbox((JPanel) contentPanel.getComponent(1));
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete email", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting email: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // SNOOZED PANEL
    private JPanel createSnoozedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        refreshSnoozedEmails(emailListPanel);
        return panel;
    }

    private void refreshSnoozedEmails(JPanel emailListPanel) {
        emailListPanel.removeAll();
        try {
            List<Email> emails = emailDAO.getSnoozedEmails(currentUser.getEmail());
            for (Email email : emails) {
                emailListPanel.add(createEmailPanel(email));
                emailListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading snoozed emails: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        emailListPanel.revalidate();
        emailListPanel.repaint();
    }

    // SENT PANEL
    private JPanel createSentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        refreshSentEmails(emailListPanel);
        return panel;
    }

    private void refreshSentEmails(JPanel emailListPanel) {
        emailListPanel.removeAll();
        try {
            List<Email> emails = emailDAO.getSentEmails(currentUser.getEmail());
            for (Email email : emails) {
                emailListPanel.add(createSentEmailPanel(email));
                emailListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading sent emails: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        emailListPanel.revalidate();
        emailListPanel.repaint();
    }

    private JPanel createSentEmailPanel(Email email) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        JLabel subjectLabel = new JLabel(email.getSubject());
        subjectLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(subjectLabel, BorderLayout.WEST);
        JLabel dateLabel = new JLabel(email.getSentDate().toString());
        panel.add(dateLabel, BorderLayout.EAST);
        JLabel previewLabel = new JLabel(email.getBody().substring(0, Math.min(100, email.getBody().length())) + "...");
        panel.add(previewLabel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewButton = new JButton("View");
        viewButton.addActionListener(e -> viewSentEmail(email));
        buttonPanel.add(viewButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void viewSentEmail(Email email) {
        try {
            JDialog dialog = new JDialog(this, "View Sent Email", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JPanel headerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
            headerPanel.add(new JLabel("From: " + currentUser.getFullName() + " <" + currentUser.getEmail() + ">"));
            headerPanel.add(new JLabel("To: " + email.getRecipientEmail()));
            headerPanel.add(new JLabel("Subject: " + email.getSubject()));
            headerPanel.add(new JLabel("Date: " + email.getSentDate()));
            panel.add(headerPanel, BorderLayout.NORTH);
            JTextArea bodyArea = new JTextArea(email.getBody());
            bodyArea.setEditable(false);
            bodyArea.setLineWrap(true);
            bodyArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(bodyArea);
            panel.add(scrollPane, BorderLayout.CENTER);
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error viewing sent email: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // TRASH PANEL
    private JPanel createTrashPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        refreshTrashEmails(emailListPanel);
        return panel;
    }

    private void refreshTrashEmails(JPanel emailListPanel) {
        emailListPanel.removeAll();
        try {
            List<Email> emails = emailDAO.getTrashEmails(currentUser.getEmail());
            for (Email email : emails) {
                emailListPanel.add(createEmailPanel(email));
                emailListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading trash emails: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        emailListPanel.revalidate();
        emailListPanel.repaint();
    }

    // SPAM PANEL
    private JPanel createSpamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        panel.add(scrollPane, BorderLayout.CENTER);
        refreshSpamEmails(emailListPanel);
        return panel;
    }

    private void refreshSpamEmails(JPanel emailListPanel) {
        emailListPanel.removeAll();
        try {
            List<Email> emails = emailDAO.getSpamEmails(currentUser.getEmail());
            for (Email email : emails) {
                emailListPanel.add(createEmailPanel(email));
                emailListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading spam emails: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        emailListPanel.revalidate();
        emailListPanel.repaint();
    }

    // LABELS PANEL
    private JPanel createLabelsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("Labels");
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));
        panel.add(label, BorderLayout.NORTH);
        // TODO: Add label management UI and logic
        return panel;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
                this.dispose();
            });
        }
    }
}
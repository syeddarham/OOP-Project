package com.emailplatform.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
import javax.swing.JViewport;
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
    private String[] sections = { "Compose", "Inbox", "Sent", "Trash", "Spam", "Labels", "GetLogs", "Logout" };

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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(createSectionPanel("Compose"), "Compose");
        contentPanel.add(createSectionPanel("Inbox"), "Inbox");
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
                } else if (section.equals("GetLogs")) {
                    handleGetLogs();
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
                btn.setBackground(new Color(30, 144, 255));
            } else {
                btn.setBackground(new Color(60, 60, 60));
            }
        }
    }

    private JPanel createSectionPanel(String section) {
        JPanel panel;
        switch (section) {
            case "Compose":
                panel = createComposePanel();
                break;
            case "Inbox":
                panel = createInboxPanel();
                break;
            case "Sent":
                panel = createSentPanel();
                break;
            case "Trash":
                panel = createTrashPanel();
                break;
            case "Spam":
                panel = createSpamPanel();
                break;
            case "Labels":
                panel = createLabelsPanel();
                break;
            default:
                panel = new JPanel();
                panel.setBackground(Color.WHITE);
                panel.setLayout(new BorderLayout());
                JLabel label = new JLabel(section);
                label.setFont(new Font("Segoe UI", Font.BOLD, 24));
                label.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));
                panel.add(label, BorderLayout.NORTH);
        }
        panel.setName(section); 
        return panel;
    }
    private JPanel createPanelHeader(String title, String panelName) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 120, 255));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(33, 120, 255));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshPanel(panelName));
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(32, 0, 24, 32));
        return headerPanel;
    }

    private JPanel createInboxPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(33, 120, 255));
        accentBar.setPreferredSize(new Dimension(8, 1));
        GridBagConstraints accentGbc = new GridBagConstraints();
        accentGbc.gridx = 0;
        accentGbc.gridy = 0;
        accentGbc.gridheight = 5;
        accentGbc.fill = GridBagConstraints.VERTICAL;
        accentGbc.insets = new Insets(0, 0, 0, 24);
        card.add(accentBar, accentGbc);

        JPanel headerPanel = createPanelHeader("Inbox", "Inbox");
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 1;
        headerGbc.gridy = 0;
        headerGbc.gridwidth = 2;
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(headerPanel, headerGbc);

        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        emailListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        GridBagConstraints listGbc = new GridBagConstraints();
        listGbc.gridx = 1;
        listGbc.gridy = 1;
        listGbc.gridwidth = 2;
        listGbc.weightx = 1.0;
        listGbc.weighty = 1.0;
        listGbc.insets = new Insets(0, 0, 24, 32);
        listGbc.fill = GridBagConstraints.BOTH;
        card.add(scrollPane, listGbc);

        try {
            List<Email> emails = emailDAO.getInboxEmails(currentUser.getEmail());
            for (Email email : emails) {
                JPanel row = createModernEmailRow(email, "inbox");
                emailListPanel.add(row);
            }
        } catch (Exception ex) {
            emailListPanel.add(new JLabel("Failed to load emails."));
        }
        outerPanel.add(card, gbc);
        return outerPanel;
    }

    private JPanel createTrashPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(33, 120, 255));
        accentBar.setPreferredSize(new Dimension(8, 1));
        GridBagConstraints accentGbc = new GridBagConstraints();
        accentGbc.gridx = 0;
        accentGbc.gridy = 0;
        accentGbc.gridheight = 5;
        accentGbc.fill = GridBagConstraints.VERTICAL;
        accentGbc.insets = new Insets(0, 0, 0, 24);
        card.add(accentBar, accentGbc);

        JPanel headerPanel = createPanelHeader("Trash", "Trash");
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 1;
        headerGbc.gridy = 0;
        headerGbc.gridwidth = 2;
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(headerPanel, headerGbc);

        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        emailListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        GridBagConstraints listGbc = new GridBagConstraints();
        listGbc.gridx = 1;
        listGbc.gridy = 1;
        listGbc.gridwidth = 2;
        listGbc.weightx = 1.0;
        listGbc.weighty = 1.0;
        listGbc.insets = new Insets(0, 0, 24, 32);
        listGbc.fill = GridBagConstraints.BOTH;
        card.add(scrollPane, listGbc);

        try {
            List<Email> emails = emailDAO.getTrashEmails(currentUser.getEmail());
            for (Email email : emails) {
                JPanel row = createModernEmailRow(email, "trash");
                emailListPanel.add(row);
            }
        } catch (Exception ex) {
            emailListPanel.add(new JLabel("Failed to load emails."));
        }
        outerPanel.add(card, gbc);
        return outerPanel;
    }

    private JPanel createSentPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(33, 120, 255));
        accentBar.setPreferredSize(new Dimension(8, 1));
        GridBagConstraints accentGbc = new GridBagConstraints();
        accentGbc.gridx = 0;
        accentGbc.gridy = 0;
        accentGbc.gridheight = 5;
        accentGbc.fill = GridBagConstraints.VERTICAL;
        accentGbc.insets = new Insets(0, 0, 0, 24);
        card.add(accentBar, accentGbc);

        JPanel headerPanel = createPanelHeader("Sent", "Sent");
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 1;
        headerGbc.gridy = 0;
        headerGbc.gridwidth = 2;
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(headerPanel, headerGbc);

        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        emailListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        GridBagConstraints listGbc = new GridBagConstraints();
        listGbc.gridx = 1;
        listGbc.gridy = 1;
        listGbc.gridwidth = 2;
        listGbc.weightx = 1.0;
        listGbc.weighty = 1.0;
        listGbc.insets = new Insets(0, 0, 24, 32);
        listGbc.fill = GridBagConstraints.BOTH;
        card.add(scrollPane, listGbc);

        try {
            List<Email> emails = emailDAO.getSentEmails(currentUser.getEmail());
            for (Email email : emails) {
                JPanel row = createModernEmailRow(email, "sent");
                emailListPanel.add(row);
            }
        } catch (Exception ex) {
            emailListPanel.add(new JLabel("Failed to load emails."));
        }
        outerPanel.add(card, gbc);
        return outerPanel;
    }

    private JPanel createSpamPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(33, 120, 255));
        accentBar.setPreferredSize(new Dimension(8, 1));
        GridBagConstraints accentGbc = new GridBagConstraints();
        accentGbc.gridx = 0;
        accentGbc.gridy = 0;
        accentGbc.gridheight = 5;
        accentGbc.fill = GridBagConstraints.VERTICAL;
        accentGbc.insets = new Insets(0, 0, 0, 24);
        card.add(accentBar, accentGbc);

        JPanel headerPanel = createPanelHeader("Spam", "Spam");
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 1;
        headerGbc.gridy = 0;
        headerGbc.gridwidth = 2;
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(headerPanel, headerGbc);

        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        emailListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        GridBagConstraints listGbc = new GridBagConstraints();
        listGbc.gridx = 1;
        listGbc.gridy = 1;
        listGbc.gridwidth = 2;
        listGbc.weightx = 1.0;
        listGbc.weighty = 1.0;
        listGbc.insets = new Insets(0, 0, 24, 32);
        listGbc.fill = GridBagConstraints.BOTH;
        card.add(scrollPane, listGbc);

        try {
            List<Email> emails = emailDAO.getSpamEmails(currentUser.getEmail());
            for (Email email : emails) {
                JPanel row = createModernEmailRow(email, "spam");
                emailListPanel.add(row);
            }
        } catch (Exception ex) {
            emailListPanel.add(new JLabel("Failed to load emails."));
        }
        outerPanel.add(card, gbc);
        return outerPanel;
    }

    private JPanel createLabelsPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(33, 120, 255));
        accentBar.setPreferredSize(new Dimension(8, 1));
        GridBagConstraints accentGbc = new GridBagConstraints();
        accentGbc.gridx = 0;
        accentGbc.gridy = 0;
        accentGbc.gridheight = 5;
        accentGbc.fill = GridBagConstraints.VERTICAL;
        accentGbc.insets = new Insets(0, 0, 0, 24);
        card.add(accentBar, accentGbc);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Labels");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 120, 255));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel createLabelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        createLabelPanel.setOpaque(false);
        JTextField labelField = new JTextField(12);
        JButton createLabelBtn = new JButton("Create Label");
        createLabelBtn.setBackground(new Color(33, 120, 255));
        createLabelBtn.setForeground(Color.WHITE);
        createLabelBtn.setFocusPainted(false);
        createLabelBtn.addActionListener(e -> {
            String label = labelField.getText().trim();
            if (!label.isEmpty()) {
                try {
                    if (emailDAO.addLabel(currentUser.getId(), label)) {
                        JOptionPane.showMessageDialog(this, "Label created!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        labelField.setText("");
                        refreshPanel("Labels");
                    } else {
                        JOptionPane.showMessageDialog(this, "Label already exists or failed to create.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        createLabelPanel.add(labelField);
        createLabelPanel.add(createLabelBtn);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(33, 120, 255));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshPanel("Labels"));
        createLabelPanel.add(refreshBtn);
        headerPanel.add(createLabelPanel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(32, 0, 24, 32));
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 1;
        headerGbc.gridy = 0;
        headerGbc.gridwidth = 2;
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(headerPanel, headerGbc);

        JPanel labelListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        labelListPanel.setOpaque(false);
        try {
            List<String> labels = emailDAO.getLabels(currentUser.getId());
            for (String label : labels) {
                JButton labelBtn = new JButton(label);
                labelBtn.setBackground(new Color(230, 240, 255));
                labelBtn.setForeground(new Color(33, 120, 255));
                labelBtn.setFocusPainted(false);
                labelBtn.addActionListener(e -> showEmailsForLabel(label));
                labelListPanel.add(labelBtn);
            }
        } catch (Exception ex) {
            labelListPanel.add(new JLabel("Failed to load labels."));
        }
        GridBagConstraints labelListGbc = new GridBagConstraints();
        labelListGbc.gridx = 1;
        labelListGbc.gridy = 1;
        labelListGbc.gridwidth = 2;
        labelListGbc.insets = new Insets(0, 0, 8, 32);
        labelListGbc.anchor = GridBagConstraints.WEST;
        card.add(labelListPanel, labelListGbc);

        JPanel emailListPanel = new JPanel();
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));
        emailListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        GridBagConstraints listGbc = new GridBagConstraints();
        listGbc.gridx = 1;
        listGbc.gridy = 2;
        listGbc.gridwidth = 2;
        listGbc.weightx = 1.0;
        listGbc.weighty = 1.0;
        listGbc.insets = new Insets(0, 0, 24, 32);
        listGbc.fill = GridBagConstraints.BOTH;
        card.add(scrollPane, listGbc);

        try {
            List<Email> emails = emailDAO.getInboxEmails(currentUser.getEmail());
            for (Email email : emails) {
                JPanel row = createModernEmailRowWithLabel(email);
                emailListPanel.add(row);
            }
        } catch (Exception ex) {
            emailListPanel.add(new JLabel("Failed to load emails."));
        }
        outerPanel.add(card, gbc);
        return outerPanel;
    }

    private JPanel createModernEmailRow(Email email, String panelType) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                row.setBackground(new Color(245, 250, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                row.setBackground(Color.WHITE);
            }

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewEmail(email);
            }
        });

        JPanel senderPanel = new JPanel(new BorderLayout(8, 0));
        senderPanel.setOpaque(false);

        String senderEmail = "Unknown";
        try {
            String senderEmailAddress;
            if (panelType.equals("sent")) {
                senderEmailAddress = currentUser.getEmail(); 
            } else {
                senderEmailAddress = email.getRecipientEmail();
            }

            User sender = userDAO.getUserByEmail(senderEmailAddress);
            if (sender != null) {
                senderEmail = sender.getFullName() + " <" + sender.getEmail() + ">";
            } else {
                senderEmail = senderEmailAddress; 
            }
        } catch (Exception ex) {
            senderEmail = "Unknown Sender";
        }

        JLabel senderLabel = new JLabel(senderEmail);
        senderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        senderLabel.setForeground(new Color(33, 33, 33));
        senderPanel.add(senderLabel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(8, 4));
        contentPanel.setOpaque(false);

        JLabel subjectLabel = new JLabel(email.getSubject());
        subjectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subjectLabel.setForeground(new Color(33, 33, 33));
        contentPanel.add(subjectLabel, BorderLayout.NORTH);

        String preview = email.getBody().replaceAll("\n", " ").trim();
        if (preview.length() > 60) {
            preview = preview.substring(0, 60) + "...";
        }
        JLabel previewLabel = new JLabel(preview);
        previewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        previewLabel.setForeground(new Color(100, 100, 100));
        contentPanel.add(previewLabel, BorderLayout.CENTER);

        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setOpaque(false);
        JLabel dateLabel = new JLabel(email.getSentDate().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(120, 120, 120));
        datePanel.add(dateLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
        Dimension buttonSize = new Dimension(100, 28);

        if (panelType.equals("inbox")) {
            JButton spamBtn = createStyledButton("Spam", new Color(255, 193, 7), buttonFont, buttonSize);
            spamBtn.addActionListener(e -> markAsSpam(email));
            buttonPanel.add(spamBtn);

            JButton deleteBtn = createStyledButton("Delete", new Color(220, 53, 69), buttonFont, buttonSize);
            deleteBtn.addActionListener(e -> deleteEmail(email));
            buttonPanel.add(deleteBtn);
        } else if (panelType.equals("trash")) {
            JButton restoreBtn = createStyledButton("Restore", new Color(40, 167, 69), buttonFont, buttonSize);
            restoreBtn.addActionListener(e -> restoreEmail(email));
            buttonPanel.add(restoreBtn);

            JButton deleteForeverBtn = createStyledButton("Delete", new Color(220, 53, 69), buttonFont,
                    buttonSize);
            deleteForeverBtn.addActionListener(e -> deleteForeverEmail(email));
            buttonPanel.add(deleteForeverBtn);
        } else if (panelType.equals("sent")) {
            JButton deleteBtn = createStyledButton("Delete", new Color(220, 53, 69), buttonFont, buttonSize);
            deleteBtn.addActionListener(e -> deleteEmail(email));
            buttonPanel.add(deleteBtn);
        } else if (panelType.equals("spam")) {
            JButton notSpamBtn = createStyledButton("Not Spam", new Color(40, 167, 69), buttonFont, buttonSize);
            notSpamBtn.addActionListener(e -> restoreEmail(email));
            buttonPanel.add(notSpamBtn);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 16);
        row.add(senderPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 0, 0, 16);
        row.add(contentPanel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(0, 0, 0, 16);
        row.add(datePanel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.1;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(buttonPanel, gbc);

        return row;
    }

    private JButton createStyledButton(String text, Color bgColor, Font font, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void restoreEmail(Email email) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to restore this email?",
                "Restore Email",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (emailDAO.restoreEmail(email.getId())) {
                    JOptionPane.showMessageDialog(this, "Email restored successfully", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    if (email.isSpam()) {
                        refreshPanel("Spam");
                    } else {
                        refreshPanel("Trash");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to restore email.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteForeverEmail(Email email) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete this email? This action cannot be undone.",
                "Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (emailDAO.deleteForever(email.getId())) {
                    JOptionPane.showMessageDialog(this, "Email permanently deleted.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshPanel("Trash");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete email.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
                JOptionPane.showMessageDialog(this, "Email moved to trash successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshPanel("Inbox"); 
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete email", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting email: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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

    private void refreshPanel(String panelName) {
        if (panelName.equals("Compose")) {
            return;
        }

        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(panelName)) {
                contentPanel.remove(comp);
                break;
            }
        }

        JPanel newPanel = createSectionPanel(panelName);
        newPanel.setName(panelName); 
        contentPanel.add(newPanel, panelName);

        cardLayout.show(contentPanel, panelName);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createComposePanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(new Color(33, 120, 255));
        accentBar.setPreferredSize(new Dimension(8, 1));
        GridBagConstraints accentGbc = new GridBagConstraints();
        accentGbc.gridx = 0;
        accentGbc.gridy = 0;
        accentGbc.gridheight = 5;
        accentGbc.fill = GridBagConstraints.VERTICAL;
        accentGbc.insets = new Insets(0, 0, 0, 24);
        card.add(accentBar, accentGbc);

        JLabel titleLabel = new JLabel("Compose Email");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 120, 255));
        GridBagConstraints titleGbc = new GridBagConstraints();
        titleGbc.gridx = 1;
        titleGbc.gridy = 0;
        titleGbc.gridwidth = 2;
        titleGbc.insets = new Insets(32, 0, 24, 32);
        titleGbc.anchor = GridBagConstraints.WEST;
        card.add(titleLabel, titleGbc);

        JTextField toField = new JTextField();
        toField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        toField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "To", 0, 0, new Font("Segoe UI", Font.PLAIN, 14), new Color(120, 120, 120)));
        GridBagConstraints toGbc = new GridBagConstraints();
        toGbc.gridx = 1;
        toGbc.gridy = 1;
        toGbc.weightx = 0.5;
        toGbc.insets = new Insets(0, 0, 16, 8);
        toGbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(toField, toGbc);

        JTextField subjectField = new JTextField();
        subjectField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subjectField
                .setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        "Subject", 0, 0, new Font("Segoe UI", Font.PLAIN, 14), new Color(120, 120, 120)));
        GridBagConstraints subjectGbc = new GridBagConstraints();
        subjectGbc.gridx = 2;
        subjectGbc.gridy = 1;
        subjectGbc.weightx = 0.5;
        subjectGbc.insets = new Insets(0, 8, 16, 32);
        subjectGbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(subjectField, subjectGbc);

        JTextArea bodyArea = new JTextArea();
        bodyArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Message", 0, 0, new Font("Segoe UI", Font.PLAIN, 14), new Color(120, 120, 120)));
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        GridBagConstraints bodyGbc = new GridBagConstraints();
        bodyGbc.gridx = 1;
        bodyGbc.gridy = 2;
        bodyGbc.gridwidth = 2;
        bodyGbc.weightx = 1.0;
        bodyGbc.weighty = 1.0;
        bodyGbc.insets = new Insets(0, 0, 16, 32);
        bodyGbc.fill = GridBagConstraints.BOTH;
        card.add(bodyScroll, bodyGbc);

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sendButton.setBackground(new Color(33, 120, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(new Color(25, 100, 220));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(new Color(33, 120, 255));
            }
        });
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
        GridBagConstraints sendBtnGbc = new GridBagConstraints();
        sendBtnGbc.gridx = 2;
        sendBtnGbc.gridy = 3;
        sendBtnGbc.anchor = GridBagConstraints.SOUTHEAST;
        sendBtnGbc.insets = new Insets(0, 8, 24, 32);
        card.add(sendButton, sendBtnGbc);

        outerPanel.add(card, gbc);
        return outerPanel;
    }

    private void markAsSpam(Email email) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to mark this email as spam?",
                "Mark as Spam",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (emailDAO.markAsSpam(email.getId())) {
                    JOptionPane.showMessageDialog(this,
                            "Email marked as spam successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshPanel("Inbox");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to mark email as spam",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshLabelEmails(String label) {
        refreshPanel("Labels");
    }

    private JPanel createModernEmailRowWithLabel(Email email) {
        JPanel row = createModernEmailRow(email, "labels");
        JButton assignLabelBtn = new JButton("Assign Label");
        assignLabelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        assignLabelBtn.setBackground(new Color(33, 120, 255));
        assignLabelBtn.setForeground(Color.WHITE);
        assignLabelBtn.setFocusPainted(false);
        assignLabelBtn.addActionListener(e -> {
            try {
                List<String> labels = emailDAO.getLabels(currentUser.getId());
                String label = (String) JOptionPane.showInputDialog(this, "Select label:", "Assign Label",
                        JOptionPane.PLAIN_MESSAGE, null, labels.toArray(), labels.isEmpty() ? null : labels.get(0));
                if (label != null) {
                    if (emailDAO.isLabelAssignedToEmail(currentUser.getId(), email.getId(), label)) {
                        JOptionPane.showMessageDialog(this, "Label already assigned to this email.", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (emailDAO.addLabelToEmail(currentUser.getId(), email.getId(), label)) {
                        JOptionPane.showMessageDialog(this, "Label assigned!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        refreshPanel("Labels");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to assign label.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        row.add(assignLabelBtn);
        return row;
    }

    private void handleGetLogs() {
        try {
            String fileName = "user_logs_" + currentUser.getEmail().replace("@", "_at_") + ".txt";
            java.io.FileWriter writer = new java.io.FileWriter(fileName);
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            writer.write("User Logs for: " + currentUser.getFullName() + " (" + currentUser.getEmail() + ")\n");
            writer.write("Generated on: " + java.time.LocalDateTime.now() + "\n\n");
            writer.write("Email Statistics:\n");
            writer.write("----------------\n");
            writer.write("Inbox Emails: " + emailDAO.getInboxEmails(currentUser.getEmail()).size() + "\n");
            writer.write("Sent Emails: " + emailDAO.getSentEmails(currentUser.getEmail()).size() + "\n");
            writer.write("Trash Emails: " + emailDAO.getTrashEmails(currentUser.getEmail()).size() + "\n");
            writer.write("Spam Emails: " + emailDAO.getSpamEmails(currentUser.getEmail()).size() + "\n\n");
            writer.write("Labels:\n");
            writer.write("-------\n");
            List<String> labels = emailDAO.getLabels(currentUser.getId());
            for (String label : labels) {
                writer.write("- " + label + "\n");
            }

            writer.close();
            JOptionPane.showMessageDialog(this,
                    "Logs have been saved to: " + fileName,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error generating logs: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Email> getEmailsByLabel(String label) {
        List<Email> result = new ArrayList<>();
        try {
            List<Email> allEmails = emailDAO.getInboxEmails(currentUser.getEmail()); 
            for (Email email : allEmails) {
                if (emailDAO.isLabelAssignedToEmail(currentUser.getId(), email.getId(), label)) {
                    result.add(email);
                }
            }
        } catch (Exception ex) {
           
        }
        return result;
    }

    private void showEmailsForLabel(String label) {
        JPanel labelPanel = (JPanel) contentPanel.getComponent(contentPanel.getComponentCount() - 1);

        JScrollPane scrollPane = null;
        for (Component comp : ((JPanel) ((JPanel) labelPanel.getComponent(0)).getComponents()[0]).getComponents()) {
            if (comp instanceof JScrollPane) {
                scrollPane = (JScrollPane) comp;
                break;
            }
        }
        if (scrollPane == null)
            return;
        JPanel emailListPanel = (JPanel) ((JViewport) scrollPane.getViewport()).getView();
        emailListPanel.removeAll();
        List<Email> emails = getEmailsByLabel(label);
        for (Email email : emails) {
            JPanel row = createModernEmailRowWithLabel(email);
            emailListPanel.add(row);
        }
        emailListPanel.revalidate();
        emailListPanel.repaint();
    }
}
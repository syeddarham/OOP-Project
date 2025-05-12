package com.emailplatform.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.emailplatform.dao.UserDAO;
import com.emailplatform.model.User;

public class LoginFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JTextField signupNameField;
    private JTextField signupEmailField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Email Platform - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Left panel with blue accent
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(600, 600));
        leftPanel.setBackground(new Color(51, 51, 51));

        // Blue accent line panel
        JPanel accentPanel = new JPanel();
        accentPanel.setPreferredSize(new Dimension(7, 0));
        accentPanel.setBackground(new Color(33, 120, 255));
        leftPanel.add(accentPanel, BorderLayout.EAST);

        // Logo panel
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setBackground(new Color(51, 51, 51));
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/emailplatform/gui/logo.png")));
        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.weightx = 1.0;
        gbcLogo.weighty = 1.0;
        gbcLogo.anchor = GridBagConstraints.CENTER;
        logoPanel.add(logoLabel, gbcLogo);
        leftPanel.add(logoPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Card panel for forms
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        add(cardPanel, BorderLayout.CENTER);

        // Login form
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel loginLabel = new JLabel("LOGIN");
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        loginLabel.setForeground(new Color(33, 120, 255));
        loginPanel.add(loginLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailLabel.setForeground(new Color(51, 51, 51));
        loginPanel.add(emailLabel, gbc);

        gbc.gridy++;
        loginEmailField = new JTextField(20);
        loginEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        loginEmailField.setPreferredSize(new Dimension(300, 40));
        loginEmailField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)));
        loginPanel.add(loginEmailField, gbc);

        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setForeground(new Color(51, 51, 51));
        loginPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        loginPasswordField = new JPasswordField(20);
        loginPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        loginPasswordField.setPreferredSize(new Dimension(300, 40));
        loginPasswordField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)));
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridy++;
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(120, 36));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(33, 120, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 32, 8, 32));
        loginPanel.add(loginButton, gbc);

        gbc.gridy++;
        JLabel textLabel1 = new JLabel("Don't have an account yet?");
        textLabel1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel1.setForeground(new Color(51, 51, 51));
        JButton signupButton = new JButton("Signup");
        signupButton.setPreferredSize(new Dimension(80, 28));
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signupButton.setBackground(new Color(33, 120, 255));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        JPanel signupPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        signupPanel1.setBackground(Color.WHITE);
        signupPanel1.add(textLabel1);
        signupPanel1.add(signupButton);
        loginPanel.add(signupPanel1, gbc);

        // Signup form
        JPanel signupPanel = new JPanel(new GridBagLayout());
        signupPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.insets = new Insets(20, 0, 10, 0);
        gbc2.anchor = GridBagConstraints.CENTER;

        JLabel signupLabel = new JLabel("Sign Up");
        signupLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        signupLabel.setForeground(new Color(33, 120, 255));
        signupPanel.add(signupLabel, gbc2);

        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.gridy++;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(new Color(51, 51, 51));
        signupPanel.add(nameLabel, gbc2);

        gbc2.gridy++;
        signupNameField = new JTextField(15);
        signupNameField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        signupNameField.setPreferredSize(new Dimension(300, 40));
        signupNameField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)));
        signupPanel.add(signupNameField, gbc2);

        gbc2.gridy++;
        JLabel signupEmailLabel = new JLabel("Email:");
        signupEmailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        signupEmailLabel.setForeground(new Color(51, 51, 51));
        signupPanel.add(signupEmailLabel, gbc2);

        gbc2.gridy++;
        signupEmailField = new JTextField(15);
        signupEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        signupEmailField.setPreferredSize(new Dimension(300, 40));
        signupEmailField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)));
        signupPanel.add(signupEmailField, gbc2);

        gbc2.gridy++;
        JLabel signupPasswordLabel = new JLabel("Password:");
        signupPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        signupPasswordLabel.setForeground(new Color(51, 51, 51));
        signupPanel.add(signupPasswordLabel, gbc2);

        gbc2.gridy++;
        signupPasswordField = new JPasswordField(15);
        signupPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        signupPasswordField.setPreferredSize(new Dimension(300, 40));
        signupPasswordField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)));
        signupPanel.add(signupPasswordField, gbc2);

        gbc2.gridy++;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        confirmPasswordLabel.setForeground(new Color(51, 51, 51));
        signupPanel.add(confirmPasswordLabel, gbc2);

        gbc2.gridy++;
        signupConfirmPasswordField = new JPasswordField(15);
        signupConfirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        signupConfirmPasswordField.setPreferredSize(new Dimension(300, 40));
        signupConfirmPasswordField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)));
        signupPanel.add(signupConfirmPasswordField, gbc2);

        gbc2.gridy++;
        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(120, 36));
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.setBackground(new Color(33, 120, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 32, 8, 32));
        signupPanel.add(submitButton, gbc2);

        gbc2.gridy++;
        JLabel textLabel2 = new JLabel("Already have an account?");
        textLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel2.setForeground(new Color(51, 51, 51));
        JButton smallLoginButton = new JButton("Login");
        smallLoginButton.setPreferredSize(new Dimension(80, 28));
        smallLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        smallLoginButton.setBackground(new Color(33, 120, 255));
        smallLoginButton.setForeground(Color.WHITE);
        smallLoginButton.setFocusPainted(false);
        JPanel signupPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        signupPanel2.setBackground(Color.WHITE);
        signupPanel2.add(textLabel2);
        signupPanel2.add(smallLoginButton);
        signupPanel.add(signupPanel2, gbc2);

        cardPanel.add(loginPanel, "Login");
        cardPanel.add(signupPanel, "Signup");
        cardLayout.show(cardPanel, "Login");

        // Button actions
        signupButton.addActionListener(e -> cardLayout.show(cardPanel, "Signup"));
        smallLoginButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        loginButton.addActionListener(e -> handleLogin());
        submitButton.addActionListener(e -> handleSignup());

        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/com/emailplatform/gui/logo.png")).getImage());
    }

    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password", "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            User user = userDAO.authenticateUser(email, password);
            if (user != null) {
                openDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password", "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSignup() {
        String fullName = signupNameField.getText().trim();
        String email = signupEmailField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirmPassword = new String(signupConfirmPasswordField.getPassword());
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (userDAO.registerUser(email, password, fullName)) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "Login");
                signupNameField.setText("");
                signupEmailField.setText("");
                signupPasswordField.setText("");
                signupConfirmPasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Email might already exist.",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dashboard = new DashboardFrame(user);
            dashboard.setVisible(true);
            this.dispose();
        });
    }
}
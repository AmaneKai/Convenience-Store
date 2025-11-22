package com.konbini.view.swing;

import com.konbini.controller.EmployeeController;

import javax.swing.*;
import java.awt.*;

public class EmployeeLoginDialog extends JDialog {
    private JTextField idField;
    private JPasswordField passwordField;
    private boolean authenticated = false;
    private String employeeId = null;

    private final EmployeeController employeeController;

    public EmployeeLoginDialog(Frame parent, EmployeeController employeeController) {
        super(parent, "Employee Login", true);
        this.employeeController = employeeController;

        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel titleLabel = new JLabel("Employee Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);

        // Help text showing sample employees
        JLabel helpLabel1 = new JLabel();
        helpLabel1.setFont(new Font("Arial", Font.PLAIN, 11));
        helpLabel1.setForeground(new Color(100, 100, 100));
        helpLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel helpLabel2 = new JLabel();
        helpLabel2.setFont(new Font("Arial", Font.PLAIN, 10));
        helpLabel2.setForeground(new Color(120, 120, 120));
        helpLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(helpLabel1);
        titlePanel.add(helpLabel2);

        add(titlePanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Employee ID Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        inputPanel.add(new JLabel("Employee ID:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        idField = new JTextField(15);
        inputPanel.add(idField, gbc);

        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        inputPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        passwordField = new JPasswordField(15);
        inputPanel.add(passwordField, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.addActionListener(e -> handleLogin());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.addActionListener(e -> {
            authenticated = false;
            dispose();
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Enter key handling
        passwordField.addActionListener(e -> handleLogin());

        pack();
        setResizable(false);
    }

    private void handleLogin() {
        String id = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        // DEBUG PRINTS
        System.out.println("DEBUG - ID length: " + id.length());
        System.out.println("DEBUG - ID value: '" + id + "'");
        System.out.println("DEBUG - Password length: " + password.length());
        System.out.println("DEBUG - Password value: '" + password + "'");

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both Employee ID and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            if (employeeController.authenticate(id, password)) {
                authenticated = true;
                employeeId = id;
                dispose();
            } else {
                // ADD THIS DEBUG TOO
                System.out.println("DEBUG - Authentication FAILED");
                JOptionPane.showMessageDialog(this,
                        "Invalid Employee ID or password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                clearFields();
            }
        }
    }
    private void clearFields() {
        passwordField.setText("");
        idField.selectAll();
        idField.requestFocus();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Shows the login dialog and returns result
     */
    public static LoginResult showLoginDialog(Frame parent, EmployeeController employeeController) {
        EmployeeLoginDialog dialog = new EmployeeLoginDialog(parent, employeeController);
        dialog.setVisible(true);
        return new LoginResult(dialog.isAuthenticated(), dialog.getEmployeeId());
    }

    /**
     * Result object from login dialog
     */
    public static class LoginResult {
        private final boolean authenticated;
        private final String employeeId;

        public LoginResult(boolean authenticated, String employeeId) {
            this.authenticated = authenticated;
            this.employeeId = employeeId;
        }

        public boolean isAuthenticated() {
            return authenticated;
        }

        public String getEmployeeId() {
            return employeeId;
        }
    }
}

package com.konbini.view.swing;

import com.konbini.controller.EmployeeController;

import javax.swing.*;
import java.awt.*;

/**
 * EmployeeLoginDialog provides a modal dialog for employee authentication.
 * This dialog collects employee ID and password credentials and validates them
 * against the employee controller. It returns the authentication result through
 * a LoginResult object.
 *
 * The dialog features a clean layout with input fields for credentials,
 * login/cancel buttons, and includes debug capabilities for troubleshooting
 * authentication issues.
 */
public class EmployeeLoginDialog extends JDialog {
    /** Text field for entering employee ID */
    private JTextField idField;

    /** Password field for entering employee password */
    private JPasswordField passwordField;

    /** Flag indicating whether authentication was successful */
    private boolean authenticated = false;

    /** The authenticated employee ID if login was successful */
    private String employeeId = null;

    /** Controller responsible for employee authentication logic */
    private final EmployeeController employeeController;

    /**
     * Constructs a new EmployeeLoginDialog with the specified parent frame and controller.
     * Initializes the UI components and sets up the dialog properties.
     *
     * @param parent the parent frame for this dialog, used for positioning
     * @param employeeController the EmployeeController that handles authentication logic
     */
    public EmployeeLoginDialog(Frame parent, EmployeeController employeeController) {
        super(parent, "Employee Login", true);
        this.employeeController = employeeController;

        initializeUI();
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes the user interface components of the dialog.
     * Creates and arranges the title, input fields, and buttons in a organized layout.
     *
     * <p>The UI consists of:
     * <ul>
     *   <li>A title panel with the dialog title</li>
     *   <li>An input panel with employee ID and password fields</li>
     *   <li>A button panel with login and cancel actions</li>
     * </ul>
     * </p>
     */
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

    /**
     * Handles the login process by validating input and authenticating credentials.
     * Validates that both fields are filled, then delegates authentication to the controller.
     * Shows appropriate error messages for invalid input or failed authentication.
     *
     * <p>Includes debug output for troubleshooting authentication issues.</p>
     */
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

    /**
     * Clears the input fields and resets focus to the ID field.
     * Used after failed login attempts to allow quick retry.
     */
    private void clearFields() {
        passwordField.setText("");
        idField.selectAll();
        idField.requestFocus();
    }

    /**
     * Returns whether the user was successfully authenticated.
     *
     * @return true if authentication was successful, false otherwise
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Returns the employee ID of the authenticated user.
     *
     * @return the employee ID if authenticated, null otherwise
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Shows the login dialog and returns the authentication result.
     * This is a convenience static method that creates, displays, and processes
     * the login dialog in one call.
     *
     * @param parent the parent frame for the dialog
     * @param employeeController the EmployeeController for authentication
     * @return LoginResult containing authentication status and employee ID
     */
    public static LoginResult showLoginDialog(Frame parent, EmployeeController employeeController) {
        EmployeeLoginDialog dialog = new EmployeeLoginDialog(parent, employeeController);
        dialog.setVisible(true);
        return new LoginResult(dialog.isAuthenticated(), dialog.getEmployeeId());
    }

    /**
     * LoginResult represents the outcome of an authentication attempt.
     * Contains both the authentication status and the authenticated employee ID.
     */
    public static class LoginResult {
        /** Flag indicating successful authentication */
        private final boolean authenticated;

        /** The authenticated employee ID */
        private final String employeeId;

        /**
         * Constructs a new LoginResult with the specified authentication data.
         *
         * @param authenticated whether authentication was successful
         * @param employeeId the employee ID if authenticated
         */
        public LoginResult(boolean authenticated, String employeeId) {
            this.authenticated = authenticated;
            this.employeeId = employeeId;
        }

        /**
         * Returns whether authentication was successful.
         *
         * @return true if authenticated, false otherwise
         */
        public boolean isAuthenticated() {
            return authenticated;
        }

        /**
         * Returns the authenticated employee ID.
         *
         * @return the employee ID if authenticated, null otherwise
         */
        public String getEmployeeId() {
            return employeeId;
        }
    }
}
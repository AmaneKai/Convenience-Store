package com.konbini.view.swing;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

/**
 * Reusable password dialog component for admin area authentication.
 * Shows a simple password prompt and validates against the employee password.
 * FIXED: Now accepts Enter key as OK and Escape as Cancel.
 */
public class PasswordDialog extends JDialog {
    private JPasswordField passwordField;
    private boolean authenticated = false;
    private static final String CORRECT_PASSWORD = "password";

    public PasswordDialog(JFrame parent) {
        super(parent, "Authentication Required", true);
        validateParentFrame(parent);
        initializeUI();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        try {
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setSize(350, 150);
            setResizable(false);

            JPanel contentPanel = createContentPanel();
            add(contentPanel);

            setupKeyBindings();
            passwordField.requestFocusInWindow();

        } catch (Exception e) {
            handleUIException(e, "initializing password dialog", "Failed to initialize authentication dialog.");
            dispose();
        }
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel promptLabel = new JLabel("Enter passcode to access this area:");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        contentPanel.add(promptLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel);

        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> handleOK());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> handleCancel());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void setupKeyBindings() {
        try {
            // Enter key for OK
            passwordField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "OK");
            passwordField.getActionMap().put("OK", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleOK();
                }
            });

            // Escape key for Cancel
            passwordField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
            passwordField.getActionMap().put("Cancel", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleCancel();
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to setup key bindings: " + e.getMessage());
            // Continue without key bindings - dialog will still work with buttons
        }
    }

    // ==================== EVENT HANDLERS ====================

    private void handleOK() {
        try {
            validatePasswordField();

            String enteredPassword = getEnteredPassword();
            validatePasswordInput(enteredPassword);

            if (authenticatePassword(enteredPassword)) {
                authenticated = true;
                dispose();
            } else {
                handleAuthenticationFailure();
            }

        } catch (IllegalArgumentException e) {
            handleUIException(e, "password validation", "Invalid password input. Please try again.");
        } catch (Exception e) {
            handleUIException(e, "authentication", "Authentication failed. Please try again.");
        }
    }

    private void handleCancel() {
        try {
            authenticated = false;
            dispose();
        } catch (Exception e) {
            handleUIException(e, "canceling authentication", "Failed to cancel authentication.");
            authenticated = false;
            dispose(); // Force close even on error
        }
    }

    // ==================== AUTHENTICATION LOGIC ====================

    private String getEnteredPassword() {
        char[] passwordChars = passwordField.getPassword();
        try {
            return new String(passwordChars);
        } finally {
            // Clear the password from memory for security
            java.util.Arrays.fill(passwordChars, '\0');
        }
    }

    private boolean authenticatePassword(String enteredPassword) {
        return CORRECT_PASSWORD.equals(enteredPassword);
    }

    private void handleAuthenticationFailure() {
        JOptionPane.showMessageDialog(
                this,
                "Incorrect passcode. Access denied.",
                "Authentication Failed",
                JOptionPane.ERROR_MESSAGE
        );
        clearPasswordField();
        passwordField.requestFocusInWindow();
    }

    private void clearPasswordField() {
        try {
            passwordField.setText("");
        } catch (Exception e) {
            System.err.println("Failed to clear password field: " + e.getMessage());
        }
    }

    // ==================== VALIDATION METHODS ====================

    private void validateParentFrame(JFrame parent) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent frame cannot be null");
        }
    }

    private void validatePasswordField() {
        if (passwordField == null) {
            throw new IllegalStateException("Password field not initialized");
        }
    }

    private void validatePasswordInput(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        // Note: Empty password is allowed as it's a valid input (just won't match)
    }

    // ==================== ERROR HANDLING ====================

    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("PasswordDialog Error " + context + ": " + e.getMessage());
        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ==================== PUBLIC METHODS ====================

    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Static helper to show dialog and check authentication in one call.
     * @param parent Parent frame
     * @return true if user authenticated successfully, false otherwise
     */
    public static boolean authenticate(JFrame parent) {
        try {
            validateParentFrameForStaticCall(parent);

            PasswordDialog dialog = new PasswordDialog(parent);
            dialog.setVisible(true);
            return dialog.isAuthenticated();

        } catch (IllegalArgumentException e) {
            System.err.println("Authentication failed - invalid parent frame: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during authentication: " + e.getMessage());
            JOptionPane.showMessageDialog(parent,
                    "Authentication service unavailable. Please try again.",
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void validateParentFrameForStaticCall(JFrame parent) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent frame cannot be null for authentication");
        }
        if (!parent.isDisplayable()) {
            throw new IllegalArgumentException("Parent frame must be displayable");
        }
    }
}
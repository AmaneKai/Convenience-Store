package com.konbini.view.swing;

import javax.swing.*;
import java.awt.*;

/**
 * UserTypeSelectionDialog provides a simple modal dialog for selecting user type.
 * This dialog presents the user with a choice between Customer and Employee roles
 * to determine which portal they should access in the application.
 */
public class UserTypeSelectionDialog extends JDialog {
    /** The selected user type, either "CUSTOMER" or "EMPLOYEE" */
    private String userType = null;

    /**
     * Constructs a new UserTypeSelectionDialog with the specified parent frame.
     * Initializes the UI components and sets up the dialog properties.
     *
     * @param parent the parent frame for this dialog, used for positioning
     */
    public UserTypeSelectionDialog(Frame parent) {
        super(parent, "Welcome to Konbini", true);
        initializeUI();
        setLocationRelativeTo(parent);
    }

    /**
     * Initializes the user interface components of the dialog.
     * Creates and arranges the title and role selection buttons in a clean layout.
     *
     * The UI consists of:
     * - A title panel with welcome message
     * - A button panel with Customer and Employee selection buttons
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        JLabel titleLabel = new JLabel("Welcome! Please select your role:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JButton customerButton = createButton("Customer", new Color(52, 152, 219));
        customerButton.addActionListener(e -> {
            userType = "CUSTOMER";
            dispose();
        });

        JButton employeeButton = createButton("Employee", new Color(230, 126, 34));
        employeeButton.addActionListener(e -> {
            userType = "EMPLOYEE";
            dispose();
        });

        buttonPanel.add(customerButton);
        buttonPanel.add(employeeButton);
        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setResizable(false);
    }

    /**
     * Creates a styled button with consistent appearance.
     * Buttons are color-coded to differentiate between customer and employee roles.
     *
     * @param text the text to display on the button
     * @param color the background color for the button
     * @return a configured JButton with the specified text and styling
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 60));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    /**
     * Returns the selected user type.
     *
     * @return the selected user type as a String: "CUSTOMER", "EMPLOYEE", or null if no selection was made
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Shows the dialog and returns the selected user type.
     * This is a convenience static method that creates, displays, and processes
     * the user type selection dialog in one call.
     *
     * @param parent the parent frame for the dialog
     * @return the selected user type as a String: "CUSTOMER" or "EMPLOYEE",
     *         or null if the dialog was closed without selection
     */
    public static String showDialog(Frame parent) {
        UserTypeSelectionDialog dialog = new UserTypeSelectionDialog(parent);
        dialog.setVisible(true);
        return dialog.getUserType();
    }
}
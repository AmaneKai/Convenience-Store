package com.konbini.view.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Simple dialog to select user type: Customer or Employee
 */
public class UserTypeSelectionDialog extends JDialog {
    private String userType = null;

    public UserTypeSelectionDialog(Frame parent) {
        super(parent, "Welcome to Konbini", true);
        initializeUI();
        setLocationRelativeTo(parent);
    }

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

    public String getUserType() {
        return userType;
    }

    /**
     * Shows the dialog and returns the selected user type
     */
    public static String showDialog(Frame parent) {
        UserTypeSelectionDialog dialog = new UserTypeSelectionDialog(parent);
        dialog.setVisible(true);
        return dialog.getUserType();
    }
}

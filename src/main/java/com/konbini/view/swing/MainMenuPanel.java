package com.konbini.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.konbini.controller.DataManagementController;

/**
 * Main menu panel - navigation hub with authentication for admin areas.
 */
public class MainMenuPanel extends JPanel {
    private Consumer<String> navigationCallback;
    private Runnable exitCallback;
    private DataManagementController dataController;
    private JFrame parentFrame;

    public MainMenuPanel(JFrame parentFrame,
                         Consumer<String> navigationCallback,
                         Runnable exitCallback,
                         DataManagementController dataController) {
        validateConstructorArgs(parentFrame, navigationCallback, exitCallback, dataController);

        this.parentFrame = parentFrame;
        this.navigationCallback = navigationCallback;
        this.exitCallback = exitCallback;
        this.dataController = dataController;

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center - Menu Buttons
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("KONBINI STORE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(236, 240, 241));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Admin buttons - require authentication
        centerPanel.add(createAdminButton("Product Management", "Product"), gbc);
        centerPanel.add(createAdminButton("Customer Management", "Customer"), gbc);
        centerPanel.add(createAdminButton("Transaction Reports", "Transaction"), gbc);

        // Customer button - no auth needed
        centerPanel.add(createButton("Cart & Checkout", this::handleCartNavigation), gbc);

        // Data buttons
        centerPanel.add(createButton("Save Data", this::handleSaveData), gbc);
        centerPanel.add(createButton("Load Data", this::handleLoadData), gbc);
        centerPanel.add(createExitButton("Exit", this::handleExit), gbc);

        return centerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        JLabel footerLabel = new JLabel("CCPROG3 Machine Project | Point of Sale System");
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);
        return footerPanel;
    }

    // ==================== EVENT HANDLERS ====================

    private void handleCartNavigation() {
        try {
            navigationCallback.accept("Cart");
        } catch (Exception e) {
            handleUIException(e, "navigating to cart", "Failed to open cart. Please try again.");
        }
    }

    private void handleSaveData() {
        try {
            dataController.handleSaveData();
        } catch (Exception e) {
            handleUIException(e, "saving data", "Failed to save data. Please try again.");
        }
    }

    private void handleLoadData() {
        try {
            dataController.handleLoadData();
        } catch (Exception e) {
            handleUIException(e, "loading data", "Failed to load data. Please try again.");
        }
    }

    private void handleExit() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                exitCallback.run();
            }
        } catch (Exception e) {
            handleUIException(e, "exiting application", "Failed to exit properly. Please try again.");
        }
    }

    private void handleAdminNavigation(String screenName) {
        try {
            if (authenticateUser()) {
                navigationCallback.accept(screenName);
            }
        } catch (Exception e) {
            handleUIException(e, "admin authentication", "Authentication failed. Please try again.");
        }
    }

    // ==================== AUTHENTICATION ====================

    private boolean authenticateUser() {
        try {
            return PasswordDialog.authenticate(parentFrame);
        } catch (Exception e) {
            handleUIException(e, "user authentication", "Authentication error. Please try again.");
            return false;
        }
    }

    // ==================== BUTTON CREATION ====================

    private JButton createAdminButton(String text, String screenName) {
        JButton btn = createBaseButton(text, new Color(230, 126, 34)); // Orange for admin
        btn.addActionListener(e -> handleAdminNavigation(screenName));
        return btn;
    }

    private JButton createButton(String text, Runnable action) {
        JButton btn = createBaseButton(text, new Color(52, 152, 219)); // Blue for regular
        btn.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                handleUIException(ex, "button action: " + text, "Action failed. Please try again.");
            }
        });
        return btn;
    }

    private JButton createExitButton(String text, Runnable action) {
        JButton btn = createBaseButton(text, new Color(231, 76, 60)); // Red for exit
        btn.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                handleUIException(ex, "exit action", "Exit failed. Please try again.");
            }
        });
        return btn;
    }

    private JButton createBaseButton(String text, Color backgroundColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(300, 60));
        btn.setFocusPainted(false);
        btn.setBackground(backgroundColor);
        btn.setForeground(Color.BLACK);

        // Hover effects
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(btn.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(backgroundColor);
            }
        });

        return btn;
    }

    // ==================== VALIDATION METHODS ====================

    private void validateConstructorArgs(JFrame parentFrame, Consumer<String> navigationCallback,
                                         Runnable exitCallback, DataManagementController dataController) {
        if (parentFrame == null) {
            throw new IllegalArgumentException("Parent frame cannot be null");
        }
        if (navigationCallback == null) {
            throw new IllegalArgumentException("Navigation callback cannot be null");
        }
        if (exitCallback == null) {
            throw new IllegalArgumentException("Exit callback cannot be null");
        }
        if (dataController == null) {
            throw new IllegalArgumentException("DataManagementController cannot be null");
        }
    }

    // ==================== ERROR HANDLING ====================

    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("UI Error " + context + ": " + e.getMessage());
        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ==================== PUBLIC METHODS ====================

    public void showWelcomeMessage() {
        try {
            JOptionPane.showMessageDialog(this,
                    "Welcome to Konbini Store!",
                    "Welcome",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            handleUIException(e, "showing welcome message", "Failed to display welcome message.");
        }
    }
}
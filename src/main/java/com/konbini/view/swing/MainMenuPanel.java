package com.konbini.view.swing;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Main menu panel that displays the primary navigation options for the POS system.
 * Uses a clean, button-based interface for easy mouse navigation.
 */
public class MainMenuPanel extends JPanel {
    private Consumer<String> navigationCallback;
    private int selectedChoice = -1;
    private final Object choiceLock = new Object();
    
    /**
     * Constructs the main menu panel with navigation callback.
     * @param navigationCallback Callback to navigate to different screens
     */
    public MainMenuPanel(Consumer<String> navigationCallback) {
        this.navigationCallback = navigationCallback;
        initializeUI();
    }
    
    /**
     * Initializes the UI components for the main menu.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185)); // Blue header
        JLabel titleLabel = new JLabel("KONBINI STORE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel with menu buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(236, 240, 241));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create menu buttons
        JButton productBtn = createMenuButton("Product Management", 1);
        JButton customerBtn = createMenuButton("Customer Management", 2);
        JButton cartBtn = createMenuButton("Cart & Checkout", 3);
        JButton transactionBtn = createMenuButton("Transaction Reports", 4);
        JButton dataBtn = createMenuButton("Data Management", 5);
        JButton exitBtn = createMenuButton("Exit", 0);
        
        centerPanel.add(productBtn, gbc);
        centerPanel.add(customerBtn, gbc);
        centerPanel.add(cartBtn, gbc);
        centerPanel.add(transactionBtn, gbc);
        centerPanel.add(dataBtn, gbc);
        centerPanel.add(exitBtn, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        JLabel footerLabel = new JLabel("CCPROG3 Machine Project | Point of Sale System");
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates a styled menu button with action listener.
     */
    private JButton createMenuButton(String text, int choice) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setPreferredSize(new Dimension(300, 60));
        button.setFocusPainted(false);
        
        // Color scheme
        if (choice == 0) {
            button.setBackground(new Color(231, 76, 60)); // Red for exit
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(52, 152, 219)); // Blue for regular options
            button.setForeground(Color.WHITE);
        }
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (choice == 0) {
                    button.setBackground(new Color(231, 76, 60));
                } else {
                    button.setBackground(new Color(52, 152, 219));
                }
            }
        });
        
        button.addActionListener(e -> handleMenuChoice(choice));
        
        return button;
    }
    
    /**
     * Handles menu choice selection.
     */
    private void handleMenuChoice(int choice) {
        synchronized (choiceLock) {
            selectedChoice = choice;
            choiceLock.notifyAll();
        }
        
        // Navigate based on choice (except exit)
        if (choice != 0) {
            String destination = switch (choice) {
                case 1 -> "Product";
                case 2 -> "Customer";
                case 3 -> "Cart";
                case 4 -> "Transaction";
                case 5 -> "Data";
                default -> "MainMenu";
            };
            navigationCallback.accept(destination);
        }
    }
    
    /**
     * Gets the user's menu choice (blocking call).
     */
    public int getMenuChoice() {
        synchronized (choiceLock) {
            while (selectedChoice == -1) {
                try {
                    choiceLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return 0;
                }
            }
            int choice = selectedChoice;
            selectedChoice = -1; // Reset for next call
            return choice;
        }
    }
    
    /**
     * Shows a welcome message dialog.
     */
    public void showWelcomeMessage() {
        JOptionPane.showMessageDialog(this,
            "Welcome to Konbini Store Point of Sale System!\n" +
            "Select an option from the main menu to begin.",
            "Welcome",
            JOptionPane.INFORMATION_MESSAGE);
    }
}

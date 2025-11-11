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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.konbini.controller.CartManagementController;
import com.konbini.controller.CustomerManagementController;
import com.konbini.controller.DataManagementController;
import com.konbini.controller.ProductManagementController;
import com.konbini.controller.TransactionManagementController;

/**
 * Main menu panel - simple navigation hub.
 * Just displays buttons that navigate to different screens.
 */
public class MainMenuPanel extends JPanel {
    private Consumer<String> navigationCallback;
    private Runnable exitCallback;
    
    private ProductManagementController productController;
    private CustomerManagementController customerController;
    private CartManagementController cartController;
    private TransactionManagementController transactionController;
    private DataManagementController dataController;
    
    public MainMenuPanel(Consumer<String> navigationCallback,
                        Runnable exitCallback,
                        ProductManagementController productController,
                        CustomerManagementController customerController,
                        CartManagementController cartController,
                        TransactionManagementController transactionController,
                        DataManagementController dataController) {
        this.navigationCallback = navigationCallback;
        this.exitCallback = exitCallback;
        this.productController = productController;
        this.customerController = customerController;
        this.cartController = cartController;
        this.transactionController = transactionController;
        this.dataController = dataController;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("KONBINI STORE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Menu Buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(236, 240, 241));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Navigation buttons - just go to screens, don't call controller logic
        centerPanel.add(createButton("Product Management", () -> navigationCallback.accept("Product")), gbc);
        centerPanel.add(createButton("Customer Management", () -> navigationCallback.accept("Customer")), gbc);
        centerPanel.add(createButton("Cart & Checkout", () -> navigationCallback.accept("Cart")), gbc);
        centerPanel.add(createButton("Transaction Reports", () -> navigationCallback.accept("Transaction")), gbc);
        centerPanel.add(createButton("Save Data", () -> {
            if (dataController != null) dataController.handleSaveData();
        }), gbc);
        centerPanel.add(createButton("Load Data", () -> {
            if (dataController != null) dataController.handleLoadData();
        }), gbc);
        centerPanel.add(createExitButton("Exit", () -> handleExit()), gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        JLabel footerLabel = new JLabel("CCPROG3 Machine Project | Point of Sale System");
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(300, 60));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.BLACK);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(btn.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 152, 219));
            }
        });
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    private JButton createExitButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(300, 60));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(231, 76, 60));
        btn.setForeground(Color.BLACK);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(btn.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(231, 76, 60));
            }
        });
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(this, "Exit?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            exitCallback.run();
        }
    }
    
    public void showWelcomeMessage() {
        JOptionPane.showMessageDialog(this, "Welcome to Konbini Store!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
    }
}
package com.konbini.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.konbini.controller.CartManagementController;
import com.konbini.dto.CartDTO;
import com.konbini.dto.TransactionItemDTO;

public class CartPanel extends JPanel {
    private Runnable backCallback;
    private CartManagementController controller;
    
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel customerInfoLabel;
    private JLabel subtotalLabel;
    
    private static final String[] COLUMN_NAMES = {"Product ID", "Product Name", "Price", "Quantity", "Total"};
    
    public CartPanel(CartManagementController controller, Runnable backCallback) {
        this.controller = controller;
        this.backCallback = backCallback;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Shopping Cart & Checkout");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> backCallback.run());
        headerPanel.add(backBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Info and Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(236, 240, 241));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.GRAY)
        ));
        
        customerInfoLabel = new JLabel("No active cart");
        customerInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(customerInfoLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        
        subtotalLabel = new JLabel("Subtotal: ₱0.00");
        subtotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(subtotalLabel);
        
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(createButton("Create Cart", () -> controller.handleCreateCart()));
        buttonPanel.add(createButton("View Cart", () -> controller.handleViewCart()));
        buttonPanel.add(createButton("Add Item", () -> controller.handleAddItem()));
        buttonPanel.add(createButton("Remove Item", () -> controller.handleRemoveItem()));
        buttonPanel.add(createButton("Update Qty", () -> controller.handleUpdateQuantity()));
        buttonPanel.add(createButton("Clear Cart", () -> controller.handleClearCart()));
        
        JButton checkoutBtn = createButton("CHECKOUT", () -> controller.handleCheckout());
        checkoutBtn.setBackground(new Color(39, 174, 96));
        checkoutBtn.setForeground(Color.WHITE);
        buttonPanel.add(checkoutBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    public void displayCart(CartDTO cart) {
        StringBuilder customerInfo = new StringBuilder();
        customerInfo.append("Customer: ").append(cart.getCustomerName());
        customerInfo.append(" (ID: ").append(cart.getCustomerId()).append(")");
        if (cart.isCustomerIsSeniorCitizen()) customerInfo.append(" [SENIOR]");
        if (cart.isCustomerHasMembershipCard()) customerInfo.append(" [MEMBER: ").append(cart.getCustomerPoints()).append(" pts]");
        
        customerInfoLabel.setText(customerInfo.toString());
        subtotalLabel.setText(String.format("Subtotal: ₱%.2f", cart.getSubtotal()));
        
        tableModel.setRowCount(0);
        for (TransactionItemDTO item : cart.getItems()) {
            tableModel.addRow(new Object[]{
                item.getProductId(),
                item.getProductName(),
                "₱" + String.format("%.2f", item.getUnitPrice()),
                item.getQuantity(),
                "₱" + String.format("%.2f", item.getTotalPrice())
            });
        }
    }
}
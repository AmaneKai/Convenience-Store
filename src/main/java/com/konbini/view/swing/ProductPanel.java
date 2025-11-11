package com.konbini.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.konbini.controller.ProductManagementController;
import com.konbini.dto.ProductDTO;

/**
 * Product panel - displays products and has action buttons.
 * Buttons call single-action methods on the controller (no loops).
 */
public class ProductPanel extends JPanel {
    private Runnable backCallback;
    private ProductManagementController controller;
    
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    private static final String[] COLUMN_NAMES = {
        "ID", "Name", "Price", "Quantity", "Category", "Brand", "Variant", "Expiration", "Status"
    };
    
    public ProductPanel(ProductManagementController controller, Runnable backCallback) {
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
        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> backCallback.run());
        headerPanel.add(backBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(productTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        statusLabel = new JLabel("No products loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(createActionButton("View All", () -> controller.handleViewAllProducts()));
        buttonPanel.add(createActionButton("Add", () -> controller.handleAddProduct()));
        buttonPanel.add(createActionButton("Update", () -> controller.handleUpdateProduct()));
        buttonPanel.add(createActionButton("Remove", () -> controller.handleRemoveProduct()));
        buttonPanel.add(createActionButton("Restock", () -> controller.handleRestockProduct()));
        buttonPanel.add(createActionButton("Low Stock", () -> controller.handleViewLowStock()));
        buttonPanel.add(createActionButton("Expired", () -> controller.handleViewExpired()));
        buttonPanel.add(createActionButton("Search", () -> controller.handleSearchProducts()));
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createActionButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    public void displayProducts(List<ProductDTO> products) {
        tableModel.setRowCount(0);
        for (ProductDTO p : products) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), "₱" + String.format("%.2f", p.getPrice()),
                p.getQuantity(), p.getCategory(), p.getBrand(), p.getVariant(),
                p.getExpirationDate() != null ? p.getExpirationDate() : "N/A",
                p.isExpired() ? "EXPIRED" : (p.isLowStock() ? "LOW" : "OK")
            });
        }
        statusLabel.setText("Displaying " + products.size() + " product(s)");
    }
    
    public void displayProduct(ProductDTO product) {
        StringBuilder sb = new StringBuilder();
        sb.append("Product Details\n");
        sb.append("================\n\n");
        sb.append("ID: ").append(product.getId()).append("\n");
        sb.append("Name: ").append(product.getName()).append("\n");
        sb.append("Price: ₱").append(String.format("%.2f", product.getPrice())).append("\n");
        sb.append("Quantity: ").append(product.getQuantity()).append("\n");
        sb.append("Category: ").append(product.getCategory()).append("\n");
        sb.append("Brand: ").append(product.getBrand()).append("\n");
        sb.append("Status: ").append(product.isExpired() ? "EXPIRED" : (product.isLowStock() ? "LOW STOCK" : "OK")).append("\n");
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, textArea, "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void displayLowStockProducts(List<ProductDTO> products) {
        displayProducts(products);
        if (!products.isEmpty()) {
            JOptionPane.showMessageDialog(this, products.size() + " product(s) low in stock!", "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void displayExpiredProducts(List<ProductDTO> products) {
        displayProducts(products);
        if (!products.isEmpty()) {
            JOptionPane.showMessageDialog(this, products.size() + " product(s) expired!", "Expired Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
}
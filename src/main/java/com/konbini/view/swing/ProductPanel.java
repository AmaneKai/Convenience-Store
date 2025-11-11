package com.konbini.view.swing;

import com.konbini.dto.ProductDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing and displaying product information.
 * Uses JTable for displaying product lists with filtering and sorting capabilities.
 */
public class ProductPanel extends JPanel {
    private Runnable backCallback;
    private int selectedChoice = -1;
    private final Object choiceLock = new Object();
    
    // UI Components
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JPanel buttonPanel;
    
    // Table columns
    private static final String[] COLUMN_NAMES = {
        "ID", "Name", "Price", "Quantity", "Category", 
        "Brand", "Variant", "Expiration", "Status"
    };
    
    /**
     * Constructs the product panel with a back navigation callback.
     */
    public ProductPanel(Runnable backCallback) {
        this.backCallback = backCallback;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel("Product Management");
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Create table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        productTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        productTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Price
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Quantity
        productTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Category
        productTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Brand
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Variant
        productTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Expiration
        productTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Status
        
        JScrollPane scrollPane = new JScrollPane(productTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel("No products loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Action buttons
        buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the header panel with title and back button.
     */
    private JPanel createHeaderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("← Back to Main Menu");
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> backCallback.run());
        panel.add(backButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the button panel with action buttons.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        String[] buttonLabels = {
            "View All Products", "Add Product", "Update Product", 
            "Remove Product", "Restock Product", "View Low Stock",
            "View Expired", "Search Products", "Back"
        };
        
        for (int i = 0; i < buttonLabels.length; i++) {
            final int choice = i + 1;
            JButton btn = new JButton(buttonLabels[i]);
            btn.setPreferredSize(new Dimension(180, 40));
            btn.addActionListener(e -> handleMenuChoice(choice == 9 ? 0 : choice));
            panel.add(btn);
        }
        
        return panel;
    }
    
    /**
     * Handles menu choice selection.
     */
    private void handleMenuChoice(int choice) {
        synchronized (choiceLock) {
            selectedChoice = choice;
            choiceLock.notifyAll();
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
            selectedChoice = -1;
            return choice;
        }
    }
    
    /**
     * Displays a list of products in the table.
     */
    public void displayProducts(List<ProductDTO> products) {
        tableModel.setRowCount(0); // Clear existing rows
        
        for (ProductDTO product : products) {
            Object[] row = {
                product.getId(),
                product.getName(),
                String.format("₱%.2f", product.getPrice()),
                product.getQuantity(),
                product.getCategory(),
                product.getBrand(),
                product.getVariant(),
                product.getExpirationDate() != null ? 
                    product.getExpirationDate().toString() : "N/A",
                getStatusString(product)
            };
            tableModel.addRow(row);
        }
        
        statusLabel.setText("Displaying " + products.size() + " product(s)");
    }
    
    /**
     * Displays a single product in a dialog.
     */
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
        sb.append("Variant: ").append(product.getVariant()).append("\n");
        sb.append("Expiration: ").append(
            product.getExpirationDate() != null ? 
            product.getExpirationDate().toString() : "N/A"
        ).append("\n");
        sb.append("Status: ").append(getStatusString(product)).append("\n");
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this, textArea, 
            "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays low stock products with visual warning.
     */
    public void displayLowStockProducts(List<ProductDTO> products) {
        displayProducts(products);
        if (!products.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Warning: " + products.size() + " product(s) are low in stock!",
                "Low Stock Alert",
                JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No low stock products found.",
                "Low Stock Check",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Displays expired products with visual error indicator.
     */
    public void displayExpiredProducts(List<ProductDTO> products) {
        displayProducts(products);
        if (!products.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Alert: " + products.size() + " product(s) are expired!",
                "Expired Products Alert",
                JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No expired products found.",
                "Expiration Check",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Gets a status string for a product based on its flags.
     */
    private String getStatusString(ProductDTO product) {
        if (product.isExpired()) {
            return "EXPIRED";
        } else if (product.isLowStock()) {
            return "LOW STOCK";
        } else {
            return "OK";
        }
    }
}

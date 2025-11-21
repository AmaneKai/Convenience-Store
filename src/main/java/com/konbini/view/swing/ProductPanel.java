package com.konbini.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.konbini.controller.ProductManagementController;
import com.konbini.dto.ProductDTO;

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
        validateConstructorArgs(controller, backCallback);
        this.controller = controller;
        this.backCallback = backCallback;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center - Table
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // Bottom - Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> handleBackAction());
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);

        // Improve row height for better readability
        productTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(productTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("No products loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        buttonPanel.add(createActionButton("View All", this::handleViewAllProducts));
        buttonPanel.add(createActionButton("Add", this::handleAddProduct));
        buttonPanel.add(createActionButton("Update", this::handleUpdateProduct));
        buttonPanel.add(createActionButton("Remove", this::handleRemoveProduct));
        buttonPanel.add(createActionButton("Restock", this::handleRestockProduct));
        buttonPanel.add(createActionButton("Low Stock", this::handleViewLowStock));
        buttonPanel.add(createActionButton("Expired", this::handleViewExpired));
        buttonPanel.add(createActionButton("Search", this::handleSearchProducts));

        return buttonPanel;
    }

    // ==================== EVENT HANDLERS ====================

    private void handleBackAction() {
        try {
            backCallback.run();
        } catch (Exception e) {
            handleUIException(e, "navigation", "Navigation error. Please try again.");
        }
    }

    private void handleViewAllProducts() {
        try {
            controller.handleViewAllProducts();
        } catch (Exception e) {
            handleUIException(e, "viewing all products", "Failed to load products. Please try again.");
        }
    }

    private void handleAddProduct() {
        try {
            controller.handleAddProduct();
        } catch (Exception e) {
            handleUIException(e, "adding product", "Failed to add product. Please try again.");
        }
    }

    private void handleUpdateProduct() {
        try {
            controller.handleUpdateProduct();
        } catch (Exception e) {
            handleUIException(e, "updating product", "Failed to update product. Please try again.");
        }
    }

    private void handleRemoveProduct() {
        try {
            controller.handleRemoveProduct();
        } catch (Exception e) {
            handleUIException(e, "removing product", "Failed to remove product. Please try again.");
        }
    }

    private void handleRestockProduct() {
        try {
            controller.handleRestockProduct();
        } catch (Exception e) {
            handleUIException(e, "restocking product", "Failed to restock product. Please try again.");
        }
    }

    private void handleViewLowStock() {
        try {
            controller.handleViewLowStock();
        } catch (Exception e) {
            handleUIException(e, "viewing low stock products", "Failed to load low stock products. Please try again.");
        }
    }

    private void handleViewExpired() {
        try {
            controller.handleViewExpired();
        } catch (Exception e) {
            handleUIException(e, "viewing expired products", "Failed to load expired products. Please try again.");
        }
    }

    private void handleSearchProducts() {
        try {
            controller.handleSearchProducts();
        } catch (Exception e) {
            handleUIException(e, "searching products", "Search failed. Please try again.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    public void displayProducts(List<ProductDTO> products) {
        try {
            if (products == null || products.isEmpty()) {
                tableModel.setRowCount(0);
                updateStatus(products == null ? "No products available" : "No products found");
            } else {

                tableModel.setRowCount(0);
                int validCount = 0;

                for (ProductDTO product : products) {
                    if (isValidProduct(product)) {
                        tableModel.addRow(createTableRow(product));
                        validCount++;
                    }
                }

                // FIX: Automatically resize columns to fit the content (e.g. Price)
                resizeColumnWidth(productTable);

                if (validCount == 0) {
                    updateStatus("No valid products to display");
                } else {
                    updateStatus("Displaying " + validCount + " of " + products.size() + " product(s)");
                }
            }

        } catch (Exception e) {
            handleUIException(e, "displaying products", "Failed to display products.");
        }

    }

    public void displayProduct(ProductDTO product) {
        try {
            if (!isValidProduct(product)) {
                JOptionPane.showMessageDialog(this, "Invalid product data.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {

                StringBuilder sb = new StringBuilder();
                sb.append("Product Details\n");
                sb.append("================\n\n");
                sb.append("ID: ").append(product.getId()).append("\n");
                sb.append("Name: ").append(product.getName()).append("\n");
                sb.append("Price: ₱").append(String.format("%.2f", product.getPrice())).append("\n");
                sb.append("Quantity: ").append(product.getQuantity()).append("\n");
                sb.append("Category: ").append(product.getCategory()).append("\n");
                sb.append("Brand: ").append(product.getBrand() != null ? product.getBrand() : "N/A").append("\n");
                sb.append("Variant: ").append(product.getVariant() != null ? product.getVariant() : "N/A").append("\n");
                sb.append("Expiration: ").append(product.getExpirationDate() != null ? product.getExpirationDate() : "N/A").append("\n");
                sb.append("Status: ").append(getProductStatus(product)).append("\n");

                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JOptionPane.showMessageDialog(this, textArea, "Product Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            handleUIException(e, "displaying product details", "Failed to display product details.");
        }
    }

    public void displayLowStockProducts(List<ProductDTO> products) {
        try {
            if (products == null || products.isEmpty()) {
                updateStatus("No low stock products found");
                JOptionPane.showMessageDialog(this,
                        "No low stock products.",
                        "Low Stock",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {

                displayProducts(products);
                JOptionPane.showMessageDialog(this,
                        products.size() + " product(s) low in stock!",
                        "Low Stock Alert",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            handleUIException(e, "displaying low stock products", "Failed to display low stock products.");
        }
    }

    public void displayExpiredProducts(List<ProductDTO> products) {
        try {
            if (products == null || products.isEmpty()) {
                updateStatus("No expired products found");
                JOptionPane.showMessageDialog(this,
                        "No expired products.",
                        "Expired Products",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {

                displayProducts(products);
                JOptionPane.showMessageDialog(this,
                        products.size() + " product(s) expired!",
                        "Expired Alert",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            handleUIException(e, "displaying expired products", "Failed to display expired products.");
        }
    }

    // ==================== UTILITY METHODS ====================

    private JButton createActionButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                handleUIException(ex, "button action: " + text, "Action failed. Please try again.");
            }
        });
        return btn;
    }

    private Object[] createTableRow(ProductDTO product) {
        return new Object[] {
                product.getId(),
                product.getName(),
                "₱" + String.format("%.2f", product.getPrice()),
                product.getQuantity(),
                product.getCategory(),
                product.getBrand() != null ? product.getBrand() : "N/A",
                product.getVariant() != null ? product.getVariant() : "N/A",
                product.getExpirationDate() != null ? product.getExpirationDate() : "N/A",
                getProductStatus(product)
        };
    }

    private String getProductStatus(ProductDTO product) {
        if (product.isExpired()) {
            return "EXPIRED";
        } else if (product.isLowStock()) {
            return "LOW STOCK";
        } else {
            return "OK";
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }

            // Also check header width
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, columnModel.getColumn(column).getHeaderValue(), false, false, 0, 0);
            width = Math.max(width, headerComp.getPreferredSize().width + 10);

            // Apply width
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    // ==================== VALIDATION METHODS ====================

    private void validateConstructorArgs(ProductManagementController controller, Runnable backCallback) {
        if (controller == null) {
            throw new IllegalArgumentException("ProductManagementController cannot be null");
        }
        if (backCallback == null) {
            throw new IllegalArgumentException("Back callback cannot be null");
        }
    }

    private boolean isValidProduct(ProductDTO product) {
        return product != null &&
                product.getId() != null &&
                !product.getId().trim().isEmpty() &&
                product.getName() != null &&
                !product.getName().trim().isEmpty() &&
                product.getCategory() != null &&
                !product.getCategory().trim().isEmpty();
    }

    // ==================== ERROR HANDLING ====================

    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("ProductPanel Error " + context + ": " + e.getMessage());
        updateStatus("Error: " + userMessage);
        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
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

/**
 * ProductPanel provides a graphical user interface for product management operations.
 * This panel displays a table of products and provides buttons for various product-related
 * actions such as viewing, adding, updating, removing, and searching products.
 * It also includes specialized views for low stock and expired products.
 */
public class ProductPanel extends JPanel {
    /** Callback function to navigate back to the previous screen */
    private Runnable backCallback;

    /** Controller responsible for handling product management business logic */
    private ProductManagementController controller;

    /** Table component for displaying product data */
    private JTable productTable;

    /** Table model managing the data for the product table */
    private DefaultTableModel tableModel;

    /** Label for displaying status messages and operation results */
    private JLabel statusLabel;

    /** Column names for the product table */
    private static final String[] COLUMN_NAMES = {
            "ID", "Name", "Price", "Quantity", "Category", "Brand", "Variant", "Expiration", "Status"
    };

    /**
     * Constructs a new ProductPanel with the specified controller and navigation callback.
     * Initializes the UI components and sets up the panel layout.
     *
     * @param controller the ProductManagementController that handles business logic operations
     * @param backCallback a Runnable that executes when navigating back to the previous screen
     * @throws IllegalArgumentException if controller or backCallback parameters are null
     */
    public ProductPanel(ProductManagementController controller, Runnable backCallback) {
        validateConstructorArgs(controller, backCallback);
        this.controller = controller;
        this.backCallback = backCallback;
        initializeUI();
    }

    /**
     * Initializes the user interface components of the panel.
     * Sets up the main layout and creates the header, center, and button panels.
     */
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

    /**
     * Creates the header panel containing the title and back button.
     *
     * @return JPanel containing the header components
     */
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

    /**
     * Creates the center panel containing the product table and status label.
     * The table displays product information with automatic column resizing for better readability.
     *
     * @return JPanel containing the table and status components
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            /**
             * Prevents table cells from being editable by the user.
             *
             * @param row the row index of the cell
             * @param col the column index of the cell
             * @return false to make all cells non-editable
             */
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

    /**
     * Creates the button panel with all product management action buttons.
     *
     * @return JPanel containing the action buttons
     */
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

    /**
     * Handles the back navigation action.
     * Executes the back callback with exception handling.
     */
    private void handleBackAction() {
        try {
            backCallback.run();
        } catch (Exception e) {
            handleUIException(e, "navigation", "Navigation error. Please try again.");
        }
    }

    /**
     * Handles the view all products action.
     * Delegates to the controller to retrieve and display all products.
     */
    private void handleViewAllProducts() {
        try {
            controller.handleViewAllProducts();
        } catch (Exception e) {
            handleUIException(e, "viewing all products", "Failed to load products. Please try again.");
        }
    }

    /**
     * Handles adding a new product to the system.
     * Delegates to the controller to add a new product.
     */
    private void handleAddProduct() {
        try {
            controller.handleAddProduct();
        } catch (Exception e) {
            handleUIException(e, "adding product", "Failed to add product. Please try again.");
        }
    }

    /**
     * Handles updating existing product information.
     * Delegates to the controller to modify product data.
     */
    private void handleUpdateProduct() {
        try {
            controller.handleUpdateProduct();
        } catch (Exception e) {
            handleUIException(e, "updating product", "Failed to update product. Please try again.");
        }
    }

    /**
     * Handles product removal/deletion from the system.
     * Delegates to the controller to remove a product.
     */
    private void handleRemoveProduct() {
        try {
            controller.handleRemoveProduct();
        } catch (Exception e) {
            handleUIException(e, "removing product", "Failed to remove product. Please try again.");
        }
    }

    /**
     * Handles product restocking operations.
     * Delegates to the controller to restock product inventory.
     */
    private void handleRestockProduct() {
        try {
            controller.handleRestockProduct();
        } catch (Exception e) {
            handleUIException(e, "restocking product", "Failed to restock product. Please try again.");
        }
    }

    /**
     * Handles viewing low stock products.
     * Delegates to the controller to retrieve products with low inventory.
     */
    private void handleViewLowStock() {
        try {
            controller.handleViewLowStock();
        } catch (Exception e) {
            handleUIException(e, "viewing low stock products", "Failed to load low stock products. Please try again.");
        }
    }

    /**
     * Handles viewing expired products.
     * Delegates to the controller to retrieve expired products.
     */
    private void handleViewExpired() {
        try {
            controller.handleViewExpired();
        } catch (Exception e) {
            handleUIException(e, "viewing expired products", "Failed to load expired products. Please try again.");
        }
    }

    /**
     * Handles product search operations.
     * Delegates to the controller to search for products.
     */
    private void handleSearchProducts() {
        try {
            controller.handleSearchProducts();
        } catch (Exception e) {
            handleUIException(e, "searching products", "Search failed. Please try again.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    /**
     * Displays a list of products in the table.
     * Clears existing data and populates the table with the provided product list.
     * Automatically resizes columns to fit content and validates product data.
     *
     * @param products the list of ProductDTO objects to display
     */
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

    /**
     * Displays detailed information for a single product in a dialog.
     * Shows comprehensive product data including pricing, quantity, and status.
     *
     * @param product the ProductDTO object containing product details to display
     */
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

    /**
     * Displays low stock products with a warning alert.
     * Shows products with low inventory levels and displays a warning message.
     *
     * @param products the list of low stock ProductDTO objects to display
     */
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

    /**
     * Displays expired products with an error alert.
     * Shows products that have expired and displays an error message.
     *
     * @param products the list of expired ProductDTO objects to display
     */
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

    /**
     * Creates a standardized button with consistent styling and error handling.
     *
     * @param text the text to display on the button
     * @param action the Runnable to execute when the button is clicked
     * @return a configured JButton with the specified text and action
     */
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

    /**
     * Creates a table row from a ProductDTO object.
     * Formats the data for display in the product table.
     *
     * @param product the ProductDTO object to convert to a table row
     * @return an array of objects representing the table row data
     */
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

    /**
     * Determines the status of a product based on expiration and stock levels.
     *
     * @param product the ProductDTO object to check
     * @return a string representing the product status: "EXPIRED", "LOW STOCK", or "OK"
     */
    private String getProductStatus(ProductDTO product) {
        if (product.isExpired()) {
            return "EXPIRED";
        } else if (product.isLowStock()) {
            return "LOW STOCK";
        } else {
            return "OK";
        }
    }

    /**
     * Updates the status label with the specified message.
     *
     * @param message the message to display in the status label
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    /**
     * Automatically resizes table columns to fit their content.
     * Calculates the optimal width for each column based on both header and cell content.
     *
     * @param table the JTable to resize columns for
     */
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

    /**
     * Validates constructor arguments to ensure they are not null.
     *
     * @param controller the ProductManagementController to validate
     * @param backCallback the back callback to validate
     * @throws IllegalArgumentException if any argument is null
     */
    private void validateConstructorArgs(ProductManagementController controller, Runnable backCallback) {
        if (controller == null) {
            throw new IllegalArgumentException("ProductManagementController cannot be null");
        }
        if (backCallback == null) {
            throw new IllegalArgumentException("Back callback cannot be null");
        }
    }

    /**
     * Validates that a product object contains required data.
     *
     * @param product the ProductDTO object to validate
     * @return true if the product is valid, false otherwise
     */
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

    /**
     * Handles UI exceptions by logging the error and displaying a user-friendly message.
     *
     * @param e the exception that occurred
     * @param context a description of where the error occurred
     * @param userMessage the user-friendly message to display
     */
    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("ProductPanel Error " + context + ": " + e.getMessage());
        updateStatus("Error: " + userMessage);
        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
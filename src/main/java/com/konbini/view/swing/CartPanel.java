package com.konbini.view.swing;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.*;

import com.konbini.controller.CartManagementController;
import com.konbini.dto.CartDTO;
import com.konbini.dto.ProductDTO;
import com.konbini.dto.TransactionItemDTO;

public class CartPanel extends JPanel {
    private Runnable backCallback;
    private CartManagementController controller;

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel customerInfoLabel;
    private JLabel subtotalLabel;
    private JPanel productButtonPanel;
    private JScrollPane productScrollPane;
    private JComboBox<String> categoryComboBox;

    private Map<String, JSpinner> quantitySpinners;
    private List<ProductDTO> availableProducts;

    private static final String[] COLUMN_NAMES = { "Product ID", "Product Name", "Price", "Quantity", "Total" };
    private static final String ALL_CATEGORIES = "All Categories";

    public CartPanel(CartManagementController controller, Runnable backCallback) {
        if (controller == null) {
            throw new IllegalArgumentException("CartManagementController cannot be null");
        }
        if (backCallback == null) {
            throw new IllegalArgumentException("Back callback cannot be null");
        }
        this.controller = controller;
        this.backCallback = backCallback;
        this.quantitySpinners = new HashMap<>();
        initializeUI();
        loadAvailableProducts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center - Split between products and cart
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Left: Products panel
        JPanel productsPanel = createProductsPanel();
        centerPanel.add(productsPanel, BorderLayout.WEST);

        // Right: Cart info and table
        JPanel cartInfoPanel = createCartInfoPanel();
        centerPanel.add(cartInfoPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom - Action buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Shopping Cart & Checkout");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> backCallback.run());
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        buttonPanel.add(createButton("Create Cart", this::handleCreateCart));
        buttonPanel.add(createButton("View Cart", this::handleViewCart));
        buttonPanel.add(createButton("Remove Item", this::handleRemoveItem));
        buttonPanel.add(createButton("Clear Cart", this::handleClearCart));

        JButton checkoutBtn = createButton("CHECKOUT", this::handleCheckout);
        checkoutBtn.setBackground(new Color(39, 174, 96));
        checkoutBtn.setForeground(Color.BLACK);
        buttonPanel.add(checkoutBtn);

        return buttonPanel;
    }

    private JPanel createProductsPanel() {
        JPanel panelWrapper = new JPanel(new BorderLayout());
        panelWrapper.setBorder(BorderFactory.createTitledBorder("Available Products"));
        panelWrapper.setPreferredSize(new Dimension(350, 400));

        // Top: Category filter
        JPanel filterPanel = createFilterPanel();
        panelWrapper.add(filterPanel, BorderLayout.NORTH);

        // Center: Product list
        JPanel centerWrapper = createProductListPanel();
        panelWrapper.add(centerWrapper, BorderLayout.CENTER);

        return panelWrapper;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBackground(new Color(236, 240, 241));

        JLabel filterLabel = new JLabel("Filter by Category:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 11));
        filterPanel.add(filterLabel);

        categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem(ALL_CATEGORIES);
        categoryComboBox.setPreferredSize(new Dimension(150, 25));
        categoryComboBox.addActionListener(e -> refreshProductDisplay());
        filterPanel.add(categoryComboBox);

        return filterPanel;
    }

    private JPanel createProductListPanel() {
        JLabel titleLabel = new JLabel("Click to add items:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        productButtonPanel = new JPanel();
        productButtonPanel.setLayout(new BoxLayout(productButtonPanel, BoxLayout.Y_AXIS));
        productButtonPanel.setBackground(new Color(245, 245, 245));

        productScrollPane = new JScrollPane(productButtonPanel);
        productScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(titlePanel, BorderLayout.NORTH);
        centerWrapper.add(productScrollPane, BorderLayout.CENTER);

        return centerWrapper;
    }

    private JPanel createCartInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Info section
        JPanel infoPanel = createInfoPanel();
        panel.add(infoPanel, BorderLayout.NORTH);

        // Table
        JScrollPane scrollPane = createCartTable();
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(236, 240, 241));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.GRAY)));

        customerInfoLabel = new JLabel("No active cart");
        customerInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(customerInfoLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        subtotalLabel = new JLabel("Subtotal: ₱0.00");
        subtotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(subtotalLabel);

        return infoPanel;
    }

    private JScrollPane createCartTable() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(cartTable);
    }

    // ==================== EVENT HANDLERS ====================

    private void handleCreateCart() {
        try {
            controller.handleCreateCart();
        } catch (Exception e) {
            handleUIException(e, "creating cart", "Failed to create cart. Please try again.");
        }
    }

    private void handleViewCart() {
        try {
            controller.handleViewCart();
        } catch (Exception e) {
            handleUIException(e, "viewing cart", "Failed to view cart. Please try again.");
        }
    }

    private void handleRemoveItem() {
        try {
            controller.handleRemoveItem();
        } catch (Exception e) {
            handleUIException(e, "removing item", "Failed to remove item. Please try again.");
        }
    }

    private void handleClearCart() {
        try {
            controller.handleClearCart();
        } catch (Exception e) {
            handleUIException(e, "clearing cart", "Failed to clear cart. Please try again.");
        }
    }

    private void handleCheckout() {
        try {
            boolean success = controller.handleCheckout();
            if (success) {
                displayCart(null);
                SwingUtilities.invokeLater(() -> backCallback.run());
            }
        } catch (Exception e) {
            handleUIException(e, "processing checkout", "Failed to process checkout. Please try again.");
        }
    }

    private void handleProductAdd(ProductDTO product) {
        try {
            validateProductForAdd(product);

            JSpinner spinner = quantitySpinners.get(product.getId());
            int quantity = spinner != null ? (int) spinner.getValue() : 1;
            controller.handleAddItem(product, quantity);

        } catch (IllegalArgumentException e) {
            handleUIException(e, "adding product", "Invalid product selection: " + getSafeErrorMessage(e));
        } catch (Exception e) {
            handleUIException(e, "adding product", "Failed to add product to cart. Please try again.");
        }
    }

    // ==================== VALIDATION METHODS ====================

    private void validateProductForAdd(ProductDTO product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.isExpired()) {
            throw new IllegalArgumentException("Cannot add expired product: " + product.getName());
        }
        if (product.getQuantity() <= 0) {
            throw new IllegalArgumentException("Product out of stock: " + product.getName());
        }
    }

    // ==================== UI HELPER METHODS ====================

    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                handleUIException(ex, "button action", "Action failed. Please try again.");
            }
        });
        return btn;
    }

    private JSpinner createButtonOnlySpinner(SpinnerNumberModel model) {
        JSpinner spinner = new JSpinner(model);

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setEditable(false);
            textField.setFocusable(false);
        }

        return spinner;
    }

    private JPanel createProductButtonRow(ProductDTO product) {
        JPanel rowPanel = new JPanel(new BorderLayout(5, 2));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));

        // Product details panel
        JPanel detailsPanel = createProductDetailsPanel(product);
        rowPanel.add(detailsPanel, BorderLayout.CENTER);

        // Right side: quantity and add button
        JPanel actionPanel = createProductActionPanel(product);
        rowPanel.add(actionPanel, BorderLayout.EAST);

        return rowPanel;
    }

    private JPanel createProductDetailsPanel(ProductDTO product) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(Color.BLACK);
        detailsPanel.add(nameLabel);

        JLabel brandPriceLabel = new JLabel(String.format("Brand: %s | ₱%.2f",
                product.getBrand() != null ? product.getBrand() : "N/A",
                product.getPrice()));
        brandPriceLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        brandPriceLabel.setForeground(new Color(100, 100, 100));
        detailsPanel.add(brandPriceLabel);

        return detailsPanel;
    }

    private JPanel createProductActionPanel(ProductDTO product) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        actionPanel.setBackground(Color.WHITE);

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, product.getQuantity(), 1);
        JSpinner quantitySpinner = createButtonOnlySpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(50, 25));
        quantitySpinners.put(product.getId(), quantitySpinner);
        actionPanel.add(quantitySpinner);

        JButton addBtn = new JButton("Add");
        addBtn.setPreferredSize(new Dimension(55, 25));
        addBtn.setBackground(new Color(39, 174, 96));
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("Arial", Font.BOLD, 10));
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> handleProductAdd(product));
        actionPanel.add(addBtn);

        return actionPanel;
    }

    private JLabel createCategoryHeader(String categoryName) {
        JLabel header = new JLabel("  " + categoryName);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.BLACK);
        header.setOpaque(true);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return header;
    }

    // ==================== ERROR HANDLING METHODS ====================

    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("UI Error " + context + ": " + e.getMessage());
        JOptionPane.showMessageDialog(this,
                userMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
    private String getSafeErrorMessage(Exception e) {
        String temp = "Please check your input and try again.";

        if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
            temp = e.getMessage();
        }

        return temp;
    }
    private void loadAvailableProducts() {
        try {
            controller.loadAvailableProducts();
        } catch (Exception e) {
            handleUIException(e, "loading products", "Failed to load products. Please restart the application.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    private void refreshProductDisplay() {
        try {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            productButtonPanel.removeAll();

            if (selectedCategory == null || selectedCategory.equals(ALL_CATEGORIES)) {
                displayAllProducts();
            } else {
                displayProductsByCategory(selectedCategory);
            }

            productButtonPanel.revalidate();
            productButtonPanel.repaint();
        } catch (Exception e) {
            handleUIException(e, "refreshing product display", "Failed to refresh product list.");
        }
    }

    private void displayAllProducts() {
        if (availableProducts == null || availableProducts.isEmpty()) {
            JLabel noProductsLabel = new JLabel("No products available");
            productButtonPanel.add(noProductsLabel);
        } else {
            Map<String, Map<String, List<ProductDTO>>> grouped = availableProducts.stream()
                    .filter(p -> !p.isExpired() && p.getQuantity() > 0)
                    .collect(Collectors.groupingBy(
                            ProductDTO::getCategory,
                            Collectors.groupingBy(ProductDTO::getVariant)));

            for (String category : grouped.keySet()) {
                productButtonPanel.add(createCategoryHeader(category));

                Map<String, List<ProductDTO>> subcategories = grouped.get(category);
                for (String subcategory : subcategories.keySet()) {
                    JLabel subHeader = new JLabel("    ◦ " + subcategory);
                    subHeader.setFont(new Font("Arial", Font.PLAIN, 11));
                    subHeader.setForeground(Color.DARK_GRAY);
                    productButtonPanel.add(subHeader);

                    for (ProductDTO product : subcategories.get(subcategory)) {
                        productButtonPanel.add(createProductButtonRow(product));
                    }
                }
                productButtonPanel.add(Box.createVerticalStrut(5));
            }
        }
    }

    private void displayProductsByCategory(String category) {
        List<ProductDTO> filteredProducts = availableProducts.stream()
                .filter(p -> p.getCategory().equals(category) && !p.isExpired() && p.getQuantity() > 0)
                .collect(Collectors.toList());

        if (filteredProducts.isEmpty()) {
            JLabel noProductsLabel = new JLabel("No products in this category");
            productButtonPanel.add(noProductsLabel);
        } else {
            Map<String, List<ProductDTO>> grouped = filteredProducts.stream()
                    .collect(Collectors.groupingBy(ProductDTO::getVariant));

            for (String subcategory : grouped.keySet()) {
                JLabel subHeader = new JLabel("◦ " + subcategory);
                subHeader.setFont(new Font("Arial", Font.BOLD, 12));
                subHeader.setForeground(new Color(52, 152, 219));
                productButtonPanel.add(subHeader);

                for (ProductDTO product : grouped.get(subcategory)) {
                    productButtonPanel.add(createProductButtonRow(product));
                }
                productButtonPanel.add(Box.createVerticalStrut(3));
            }
        }
    }

    public void displayCart(CartDTO cart) {
        try {
            if (cart == null) {
                customerInfoLabel.setText("No active cart");
                subtotalLabel.setText("Subtotal: ₱0.00");
                tableModel.setRowCount(0);
            } else {

                StringBuilder customerInfo = new StringBuilder();
                customerInfo.append("Customer: ").append(cart.getCustomerName());
                customerInfo.append(" (ID: ").append(cart.getCustomerId()).append(")");
                if (cart.isCustomerIsSeniorCitizen())
                    customerInfo.append(" [SENIOR]");
                if (cart.isCustomerHasMembershipCard())
                    customerInfo.append(" [MEMBER: ").append(cart.getCustomerPoints()).append(" pts]");

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
        } catch (Exception e) {
            handleUIException(e, "displaying cart", "Failed to display cart information.");
        }
    }

    public void displayProducts(List<ProductDTO> products) {
        try {
            this.availableProducts = products;

            Set<String> categories = new HashSet<>();
            if (products != null && !products.isEmpty()) {
                categories = products.stream()
                        .filter(p -> !p.isExpired() && p.getQuantity() > 0)
                        .map(ProductDTO::getCategory)
                        .collect(Collectors.toSet());
            }

            categoryComboBox.removeAllItems();
            categoryComboBox.addItem(ALL_CATEGORIES);
            categories.forEach(cat -> categoryComboBox.addItem(cat));

            refreshProductDisplay();
        } catch (Exception e) {
            handleUIException(e, "displaying products", "Failed to display product list.");
        }
    }

    // ==================== CLEANUP METHOD ====================

    public void cleanup() {
        if (quantitySpinners != null) {
            quantitySpinners.clear();
        }
    }
}
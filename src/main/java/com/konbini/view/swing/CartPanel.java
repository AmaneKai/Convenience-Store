package com.konbini.view.swing;

import com.konbini.dto.CartDTO;
import com.konbini.dto.TransactionItemDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing shopping cart operations and checkout process.
 * Displays cart items in a table with customer information and totals.
 */
public class CartPanel extends JPanel {
    private Runnable backCallback;
    private int selectedChoice = -1;
    private final Object choiceLock = new Object();
    
    // UI Components
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel customerInfoLabel;
    private JLabel subtotalLabel;
    private JPanel infoPanel;
    
    // Table columns
    private static final String[] COLUMN_NAMES = {
        "Product ID", "Product Name", "Price", "Quantity", "Total"
    };
    
    /**
     * Constructs the cart panel with a back navigation callback.
     */
    public CartPanel(Runnable backCallback) {
        this.backCallback = backCallback;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel("Shopping Cart & Checkout");
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Split into info panel and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Info panel at top
        infoPanel = createInfoPanel();
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Table in center
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Product ID
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Name
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Price
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Quantity
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Total
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Action buttons
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Creates the info panel showing customer and cart summary.
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.GRAY)
        ));
        
        customerInfoLabel = new JLabel("No active cart");
        customerInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(customerInfoLabel);
        
        panel.add(Box.createVerticalStrut(10));
        
        subtotalLabel = new JLabel("Subtotal: ₱0.00");
        subtotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(subtotalLabel);
        
        return panel;
    }
    
    /**
     * Creates the header panel.
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
     * Creates the button panel.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        String[] buttonLabels = {
            "Create Cart", "View Cart", "Add Item",
            "Remove Item", "Update Quantity", "Clear Cart",
            "Checkout", "Back"
        };
        
        for (int i = 0; i < buttonLabels.length; i++) {
            final int choice = i + 1;
            JButton btn = new JButton(buttonLabels[i]);
            btn.setPreferredSize(new Dimension(180, 40));
            
            // Special styling for checkout button
            if (i == 6) {
                btn.setBackground(new Color(39, 174, 96));
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Arial", Font.BOLD, 14));
            }
            
            btn.addActionListener(e -> handleMenuChoice(choice == 8 ? 0 : choice));
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
     * Gets the user's menu choice.
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
     * Displays the cart with all items and customer information.
     */
    public void displayCart(CartDTO cart) {
        // Update customer info
        StringBuilder customerInfo = new StringBuilder();
        customerInfo.append("Customer: ").append(cart.getCustomerName());
        customerInfo.append(" (ID: ").append(cart.getCustomerId()).append(")");
        
        if (cart.isCustomerIsSeniorCitizen()) {
            customerInfo.append(" [SENIOR CITIZEN]");
        }
        if (cart.isCustomerHasMembershipCard()) {
            customerInfo.append(" [MEMBER - ").append(cart.getCustomerPoints()).append(" pts]");
        }
        
        customerInfoLabel.setText(customerInfo.toString());
        
        // Update subtotal
        subtotalLabel.setText(String.format("Subtotal: ₱%.2f", cart.getSubtotal()));
        
        // Clear and populate table
        tableModel.setRowCount(0);
        
        for (TransactionItemDTO item : cart.getItems()) {
            Object[] row = {
                item.getProductId(),
                item.getProductName(),
                String.format("₱%.2f", item.getUnitPrice()),
                item.getQuantity(),
                String.format("₱%.2f", item.getTotalPrice())
            };
            tableModel.addRow(row);
        }
        
        // Show empty message if cart is empty
        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Cart is empty. Add items to proceed with checkout.",
                "Empty Cart",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

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

import com.konbini.controller.CustomerManagementController;
import com.konbini.dto.CustomerDTO;

public class CustomerPanel extends JPanel {
    private Runnable backCallback;
    private CustomerManagementController controller;

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private static final String[] COLUMN_NAMES = {"ID", "Name", "Senior", "Membership", "Points"};

    public CustomerPanel(CustomerManagementController controller, Runnable backCallback) {
        if (controller == null) {
            throw new IllegalArgumentException("CustomerManagementController cannot be null");
        }
        if (backCallback == null) {
            throw new IllegalArgumentException("Back callback cannot be null");
        }
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

        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK); // Consistent with other panels
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> handleBackAction());
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("No customers loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        buttonPanel.add(createButton("View All", this::handleViewAllCustomers));
        buttonPanel.add(createButton("View Details", this::handleViewCustomerDetails));
        buttonPanel.add(createButton("Register", this::handleRegisterCustomer));
        buttonPanel.add(createButton("Register w/ Member", this::handleRegisterWithMembership));
        buttonPanel.add(createButton("Update", this::handleUpdateCustomer));
        buttonPanel.add(createButton("Remove", this::handleRemoveCustomer));
        buttonPanel.add(createButton("Add Card", this::handleAddMembershipCard));

        return buttonPanel;
    }

    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                handleUIException(ex, "button action: " + text, "Action failed. Please try again.");
            }
        });
        return btn;
    }

    // ==================== EVENT HANDLERS ====================

    private void handleBackAction() {
        try {
            backCallback.run();
        } catch (Exception e) {
            handleUIException(e, "navigation", "Navigation error. Please try again.");
        }
    }

    private void handleViewAllCustomers() {
        try {
            controller.handleViewAllCustomers();
        } catch (Exception e) {
            handleUIException(e, "viewing all customers", "Failed to load customers. Please try again.");
        }
    }

    private void handleViewCustomerDetails() {
        try {
            controller.handleViewCustomerDetails();
        } catch (Exception e) {
            handleUIException(e, "viewing customer details", "Failed to view customer details. Please try again.");
        }
    }

    private void handleRegisterCustomer() {
        try {
            controller.handleRegisterCustomer();
        } catch (Exception e) {
            handleUIException(e, "registering customer", "Failed to register customer. Please try again.");
        }
    }

    private void handleRegisterWithMembership() {
        try {
            controller.handleRegisterWithMembership();
        } catch (Exception e) {
            handleUIException(e, "registering customer with membership", "Failed to register customer with membership. Please try again.");
        }
    }

    private void handleUpdateCustomer() {
        try {
            controller.handleUpdateCustomer();
        } catch (Exception e) {
            handleUIException(e, "updating customer", "Failed to update customer. Please try again.");
        }
    }

    private void handleRemoveCustomer() {
        try {
            controller.handleRemoveCustomer();
        } catch (Exception e) {
            handleUIException(e, "removing customer", "Failed to remove customer. Please try again.");
        }
    }

    private void handleAddMembershipCard() {
        try {
            controller.handleAddMembershipCard();
        } catch (Exception e) {
            handleUIException(e, "adding membership card", "Failed to add membership card. Please try again.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    public void displayCustomers(List<CustomerDTO> customers) {
        try {
            validateCustomerList(customers);

            tableModel.setRowCount(0);
            for (CustomerDTO customer : customers) {
                validateCustomer(customer);
                tableModel.addRow(new Object[]{
                        customer.getId(),
                        customer.getName(),
                        customer.isSeniorCitizen() ? "Yes" : "No",
                        customer.isHasMembershipCard() ? "Yes" : "No",
                        customer.isHasMembershipCard() ? customer.getPoints() : "N/A"
                });
            }
            updateStatus("Displaying " + customers.size() + " customer(s)");

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying customers", "Invalid customer data received.");
        } catch (Exception e) {
            handleUIException(e, "displaying customers", "Failed to display customers.");
        }
    }

    public void displayCustomer(CustomerDTO customer) {
        try {
            validateCustomer(customer);

            StringBuilder sb = new StringBuilder();
            sb.append("Customer Details\n");
            sb.append("================\n\n");
            sb.append("ID: ").append(customer.getId()).append("\n");
            sb.append("Name: ").append(customer.getName()).append("\n");
            sb.append("Senior Citizen: ").append(customer.isSeniorCitizen() ? "Yes" : "No").append("\n");
            sb.append("Membership: ").append(customer.isHasMembershipCard() ? "Yes" : "No").append("\n");

            if (customer.isHasMembershipCard()) {
                sb.append("Card Number: ").append(customer.getCardNumber() != null ? customer.getCardNumber() : "N/A").append("\n");
                sb.append("Points: ").append(customer.getPoints()).append("\n");
                sb.append("Expiry Date: ").append(customer.getCardExpiryDate() != null ? customer.getCardExpiryDate() : "N/A").append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JOptionPane.showMessageDialog(this,
                    new JScrollPane(textArea), // Use scroll pane for long content
                    "Customer Details",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying customer details", "Invalid customer data.");
        } catch (Exception e) {
            handleUIException(e, "displaying customer details", "Failed to display customer details.");
        }
    }

    public void displayErrorMessage(String message) {
        updateStatus("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void displaySuccessMessage(String message) {
        updateStatus("Success: " + message);
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void displayInfoMessage(String message) {
        updateStatus("Info: " + message);
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== VALIDATION METHODS ====================

    private void validateCustomerList(List<CustomerDTO> customers) {
        if (customers == null) {
            throw new IllegalArgumentException("Customer list cannot be null");
        }
    }

    private void validateCustomer(CustomerDTO customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customer.getId() == null || customer.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
    }

    // ==================== UTILITY METHODS ====================

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("UI Error " + context + ": " + e.getMessage());
        updateStatus("Error: " + userMessage);
        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ==================== CLEANUP METHOD ====================

    public void cleanup() {
        // Clean up any resources if needed
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }
        updateStatus("Ready");
    }
}
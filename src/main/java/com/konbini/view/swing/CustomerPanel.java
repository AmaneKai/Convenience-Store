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

/**
 * CustomerPanel provides a graphical user interface for customer management operations.
 * This panel displays a table of customers and provides buttons for various customer-related
 * actions such as viewing details, registration, updates, and membership management.
 *
 * The panel features a clean layout with a header, customer table, and action buttons,
 * and includes comprehensive error handling and validation.
 */
public class CustomerPanel extends JPanel {
    /** Callback function to navigate back to the previous screen */
    private Runnable backCallback;

    /** Controller responsible for handling customer management business logic */
    private CustomerManagementController controller;

    /** Table component for displaying customer data */
    private JTable customerTable;

    /** Table model managing the data for the customer table */
    private DefaultTableModel tableModel;

    /** Label for displaying status messages and operation results */
    private JLabel statusLabel;

    /** Column names for the customer table */
    private static final String[] COLUMN_NAMES = {"ID", "Name", "Senior", "Membership", "Points"};

    /**
     * Constructs a new CustomerPanel with the specified controller and navigation callback.
     * Initializes the UI components and sets up the panel layout.
     *
     * @param controller the CustomerManagementController that handles business logic operations
     * @param backCallback a Runnable that executes when navigating back to the previous screen
     * @throws IllegalArgumentException if controller or backCallback parameters are null
     */
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

        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK); // Consistent with other panels
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> handleBackAction());
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates the center panel containing the customer table and status label.
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

    /**
     * Creates the button panel with all customer management action buttons.
     *
     * @return JPanel containing the action buttons
     */
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

    /**
     * Creates a standardized button with consistent styling and error handling.
     *
     * @param text the text to display on the button
     * @param action the Runnable to execute when the button is clicked
     * @return a configured JButton with the specified text and action
     */
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
     * Handles the view all customers action.
     * Delegates to the controller to retrieve and display all customers.
     */
    private void handleViewAllCustomers() {
        try {
            controller.handleViewAllCustomers();
        } catch (Exception e) {
            handleUIException(e, "viewing all customers", "Failed to load customers. Please try again.");
        }
    }

    /**
     * Handles viewing detailed information for a specific customer.
     * Delegates to the controller to show customer details.
     */
    private void handleViewCustomerDetails() {
        try {
            controller.handleViewCustomerDetails();
        } catch (Exception e) {
            handleUIException(e, "viewing customer details", "Failed to view customer details. Please try again.");
        }
    }

    /**
     * Handles customer registration without membership.
     * Delegates to the controller to register a new customer.
     */
    private void handleRegisterCustomer() {
        try {
            controller.handleRegisterCustomer();
        } catch (Exception e) {
            handleUIException(e, "registering customer", "Failed to register customer. Please try again.");
        }
    }

    /**
     * Handles customer registration with membership.
     * Delegates to the controller to register a new customer with membership benefits.
     */
    private void handleRegisterWithMembership() {
        try {
            controller.handleRegisterWithMembership();
        } catch (Exception e) {
            handleUIException(e, "registering customer with membership", "Failed to register customer with membership. Please try again.");
        }
    }

    /**
     * Handles updating existing customer information.
     * Delegates to the controller to modify customer data.
     */
    private void handleUpdateCustomer() {
        try {
            controller.handleUpdateCustomer();
        } catch (Exception e) {
            handleUIException(e, "updating customer", "Failed to update customer. Please try again.");
        }
    }

    /**
     * Handles customer removal/deletion.
     * Delegates to the controller to remove a customer from the system.
     */
    private void handleRemoveCustomer() {
        try {
            controller.handleRemoveCustomer();
        } catch (Exception e) {
            handleUIException(e, "removing customer", "Failed to remove customer. Please try again.");
        }
    }

    /**
     * Handles adding a membership card to an existing customer.
     * Delegates to the controller to add membership benefits.
     */
    private void handleAddMembershipCard() {
        try {
            controller.handleAddMembershipCard();
        } catch (Exception e) {
            handleUIException(e, "adding membership card", "Failed to add membership card. Please try again.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    /**
     * Displays a list of customers in the table.
     * Clears existing data and populates the table with the provided customer list.
     *
     * @param customers the list of CustomerDTO objects to display
     * @throws IllegalArgumentException if the customer list is null or contains invalid data
     */
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

    /**
     * Displays detailed information for a single customer in a dialog.
     * Shows comprehensive customer data including membership information if available.
     *
     * @param customer the CustomerDTO object containing customer details to display
     * @throws IllegalArgumentException if the customer data is invalid
     */
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

    /**
     * Displays an error message in both the status label and a dialog.
     *
     * @param message the error message to display
     */
    public void displayErrorMessage(String message) {
        updateStatus("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a success message in both the status label and a dialog.
     *
     * @param message the success message to display
     */
    public void displaySuccessMessage(String message) {
        updateStatus("Success: " + message);
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an informational message in both the status label and a dialog.
     *
     * @param message the informational message to display
     */
    public void displayInfoMessage(String message) {
        updateStatus("Info: " + message);
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================== VALIDATION METHODS ====================

    /**
     * Validates that the customer list is not null.
     *
     * @param customers the list of customers to validate
     * @throws IllegalArgumentException if the customer list is null
     */
    private void validateCustomerList(List<CustomerDTO> customers) {
        if (customers == null) {
            throw new IllegalArgumentException("Customer list cannot be null");
        }
    }

    /**
     * Validates that a customer object contains required data.
     *
     * @param customer the CustomerDTO object to validate
     * @throws IllegalArgumentException if the customer is null or has invalid data
     */
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
     * Handles UI exceptions by logging the error and displaying a user-friendly message.
     *
     * @param e the exception that occurred
     * @param context a description of where the error occurred
     * @param userMessage the user-friendly message to display
     */
    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("UI Error " + context + ": " + e.getMessage());
        updateStatus("Error: " + userMessage);
        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ==================== CLEANUP METHOD ====================

    /**
     * Cleans up resources and resets the panel state.
     * Clears the table data and resets the status message.
     */
    public void cleanup() {
        // Clean up any resources if needed
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }
        updateStatus("Ready");
    }
}
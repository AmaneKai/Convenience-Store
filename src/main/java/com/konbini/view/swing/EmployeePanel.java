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

import com.konbini.controller.EmployeeManagementController;
import com.konbini.dto.EmployeeDTO;

/**
 * EmployeePanel provides a graphical user interface for employee management operations.
 * This panel displays a table of employees and provides buttons for various employee-related
 * actions such as viewing details, adding new employees, updates, and password management.
 */
public class EmployeePanel extends JPanel {
    /** Callback function to navigate back to the previous screen */
    private Runnable backCallback;

    /** Controller responsible for handling employee management business logic */
    private EmployeeManagementController controller;

    /** Table component for displaying employee data */
    private JTable employeeTable;

    /** Table model managing the data for the employee table */
    private DefaultTableModel tableModel;

    /** Label for displaying status messages and operation results */
    private JLabel statusLabel;

    /** Column names for the employee table */
    private static final String[] COLUMN_NAMES = {"ID", "Name"};

    /**
     * Constructs a new EmployeePanel with the specified controller and navigation callback.
     * Initializes the UI components and sets up the panel layout.
     *
     * @param controller the EmployeeManagementController that handles business logic operations
     * @param backCallback a Runnable that executes when navigating back to the previous screen
     * @throws IllegalArgumentException if controller or backCallback parameters are null
     */
    public EmployeePanel(EmployeeManagementController controller, Runnable backCallback) {
        if (controller == null) {
            throw new IllegalArgumentException("EmployeeManagementController cannot be null");
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

        JLabel titleLabel = new JLabel("Employee Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> handleBackAction());
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Creates the center panel containing the employee table and status label.
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
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("No employees loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        return centerPanel;
    }

    /**
     * Creates the button panel with all employee management action buttons.
     *
     * @return JPanel containing the action buttons
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        buttonPanel.add(createButton("View All", this::handleViewAllEmployees));
        buttonPanel.add(createButton("View Details", this::handleViewEmployeeDetails));
        buttonPanel.add(createButton("Add Employee", this::handleAddEmployee));
        buttonPanel.add(createButton("Update", this::handleUpdateEmployee));
        buttonPanel.add(createButton("Remove", this::handleRemoveEmployee));
        buttonPanel.add(createButton("Change Password", this::handleChangePassword));

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
     * Handles the view all employees action.
     * Delegates to the controller to retrieve and display all employees.
     */
    private void handleViewAllEmployees() {
        try {
            controller.handleViewAllEmployees();
        } catch (Exception e) {
            handleUIException(e, "viewing all employees", "Failed to load employees. Please try again.");
        }
    }

    /**
     * Handles viewing detailed information for a specific employee.
     * Delegates to the controller to show employee details.
     */
    private void handleViewEmployeeDetails() {
        try {
            controller.handleViewEmployeeDetails();
        } catch (Exception e) {
            handleUIException(e, "viewing employee details", "Failed to view employee details. Please try again.");
        }
    }

    /**
     * Handles adding a new employee to the system.
     * Delegates to the controller to register a new employee.
     */
    private void handleAddEmployee() {
        try {
            controller.handleAddEmployee();
        } catch (Exception e) {
            handleUIException(e, "adding employee", "Failed to add employee. Please try again.");
        }
    }

    /**
     * Handles updating existing employee information.
     * Delegates to the controller to modify employee data.
     */
    private void handleUpdateEmployee() {
        try {
            controller.handleUpdateEmployee();
        } catch (Exception e) {
            handleUIException(e, "updating employee", "Failed to update employee. Please try again.");
        }
    }

    /**
     * Handles employee removal/deletion from the system.
     * Delegates to the controller to remove an employee.
     */
    private void handleRemoveEmployee() {
        try {
            controller.handleRemoveEmployee();
        } catch (Exception e) {
            handleUIException(e, "removing employee", "Failed to remove employee. Please try again.");
        }
    }

    /**
     * Handles changing an employee's password.
     * Delegates to the controller to update password credentials.
     */
    private void handleChangePassword() {
        try {
            controller.handleChangePassword();
        } catch (Exception e) {
            handleUIException(e, "changing password", "Failed to change password. Please try again.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    /**
     * Displays a list of employees in the table.
     * Clears existing data and populates the table with the provided employee list.
     *
     * @param employees the list of EmployeeDTO objects to display
     * @throws IllegalArgumentException if the employee list is null or contains invalid data
     */
    public void displayEmployees(List<EmployeeDTO> employees) {
        try {
            validateEmployeeList(employees);

            tableModel.setRowCount(0);
            for (EmployeeDTO employee : employees) {
                validateEmployee(employee);
                tableModel.addRow(new Object[]{
                        employee.getId(),
                        employee.getName()
                });
            }
            updateStatus("Displaying " + employees.size() + " employee(s)");

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying employees", "Invalid employee data received.");
        } catch (Exception e) {
            handleUIException(e, "displaying employees", "Failed to display employees.");
        }
    }

    /**
     * Displays detailed information for a single employee in a dialog.
     * Shows comprehensive employee data while masking sensitive password information.
     *
     * @param employee the EmployeeDTO object containing employee details to display
     * @throws IllegalArgumentException if the employee data is invalid
     */
    public void displayEmployee(EmployeeDTO employee) {
        try {
            validateEmployee(employee);

            StringBuilder sb = new StringBuilder();
            sb.append("Employee Details\n");
            sb.append("================\n\n");
            sb.append("ID: ").append(employee.getId()).append("\n");
            sb.append("Name: ").append(employee.getName()).append("\n");
            sb.append("Password: ").append("********").append("\n");

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JOptionPane.showMessageDialog(this,
                    new JScrollPane(textArea),
                    "Employee Details",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying employee details", "Invalid employee data.");
        } catch (Exception e) {
            handleUIException(e, "displaying employee details", "Failed to display employee details.");
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
     * Validates that the employee list is not null.
     *
     * @param employees the list of employees to validate
     * @throws IllegalArgumentException if the employee list is null
     */
    private void validateEmployeeList(List<EmployeeDTO> employees) {
        if (employees == null) {
            throw new IllegalArgumentException("Employee list cannot be null");
        }
    }

    /**
     * Validates that an employee object contains required data.
     *
     * @param employee the EmployeeDTO object to validate
     * @throws IllegalArgumentException if the employee is null or has invalid data
     */
    private void validateEmployee(EmployeeDTO employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (employee.getId() == null || employee.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
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
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }
        updateStatus("Ready");
    }
}
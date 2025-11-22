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

public class EmployeePanel extends JPanel {
    private Runnable backCallback;
    private EmployeeManagementController controller;

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private static final String[] COLUMN_NAMES = {"ID", "Name"};

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

        JLabel titleLabel = new JLabel("Employee Management");
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

    private void handleViewAllEmployees() {
        try {
            controller.handleViewAllEmployees();
        } catch (Exception e) {
            handleUIException(e, "viewing all employees", "Failed to load employees. Please try again.");
        }
    }

    private void handleViewEmployeeDetails() {
        try {
            controller.handleViewEmployeeDetails();
        } catch (Exception e) {
            handleUIException(e, "viewing employee details", "Failed to view employee details. Please try again.");
        }
    }

    private void handleAddEmployee() {
        try {
            controller.handleAddEmployee();
        } catch (Exception e) {
            handleUIException(e, "adding employee", "Failed to add employee. Please try again.");
        }
    }

    private void handleUpdateEmployee() {
        try {
            controller.handleUpdateEmployee();
        } catch (Exception e) {
            handleUIException(e, "updating employee", "Failed to update employee. Please try again.");
        }
    }

    private void handleRemoveEmployee() {
        try {
            controller.handleRemoveEmployee();
        } catch (Exception e) {
            handleUIException(e, "removing employee", "Failed to remove employee. Please try again.");
        }
    }

    private void handleChangePassword() {
        try {
            controller.handleChangePassword();
        } catch (Exception e) {
            handleUIException(e, "changing password", "Failed to change password. Please try again.");
        }
    }

    // ==================== DISPLAY METHODS ====================

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

    private void validateEmployeeList(List<EmployeeDTO> employees) {
        if (employees == null) {
            throw new IllegalArgumentException("Employee list cannot be null");
        }
    }

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
        if (tableModel != null) {
            tableModel.setRowCount(0);
        }
        updateStatus("Ready");
    }
}
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
        JLabel titleLabel = new JLabel("Customer Management");
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
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        statusLabel = new JLabel("No customers loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(createButton("View All", () -> controller.handleViewAllCustomers()));
        buttonPanel.add(createButton("View Details", () -> controller.handleViewCustomerDetails()));
        buttonPanel.add(createButton("Register", () -> controller.handleRegisterCustomer()));
        buttonPanel.add(createButton("Register w/ Member", () -> controller.handleRegisterWithMembership()));
        buttonPanel.add(createButton("Update", () -> controller.handleUpdateCustomer()));
        buttonPanel.add(createButton("Remove", () -> controller.handleRemoveCustomer()));
        buttonPanel.add(createButton("Add Card", () -> controller.handleAddMembershipCard()));
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    public void displayCustomers(List<CustomerDTO> customers) {
        tableModel.setRowCount(0);
        for (CustomerDTO c : customers) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getName(),
                c.isSeniorCitizen() ? "Yes" : "No",
                c.isHasMembershipCard() ? "Yes" : "No",
                c.isHasMembershipCard() ? c.getPoints() : "N/A"
            });
        }
        statusLabel.setText("Displaying " + customers.size() + " customer(s)");
    }
    
    public void displayCustomer(CustomerDTO customer) {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Details\n");
        sb.append("================\n\n");
        sb.append("ID: ").append(customer.getId()).append("\n");
        sb.append("Name: ").append(customer.getName()).append("\n");
        sb.append("Senior: ").append(customer.isSeniorCitizen() ? "Yes" : "No").append("\n");
        sb.append("Membership: ").append(customer.isHasMembershipCard() ? "Yes" : "No").append("\n");
        if (customer.isHasMembershipCard()) {
            sb.append("Card #: ").append(customer.getCardNumber()).append("\n");
            sb.append("Points: ").append(customer.getPoints()).append("\n");
            sb.append("Expires: ").append(customer.getCardExpiryDate()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, textArea, "Customer Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
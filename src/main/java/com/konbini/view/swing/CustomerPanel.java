package com.konbini.view.swing;

import com.konbini.dto.CustomerDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing and displaying customer information.
 * Uses JTable for displaying customer lists with membership card details.
 */
public class CustomerPanel extends JPanel {
    private Runnable backCallback;
    private int selectedChoice = -1;
    private final Object choiceLock = new Object();
    
    // UI Components
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    // Table columns
    private static final String[] COLUMN_NAMES = {
        "ID", "Name", "Senior Citizen", "Membership Card", "Points"
    };
    
    /**
     * Constructs the customer panel with a back navigation callback.
     */
    public CustomerPanel(Runnable backCallback) {
        this.backCallback = backCallback;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel("Customer Management");
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Create table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(100); // ID
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Name
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Senior
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Card
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Points
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel("No customers loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Action buttons
        add(createButtonPanel(), BorderLayout.SOUTH);
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
            "View All Customers", "Add Customer", "Update Customer",
            "Remove Customer", "Issue Membership Card", "Add Points",
            "View Customer Details", "Back"
        };
        
        for (int i = 0; i < buttonLabels.length; i++) {
            final int choice = i + 1;
            JButton btn = new JButton(buttonLabels[i]);
            btn.setPreferredSize(new Dimension(200, 40));
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
     * Displays a list of customers in the table.
     */
    public void displayCustomers(List<CustomerDTO> customers) {
        tableModel.setRowCount(0);
        
        for (CustomerDTO customer : customers) {
            Object[] row = {
                customer.getId(),
                customer.getName(),
                customer.isSeniorCitizen() ? "Yes" : "No",
                customer.isHasMembershipCard() ? "Yes" : "No",
                customer.isHasMembershipCard() ? 
                    String.valueOf(customer.getPoints()) : "N/A"
            };
            tableModel.addRow(row);
        }
        
        statusLabel.setText("Displaying " + customers.size() + " customer(s)");
    }
    
    /**
     * Displays a single customer's details in a dialog.
     */
    public void displayCustomer(CustomerDTO customer) {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Details\n");
        sb.append("================\n\n");
        sb.append("ID: ").append(customer.getId()).append("\n");
        sb.append("Name: ").append(customer.getName()).append("\n");
        sb.append("Senior Citizen: ").append(customer.isSeniorCitizen() ? "Yes" : "No").append("\n");
        sb.append("Membership Card: ").append(customer.isHasMembershipCard() ? "Yes" : "No").append("\n");
        
        if (customer.isHasMembershipCard()) {
            sb.append("Card ID: ").append(customer.getCardId()).append("\n");
            sb.append("Points: ").append(customer.getPoints()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this, textArea,
            "Customer Details", JOptionPane.INFORMATION_MESSAGE);
    }
}

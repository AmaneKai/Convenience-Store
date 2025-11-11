package com.konbini.view.swing;

import com.konbini.dto.TransactionDTO;
import com.konbini.dto.TransactionItemDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for viewing transaction history and generating sales reports.
 * Displays transactions in a table with filtering and analysis options.
 */
public class TransactionPanel extends JPanel {
    private Runnable backCallback;
    private int selectedChoice = -1;
    private final Object choiceLock = new Object();
    
    // UI Components
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel salesSummaryLabel;
    
    // Table columns
    private static final String[] COLUMN_NAMES = {
        "Transaction ID", "Customer", "Date", "Total Amount", 
        "Payment", "Change", "Items"
    };
    
    /**
     * Constructs the transaction panel with a back navigation callback.
     */
    public TransactionPanel(Runnable backCallback) {
        this.backCallback = backCallback;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel("Transaction Reports");
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Table with sales summary
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Sales summary at top
        salesSummaryLabel = new JLabel("Total Sales: ₱0.00");
        salesSummaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        salesSummaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        salesSummaryLabel.setOpaque(true);
        salesSummaryLabel.setBackground(new Color(46, 204, 113));
        salesSummaryLabel.setForeground(Color.WHITE);
        centerPanel.add(salesSummaryLabel, BorderLayout.NORTH);
        
        // Create table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(120); // ID
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Customer
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Date
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Total
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Payment
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Change
        transactionTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Items
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel("No transactions loaded");
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
            "View All Transactions", "View by Customer", "View by Date",
            "View by Date Range", "View Total Sales", "View Sales by Date",
            "View Sales by Range", "View Transaction Details", "Back"
        };
        
        for (int i = 0; i < buttonLabels.length; i++) {
            final int choice = i + 1;
            JButton btn = new JButton(buttonLabels[i]);
            btn.setPreferredSize(new Dimension(200, 40));
            btn.addActionListener(e -> handleMenuChoice(choice == 9 ? 0 : choice));
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
     * Displays a list of transactions in the table.
     */
    public void displayTransactions(List<TransactionDTO> transactions) {
        tableModel.setRowCount(0);
        
        for (TransactionDTO transaction : transactions) {
            Object[] row = {
                transaction.getId(),
                transaction.getCustomerName(),
                transaction.getDate().toString(),
                String.format("₱%.2f", transaction.getTotalAmount()),
                String.format("₱%.2f", transaction.getPaymentAmount()),
                String.format("₱%.2f", transaction.getChange()),
                transaction.getItems().size()
            };
            tableModel.addRow(row);
        }
        
        statusLabel.setText("Displaying " + transactions.size() + " transaction(s)");
    }
    
    /**
     * Displays a single transaction's details in a dialog.
     */
    public void displayTransaction(TransactionDTO transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction Details\n");
        sb.append("===================\n\n");
        sb.append("Transaction ID: ").append(transaction.getId()).append("\n");
        sb.append("Date: ").append(transaction.getDate()).append("\n");
        sb.append("Customer: ").append(transaction.getCustomerName());
        sb.append(" (ID: ").append(transaction.getCustomerId()).append(")\n\n");
        
        sb.append("Items:\n");
        sb.append("------\n");
        for (TransactionItemDTO item : transaction.getItems()) {
            sb.append(item.getProductName())
              .append(" x").append(item.getQuantity())
              .append(" @ ₱").append(String.format("%.2f", item.getUnitPrice()))
              .append(" = ₱").append(String.format("%.2f", item.getTotalPrice()))
              .append("\n");
        }
        
        sb.append("\n");
        sb.append("Subtotal: ₱").append(String.format("%.2f", transaction.getSubtotal())).append("\n");
        sb.append("Tax (VAT): ₱").append(String.format("%.2f", transaction.getTax())).append("\n");
        sb.append("Discount: ₱").append(String.format("%.2f", transaction.getDiscount())).append("\n");
        sb.append("Points Redeemed: ").append(transaction.getPointsRedeemed()).append("\n");
        sb.append("Total: ₱").append(String.format("%.2f", transaction.getTotalAmount())).append("\n");
        sb.append("Payment: ₱").append(String.format("%.2f", transaction.getPaymentAmount())).append("\n");
        sb.append("Change: ₱").append(String.format("%.2f", transaction.getChange())).append("\n");
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane,
            "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays total sales information.
     */
    public void displayTotalSales(double totalSales) {
        salesSummaryLabel.setText(String.format("Total Sales (All Time): ₱%.2f", totalSales));
        JOptionPane.showMessageDialog(this,
            String.format("Total sales across all transactions: ₱%.2f", totalSales),
            "Total Sales Report",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays sales for a specific date.
     */
    public void displayTotalSalesByDate(LocalDate date, double totalSales) {
        salesSummaryLabel.setText(String.format("Sales on %s: ₱%.2f", date, totalSales));
        JOptionPane.showMessageDialog(this,
            String.format("Total sales on %s: ₱%.2f", date, totalSales),
            "Sales Report by Date",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Displays sales for a date range.
     */
    public void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales) {
        salesSummaryLabel.setText(String.format("Sales (%s to %s): ₱%.2f", 
            startDate, endDate, totalSales));
        JOptionPane.showMessageDialog(this,
            String.format("Total sales from %s to %s: ₱%.2f", 
                startDate, endDate, totalSales),
            "Sales Report by Date Range",
            JOptionPane.INFORMATION_MESSAGE);
    }
}

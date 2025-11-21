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

import com.konbini.controller.TransactionManagementController;
import com.konbini.dto.TransactionDTO;
import com.konbini.dto.TransactionItemDTO;

public class TransactionPanel extends JPanel {
    private Runnable backCallback;
    private TransactionManagementController controller;

    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel salesSummaryLabel;

    private static final String[] COLUMN_NAMES = {"ID", "Customer", "Date", "Total", "Payment", "Change", "Items"};

    public TransactionPanel(TransactionManagementController controller, Runnable backCallback) {
        validateConstructorArgs(controller, backCallback);
        this.controller = controller;
        this.backCallback = backCallback;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Center - Sales Summary and Table
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

        JLabel titleLabel = new JLabel("Transaction Reports");
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

        salesSummaryLabel = new JLabel("Total Sales: ₱0.00");
        salesSummaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        salesSummaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        salesSummaryLabel.setOpaque(true);
        salesSummaryLabel.setBackground(new Color(46, 204, 113));
        salesSummaryLabel.setForeground(Color.WHITE);
        centerPanel.add(salesSummaryLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        statusLabel = new JLabel("No transactions loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        buttonPanel.add(createButton("View All", this::handleViewAllTransactions));
        buttonPanel.add(createButton("By Customer", this::handleViewCustomerTransactions));
        buttonPanel.add(createButton("By Date", this::handleViewByDate));
        buttonPanel.add(createButton("By Range", this::handleViewByDateRange));
        buttonPanel.add(createButton("Total Sales", this::handleViewTotalSales));
        buttonPanel.add(createButton("Sales by Date", this::handleViewSalesByDate));
        buttonPanel.add(createButton("Sales by Range", this::handleViewSalesByDateRange));

        return buttonPanel;
    }

    // ==================== EVENT HANDLERS ====================

    private void handleBackAction() {
        try {
            backCallback.run();
        } catch (Exception e) {
            handleUIException(e, "navigation", "Navigation error. Please try again.");
        }
    }

    private void handleViewAllTransactions() {
        try {
            controller.handleViewAllTransactions();
        } catch (Exception e) {
            handleUIException(e, "viewing all transactions", "Failed to load transactions. Please try again.");
        }
    }

    private void handleViewCustomerTransactions() {
        try {
            controller.handleViewCustomerTransactions();
        } catch (Exception e) {
            handleUIException(e, "viewing customer transactions", "Failed to load customer transactions. Please try again.");
        }
    }

    private void handleViewByDate() {
        try {
            controller.handleViewByDate();
        } catch (Exception e) {
            handleUIException(e, "viewing transactions by date", "Failed to load transactions for the selected date.");
        }
    }

    private void handleViewByDateRange() {
        try {
            controller.handleViewByDateRange();
        } catch (Exception e) {
            handleUIException(e, "viewing transactions by date range", "Failed to load transactions for the date range.");
        }
    }

    private void handleViewTotalSales() {
        try {
            controller.handleViewTotalSales();
        } catch (Exception e) {
            handleUIException(e, "calculating total sales", "Failed to calculate total sales. Please try again.");
        }
    }

    private void handleViewSalesByDate() {
        try {
            controller.handleViewSalesByDate();
        } catch (Exception e) {
            handleUIException(e, "calculating sales by date", "Failed to calculate sales for the selected date.");
        }
    }

    private void handleViewSalesByDateRange() {
        try {
            controller.handleViewSalesByDateRange();
        } catch (Exception e) {
            handleUIException(e, "calculating sales by date range", "Failed to calculate sales for the date range.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    public void displayTransactions(List<TransactionDTO> transactions) {
        try {
            validateTransactionList(transactions);

            tableModel.setRowCount(0);
            int validCount = 0;

            for (TransactionDTO transaction : transactions) {
                if (isValidTransaction(transaction)) {
                    tableModel.addRow(createTableRow(transaction));
                    validCount++;
                }
            }

            if (validCount == 0) {
                updateStatus("No valid transactions to display");
            } else {
                updateStatus("Displaying " + validCount + " of " + transactions.size() + " transaction(s)");
            }

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying transactions", "Invalid transaction data received.");
        } catch (Exception e) {
            handleUIException(e, "displaying transactions", "Failed to display transactions.");
        }
    }

    public void displayTransaction(TransactionDTO transaction) {
        try {
            validateTransaction(transaction);

            StringBuilder sb = new StringBuilder();
            sb.append("Transaction Details\n");
            sb.append("===================\n\n");
            sb.append("ID: ").append(transaction.getId()).append("\n");
            sb.append("Customer: ").append(transaction.getCustomerName()).append("\n");
            sb.append("Date: ").append(transaction.getDate()).append("\n\n");
            sb.append("Items:\n");

            for (TransactionItemDTO item : transaction.getItems()) {
                if (isValidTransactionItem(item)) {
                    sb.append("  ").append(item.getProductName()).append(" x").append(item.getQuantity())
                            .append(" = ₱").append(String.format("%.2f", item.getTotalPrice())).append("\n");
                }
            }

            sb.append("\nSubtotal: ₱").append(String.format("%.2f", transaction.getSubtotal())).append("\n");
            sb.append("Tax: ₱").append(String.format("%.2f", transaction.getTax())).append("\n");
            if (transaction.getDiscount() > 0) {
                sb.append("Discount: ₱").append(String.format("%.2f", transaction.getDiscount())).append("\n");
            }
            sb.append("Total: ₱").append(String.format("%.2f", transaction.getTotalAmount())).append("\n");
            sb.append("Paid: ₱").append(String.format("%.2f", transaction.getPaymentAmount())).append("\n");
            sb.append("Change: ₱").append(String.format("%.2f", transaction.getChange())).append("\n");

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane pane = new JScrollPane(textArea);
            pane.setPreferredSize(new Dimension(500, 400));
            JOptionPane.showMessageDialog(this, pane, "Transaction Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying transaction details", "Invalid transaction data.");
        } catch (Exception e) {
            handleUIException(e, "displaying transaction details", "Failed to display transaction details.");
        }
    }

    public void displayTotalSales(double totalSales) {
        try {
            validateSalesAmount(totalSales);

            String summaryText = String.format("Total Sales (All Time): ₱%.2f", totalSales);
            salesSummaryLabel.setText(summaryText);

            JOptionPane.showMessageDialog(this,
                    String.format("Total Sales: ₱%.2f", totalSales),
                    "Sales Report",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying total sales", "Invalid sales data.");
        } catch (Exception e) {
            handleUIException(e, "displaying total sales", "Failed to display sales report.");
        }
    }

    public void displayTotalSalesByDate(java.time.LocalDate date, double totalSales) {
        try {
            validateDate(date);
            validateSalesAmount(totalSales);

            String summaryText = String.format("Sales on %s: ₱%.2f", date, totalSales);
            salesSummaryLabel.setText(summaryText);

            JOptionPane.showMessageDialog(this,
                    String.format("Sales on %s: ₱%.2f", date, totalSales),
                    "Sales Report",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying sales by date", "Invalid sales data.");
        } catch (Exception e) {
            handleUIException(e, "displaying sales by date", "Failed to display sales report.");
        }
    }

    public void displayTotalSalesByDateRange(java.time.LocalDate start, java.time.LocalDate end, double totalSales) {
        try {
            validateDateRange(start, end);
            validateSalesAmount(totalSales);

            String summaryText = String.format("Sales (%s to %s): ₱%.2f", start, end, totalSales);
            salesSummaryLabel.setText(summaryText);

            JOptionPane.showMessageDialog(this,
                    String.format("Sales from %s to %s: ₱%.2f", start, end, totalSales),
                    "Sales Report",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e) {
            handleUIException(e, "displaying sales by date range", "Invalid sales data.");
        } catch (Exception e) {
            handleUIException(e, "displaying sales by date range", "Failed to display sales report.");
        }
    }

    // ==================== UTILITY METHODS ====================

    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(110, 40));
        btn.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                handleUIException(ex, "button action: " + text, "Action failed. Please try again.");
            }
        });
        return btn;
    }

    private Object[] createTableRow(TransactionDTO transaction) {
        return new Object[]{
                transaction.getId(),
                transaction.getCustomerName() != null ? transaction.getCustomerName() : "Unknown",
                transaction.getDate() != null ? transaction.getDate().toString() : "Unknown",
                "₱" + String.format("%.2f", transaction.getTotalAmount()),
                "₱" + String.format("%.2f", transaction.getPaymentAmount()),
                "₱" + String.format("%.2f", transaction.getChange()),
                transaction.getItems() != null ? transaction.getItems().size() : 0
        };
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    // ==================== VALIDATION METHODS ====================

    private void validateConstructorArgs(TransactionManagementController controller, Runnable backCallback) {
        if (controller == null) {
            throw new IllegalArgumentException("TransactionManagementController cannot be null");
        }
        if (backCallback == null) {
            throw new IllegalArgumentException("Back callback cannot be null");
        }
    }

    private void validateTransactionList(List<TransactionDTO> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transaction list cannot be null");
        }
    }

    private boolean isValidTransaction(TransactionDTO transaction) {
        return transaction != null &&
                transaction.getId() != null &&
                !transaction.getId().trim().isEmpty();
    }

    private void validateTransaction(TransactionDTO transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.getId() == null || transaction.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
    }

    private boolean isValidTransactionItem(TransactionItemDTO item) {
        return item != null &&
                item.getProductName() != null &&
                !item.getProductName().trim().isEmpty();
    }

    private void validateSalesAmount(double salesAmount) {
        if (salesAmount < 0) {
            throw new IllegalArgumentException("Sales amount cannot be negative");
        }
        if (Double.isNaN(salesAmount) || Double.isInfinite(salesAmount)) {
            throw new IllegalArgumentException("Sales amount must be a valid number");
        }
    }

    private void validateDate(java.time.LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
    }

    private void validateDateRange(java.time.LocalDate start, java.time.LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Both start and end dates must be provided");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    // ==================== ERROR HANDLING ====================

    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("TransactionPanel Error " + context + ": " + e.getMessage());
        updateStatus("Error: " + userMessage);
        JOptionPane.showMessageDialog(this, userMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

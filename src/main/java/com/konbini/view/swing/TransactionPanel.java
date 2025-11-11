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
        JLabel titleLabel = new JLabel("Transaction Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        JButton backBtn = new JButton("← Back");
        backBtn.addActionListener(e -> backCallback.run());
        headerPanel.add(backBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Sales Summary and Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        salesSummaryLabel = new JLabel("Total Sales: ₱0.00");
        salesSummaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        salesSummaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        salesSummaryLabel.setOpaque(true);
        salesSummaryLabel.setBackground(new Color(46, 204, 113));
        salesSummaryLabel.setForeground(Color.WHITE);
        centerPanel.add(salesSummaryLabel, BorderLayout.NORTH);
        
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        statusLabel = new JLabel("No transactions loaded");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        centerPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(createButton("View All", () -> controller.handleViewAllTransactions()));
        buttonPanel.add(createButton("By Customer", () -> controller.handleViewCustomerTransactions()));
        buttonPanel.add(createButton("By Date", () -> controller.handleViewByDate()));
        buttonPanel.add(createButton("By Range", () -> controller.handleViewByDateRange()));
        buttonPanel.add(createButton("Total Sales", () -> controller.handleViewTotalSales()));
        buttonPanel.add(createButton("Sales by Date", () -> controller.handleViewSalesByDate()));
        buttonPanel.add(createButton("Sales by Range", () -> controller.handleViewSalesByDateRange()));
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(110, 40));
        btn.addActionListener(e -> action.run());
        return btn;
    }
    
    public void displayTransactions(List<TransactionDTO> transactions) {
        tableModel.setRowCount(0);
        for (TransactionDTO t : transactions) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getCustomerName(),
                t.getDate().toString(),
                "₱" + String.format("%.2f", t.getTotalAmount()),
                "₱" + String.format("%.2f", t.getPaymentAmount()),
                "₱" + String.format("%.2f", t.getChange()),
                t.getItems().size()
            });
        }
        statusLabel.setText("Displaying " + transactions.size() + " transaction(s)");
    }
    
    public void displayTransaction(TransactionDTO t) {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction Details\n");
        sb.append("===================\n\n");
        sb.append("ID: ").append(t.getId()).append("\n");
        sb.append("Customer: ").append(t.getCustomerName()).append("\n");
        sb.append("Date: ").append(t.getDate()).append("\n\n");
        sb.append("Items:\n");
        for (TransactionItemDTO item : t.getItems()) {
            sb.append("  ").append(item.getProductName()).append(" x").append(item.getQuantity())
              .append(" = ₱").append(String.format("%.2f", item.getTotalPrice())).append("\n");
        }
        sb.append("\nSubtotal: ₱").append(String.format("%.2f", t.getSubtotal())).append("\n");
        sb.append("Tax: ₱").append(String.format("%.2f", t.getTax())).append("\n");
        if (t.getDiscount() > 0) {
            sb.append("Discount: ₱").append(String.format("%.2f", t.getDiscount())).append("\n");
        }
        sb.append("Total: ₱").append(String.format("%.2f", t.getTotalAmount())).append("\n");
        sb.append("Paid: ₱").append(String.format("%.2f", t.getPaymentAmount())).append("\n");
        sb.append("Change: ₱").append(String.format("%.2f", t.getChange())).append("\n");
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane pane = new JScrollPane(textArea);
        pane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, pane, "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void displayTotalSales(double totalSales) {
        salesSummaryLabel.setText(String.format("Total Sales (All Time): ₱%.2f", totalSales));
        JOptionPane.showMessageDialog(this, String.format("Total: ₱%.2f", totalSales), "Sales Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void displayTotalSalesByDate(java.time.LocalDate date, double totalSales) {
        salesSummaryLabel.setText(String.format("Sales on %s: ₱%.2f", date, totalSales));
        JOptionPane.showMessageDialog(this, String.format("Sales on %s: ₱%.2f", date, totalSales), "Sales Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void displayTotalSalesByDateRange(java.time.LocalDate start, java.time.LocalDate end, double totalSales) {
        salesSummaryLabel.setText(String.format("Sales (%s to %s): ₱%.2f", start, end, totalSales));
        JOptionPane.showMessageDialog(this, String.format("Sales from %s to %s: ₱%.2f", start, end, totalSales), "Sales Report", JOptionPane.INFORMATION_MESSAGE);
    }
}
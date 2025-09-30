package com.konbini.controller;

import com.konbini.model.Transaction;
import com.konbini.view.StoreView;
import java.time.LocalDate;
import java.util.Optional;

public class TransactionManagementController {
    private final StoreView view;
    private final CustomerController customerController;
    private final TransactionController transactionController;
    
    public TransactionManagementController(
            StoreView view,
            CustomerController customerController,
            TransactionController transactionController) {
        this.view = view;
        this.customerController = customerController;
        this.transactionController = transactionController;
    }
    
    public void handleTransactionManagement() {
        boolean backToMain = false;
        
        while (!backToMain) {
            view.displayTransactionMenu();
            int choice = view.getTransactionMenuChoice();
            
            switch (choice) {
                case 1:
                    viewAllTransactions();
                    break;
                case 2:
                    viewTransactionDetails();
                    break;
                case 3:
                    viewCustomerTransactions();
                    break;
                case 4:
                    viewTransactionsByDate();
                    break;
                case 5:
                    viewTransactionsByDateRange();
                    break;
                case 6:
                    viewTotalSales();
                    break;
                case 7:
                    viewTotalSalesByDate();
                    break;
                case 8:
                    viewTotalSalesByDateRange();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    view.displayErrorMessage("Invalid choice. Please try again.");
            }
        }
    }
    
    private void viewAllTransactions() {
        view.displayTransactions(transactionController.getAllTransactions());
    }
    
    private void viewTransactionDetails() {
        try {
            view.displayTransactions(transactionController.getAllTransactions());
            String transactionId = view.getStringInput("Enter transaction ID to view details: ");
            Optional<Transaction> optionalTransaction = 
                transactionController.getTransactionById(transactionId);
            
            if (optionalTransaction.isPresent()) {
                view.displayTransaction(optionalTransaction.get());
                
                if (view.getBooleanInput("View receipt?")) {
                    String receipt = transactionController.generateReceipt(
                        optionalTransaction.get());
                    view.displayReceipt(receipt);
                }
            } else {
                view.displayErrorMessage("Transaction not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view transaction details: " 
                + e.getMessage());
        }
    }
    
    private void viewCustomerTransactions() {
        try {
            view.displayCustomers(customerController.getAllCustomers());
            String customerId = view.getStringInput
                ("Enter customer ID to view transactions: ");
            view.displayTransactions(transactionController
                    .getTransactionsByCustomerId(customerId));
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to view customer transactions: " + e.getMessage());
        }
    }
    
    private void viewTransactionsByDate() {
        try {
            LocalDate date = view
                .getDateInput("Enter date to view transactions: ");
            if (date != null) {
                view.displayTransactions(transactionController
                    .getTransactionsByDate(date));
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view transactions by date: " 
                + e.getMessage());
        }
    }
    
    private void viewTransactionsByDateRange() {
        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            LocalDate endDate = view.getDateInput("Enter end date: ");
            
            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    view.displayErrorMessage
                        ("Start date cannot be after end date.");
                    return;
                }
                view.displayTransactions(
                    transactionController.getTransactionsByDateRange
                        (startDate, endDate));
            }
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to view transactions by date range: " 
                + e.getMessage());
        }
    }
    
    private void viewTotalSales() {
        double totalSales = transactionController.getTotalSales();
        view.displayTotalSales(totalSales);
    }
    
    private void viewTotalSalesByDate() {
        try {
            LocalDate date = view.getDateInput
                ("Enter date to view total sales: ");
            if (date != null) {
                double totalSales = transactionController
                    .getTotalSalesByDate(date);
                view.displayTotalSalesByDate(date, totalSales);
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view total sales by date: " 
                + e.getMessage());
        }
    }
    
    private void viewTotalSalesByDateRange() {
        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            LocalDate endDate = view.getDateInput("Enter end date: ");
            
            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    view.displayErrorMessage
                        ("Start date cannot be after end date.");
                    return;
                }
                double totalSales = transactionController
                    .getTotalSalesByDateRange(startDate, endDate);
                view.displayTotalSalesByDateRange(startDate, endDate, 
                    totalSales);
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view total sales by date range: " 
                + e.getMessage());
        }
    }
}

package com.konbini.controller;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.dto.CustomerDTO;
import com.konbini.dto.TransactionDTO;
import com.konbini.model.Transaction;
import com.konbini.view.StoreView;

/**
 * Transaction Management - GUI Event-Driven.
 * Each method is a single action called directly by GUI buttons.
 */
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
    
    public void handleViewAllTransactions() {
        try {
            view.displayTransactions(TransactionDTO.fromModelList(transactionController.getAllTransactions()));
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view transactions: " + e.getMessage());
        }
    }
    
    public void handleViewTransactionDetails() {
        try {
            view.displayTransactions(TransactionDTO.fromModelList(transactionController.getAllTransactions()));
            String transactionId = view.getStringInput("Enter transaction ID: ");
            if (transactionId == null || transactionId.trim().isEmpty()) return;
            
            Optional<Transaction> transaction = transactionController.getTransactionById(transactionId);
            if (transaction.isPresent()) {
                view.displayTransaction(TransactionDTO.fromModel(transaction.get()));
            } else {
                view.displayErrorMessage("Transaction not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
    
    public void handleViewCustomerTransactions() {
        try {
            view.displayCustomers(customerController.getAllCustomers().stream().map(CustomerDTO::fromModel).collect(Collectors.toList()));
            String customerId = view.getStringInput("Enter customer ID: ");
            if (customerId == null || customerId.trim().isEmpty()) return;
            
            view.displayTransactions(TransactionDTO.fromModelList(transactionController.getTransactionsByCustomerId(customerId)));
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
    
    public void handleViewByDate() {
        try {
            LocalDate date = view.getDateInput("Enter date: ");
            if (date != null) {
                view.displayTransactions(TransactionDTO.fromModelList(transactionController.getTransactionsByDate(date)));
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
    
    public void handleViewByDateRange() {
        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            if (startDate == null) return;
            
            LocalDate endDate = view.getDateInput("Enter end date: ");
            if (endDate == null) return;
            
            if (startDate.isAfter(endDate)) {
                view.displayErrorMessage("Start date cannot be after end date.");
                return;
            }
            
            view.displayTransactions(TransactionDTO.fromModelList(transactionController.getTransactionsByDateRange(startDate, endDate)));
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
    
    public void handleViewTotalSales() {
        try {
            double total = transactionController.getTotalSales();
            view.displayTotalSales(total);
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
    
    public void handleViewSalesByDate() {
        try {
            LocalDate date = view.getDateInput("Enter date: ");
            if (date != null) {
                double total = transactionController.getTotalSalesByDate(date);
                view.displayTotalSalesByDate(date, total);
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
    
    public void handleViewSalesByDateRange() {
        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            if (startDate == null) return;
            
            LocalDate endDate = view.getDateInput("Enter end date: ");
            if (endDate == null) return;
            
            if (startDate.isAfter(endDate)) {
                view.displayErrorMessage("Start date cannot be after end date.");
                return;
            }
            
            double total = transactionController.getTotalSalesByDateRange(startDate, endDate);
            view.displayTotalSalesByDateRange(startDate, endDate, total);
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
}
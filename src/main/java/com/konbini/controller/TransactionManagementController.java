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
        if (view == null || customerController == null || transactionController == null) {
            throw new IllegalArgumentException("All dependencies must be provided");
        }
        this.view = view;
        this.customerController = customerController;
        this.transactionController = transactionController;
    }

    // ==================== PUBLIC HANDLERS ====================

    public void handleViewAllTransactions() {
        try {
            view.displayTransactions(TransactionDTO.fromModelList(transactionController.getAllTransactions()));
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing all transactions");
        } catch (Exception e) {
            handleGenericException(e, "viewing all transactions", "Failed to load transactions. Please try again.");
        }
    }

    public void handleViewByDate() {
        try {
            LocalDate date = view.getDateInput("Enter date: ");
            if (date != null) {
                view.displayTransactions(
                        TransactionDTO.fromModelList(transactionController.getTransactionsByDate(date)));
            } else {
                view.displayInfoMessage("No date selected.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing transactions by date");
        } catch (Exception e) {
            handleGenericException(e, "viewing transactions by date", "Failed to load transactions for the selected date.");
        }
    }

    public void handleViewTotalSales() {
        try {
            double total = transactionController.getTotalSales();
            view.displayTotalSales(total);
        } catch (Exception e) {
            handleGenericException(e, "calculating total sales", "Failed to calculate total sales. Please try again.");
        }
    }

    public void handleViewSalesByDate() {
        try {
            LocalDate date = view.getDateInput("Enter date: ");
            if (date != null) {
                double total = transactionController.getTotalSalesByDate(date);
                view.displayTotalSalesByDate(date, total);
            } else {
                view.displayInfoMessage("No date selected.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing sales by date");
        } catch (Exception e) {
            handleGenericException(e, "viewing sales by date", "Failed to calculate sales for the selected date.");
        }
    }

    public void handleViewTransactionDetails() {
        try {
            view.displayTransactions(TransactionDTO.fromModelList(transactionController.getAllTransactions()));
            String transactionId = view.getStringInput("Enter transaction ID: ");

            if (transactionId != null && !transactionId.trim().isEmpty()) {
                showTransactionDetails(transactionId.trim());
            } else {
                view.displayErrorMessage("Transaction ID cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing transaction details");
        } catch (Exception e) {
            handleGenericException(e, "viewing transaction details", "Failed to load transaction details. Please try again.");
        }
    }

    public void handleViewCustomerTransactions() {
        try {
            view.displayCustomers(customerController.getAllCustomers().stream()
                    .map(CustomerDTO::fromModel)
                    .collect(Collectors.toList()));
            String customerId = view.getStringInput("Enter customer ID: ");

            if (customerId != null && !customerId.trim().isEmpty()) {
                showCustomerTransactions(customerId.trim());
            } else {
                view.displayErrorMessage("Customer ID cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing customer transactions");
        } catch (Exception e) {
            handleGenericException(e, "viewing customer transactions", "Failed to load customer transactions. Please try again.");
        }
    }

    public void handleViewByDateRange() {
        try {
            Optional<DateRange> dateRange = promptForDateRange("view transactions");
            dateRange.ifPresent(range -> {
                view.displayTransactions(TransactionDTO.fromModelList(
                        transactionController.getTransactionsByDateRange(range.startDate(), range.endDate())));
            });
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing transactions by date range");
        } catch (Exception e) {
            handleGenericException(e, "viewing transactions by date range", "Failed to load transactions for the date range.");
        }
    }

    public void handleViewSalesByDateRange() {
        try {
            Optional<DateRange> dateRange = promptForDateRange("calculate sales");
            dateRange.ifPresent(range -> {
                double total = transactionController.getTotalSalesByDateRange(range.startDate(), range.endDate());
                view.displayTotalSalesByDateRange(range.startDate(), range.endDate(), total);
            });
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing sales by date range");
        } catch (Exception e) {
            handleGenericException(e, "viewing sales by date range", "Failed to calculate sales for the date range.");
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void showTransactionDetails(String transactionId) {
        try {
            validateTransactionId(transactionId);
            Optional<Transaction> transaction = transactionController.getTransactionById(transactionId);

            if (transaction.isPresent()) {
                view.displayTransaction(TransactionDTO.fromModel(transaction.get()));
            } else {
                view.displayErrorMessage("Transaction not found.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "showing transaction details");
        } catch (Exception e) {
            handleGenericException(e, "showing transaction details", "Error displaying transaction details.");
        }
    }

    private void showCustomerTransactions(String customerId) {
        try {
            validateCustomerId(customerId);
            view.displayTransactions(
                    TransactionDTO.fromModelList(transactionController.getTransactionsByCustomerId(customerId)));
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "showing customer transactions");
        } catch (Exception e) {
            handleGenericException(e, "showing customer transactions", "Error loading customer transactions.");
        }
    }

    private Optional<DateRange> promptForDateRange(String operation) {
        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            if (startDate == null) {
                view.displayInfoMessage("Start date required for " + operation + ".");
                return Optional.empty();
            }

            LocalDate endDate = view.getDateInput("Enter end date: ");
            if (endDate == null) {
                view.displayInfoMessage("End date required for " + operation + ".");
                return Optional.empty();
            }

            validateDateRange(startDate, endDate);
            return Optional.of(new DateRange(startDate, endDate));

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "prompting for date range");
            return Optional.empty();
        } catch (Exception e) {
            handleGenericException(e, "prompting for date range", "Failed to process date range input.");
            return Optional.empty();
        }
    }

    // ==================== VALIDATION METHODS ====================

    private void validateTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
    }

    private void validateCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both start date and end date must be provided");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    // ==================== ERROR HANDLING HELPERS ====================

    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }

    // ==================== SUPPORTING RECORD ====================

    private record DateRange(LocalDate startDate, LocalDate endDate) {
        DateRange {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Dates cannot be null");
            }
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        }
    }
}
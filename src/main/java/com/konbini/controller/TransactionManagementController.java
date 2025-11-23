package com.konbini.controller;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.dto.CustomerDTO;
import com.konbini.dto.TransactionDTO;
import com.konbini.model.Transaction;
import com.konbini.view.swing.SwingStoreView;

/**
 * Controller for managing transaction viewing, reporting, and sales analytics.
 * Provides GUI event-driven methods for transaction-related operations including
 * viewing transactions by various criteria and calculating sales totals.
 */
public class TransactionManagementController {
    private final SwingStoreView view;
    private final CustomerController customerController;
    private final TransactionController transactionController;

    /**
     * Constructs a TransactionManagementController with all required dependencies.
     *
     * @param view the store view for user interface interactions
     * @param customerController controller for customer operations
     * @param transactionController controller for transaction operations
     * @throws IllegalArgumentException if any dependency is null
     */
    public TransactionManagementController(
            SwingStoreView view,
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

    /**
     * Handles displaying all transactions in the system.
     * Catches and handles any exceptions during the loading process.
     */
    public void handleViewAllTransactions() {
        try {
            view.displayTransactions(TransactionDTO.fromModelList(transactionController.getAllTransactions()));
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing all transactions");
        } catch (Exception e) {
            handleGenericException(e, "viewing all transactions", "Failed to load transactions. Please try again.");
        }
    }

    /**
     * Handles viewing transactions that occurred on a specific date.
     * Prompts the user for a date input and displays matching transactions.
     */
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

    /**
     * Handles calculating and displaying total sales across all transactions.
     */
    public void handleViewTotalSales() {
        try {
            double total = transactionController.getTotalSales();
            view.displayTotalSales(total);
        } catch (Exception e) {
            handleGenericException(e, "calculating total sales", "Failed to calculate total sales. Please try again.");
        }
    }

    /**
     * Handles calculating and displaying total sales for a specific date.
     * Prompts the user for a date input and displays sales total for that date.
     */
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

    /**
     * Handles viewing detailed information for a specific transaction.
     * Displays all transactions first, then prompts for transaction ID.
     */
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

    /**
     * Handles viewing all transactions for a specific customer.
     * Displays all customers first, then prompts for customer ID.
     */
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

    /**
     * Handles viewing transactions within a specified date range.
     * Prompts the user for start and end dates.
     */
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

    /**
     * Handles calculating and displaying total sales within a specified date range.
     * Prompts the user for start and end dates.
     */
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

    /**
     * Shows detailed information for a specific transaction.
     *
     * @param transactionId the ID of the transaction to display
     */
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

    /**
     * Shows all transactions for a specific customer.
     *
     * @param customerId the ID of the customer whose transactions to display
     */
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

    /**
     * Prompts the user for a date range (start and end dates).
     *
     * @param operation the operation being performed (for context in messages)
     * @return an Optional containing the DateRange if valid dates provided, empty otherwise
     */
    private Optional<DateRange> promptForDateRange(String operation) {
        Optional<DateRange> temp = Optional.empty();

        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            LocalDate endDate = null;

            if (startDate != null) {
                endDate = view.getDateInput("Enter end date: ");
            }

            if (startDate == null) {
                view.displayInfoMessage("Start date required for " + operation + ".");
            } else if (endDate == null) {
                view.displayInfoMessage("End date required for " + operation + ".");
            } else {
                validateDateRange(startDate, endDate);
                temp = Optional.of(new DateRange(startDate, endDate));
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "prompting for date range");
        } catch (Exception e) {
            handleGenericException(e, "prompting for date range", "Failed to process date range input.");
        }

        return temp;
    }

    // ==================== VALIDATION METHODS ====================

    /**
     * Validates that a transaction ID is not null or empty.
     *
     * @param transactionId the transaction ID to validate
     * @throws IllegalArgumentException if the transaction ID is invalid
     */
    private void validateTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
    }

    /**
     * Validates that a customer ID is not null or empty.
     *
     * @param customerId the customer ID to validate
     * @throws IllegalArgumentException if the customer ID is invalid
     */
    private void validateCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
    }

    /**
     * Validates that a date range is valid (both dates provided and start not after end).
     *
     * @param startDate the start date to validate
     * @param endDate the end date to validate
     * @throws IllegalArgumentException if the date range is invalid
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both start date and end date must be provided");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    // ==================== ERROR HANDLING HELPERS ====================

    /**
     * Handles IllegalArgumentException by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     */
    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    /**
     * Handles generic exceptions by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     * @param userMessage the message to display to the user
     */
    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }

    // ==================== SUPPORTING RECORD ====================

    /**
     * Record representing a date range with validation.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     */
    private record DateRange(LocalDate startDate, LocalDate endDate) {
        /**
         * Compact constructor for DateRange that validates the dates.
         *
         * @throws IllegalArgumentException if dates are null or start date is after end date
         */
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
package com.konbini.controller;

import com.konbini.model.Cart;
import com.konbini.model.Transaction;
import com.konbini.service.TransactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller class responsible for coordinating and executing all transaction-related
 * operations, including processing sales, generating receipts, and querying
 * transaction history and sales reports. It delegates core business logic to the
 * TransactionService.
 */
public class TransactionController {
    /**
     * The service dependency used for all data persistence and business logic
     * related to Transaction entities.
     */
    private final TransactionService transactionService;

    /**
     * Constructs the TransactionController, injecting the required transaction service.
     *
     * @param transactionService The service providing data access and business logic for transactions.
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Processes a completed transaction, finalizing the sale, updating inventory,
     * managing customer points, and saving the transaction record.
     *
     * @param cart The completed shopping cart containing items and customer details.
     * @param paymentAmount The total amount paid by the customer.
     * @param pointsToRedeem The number of loyalty points redeemed in this transaction.
     * @return The newly created Transaction object.
     */
    public Transaction processTransaction(Cart cart, double paymentAmount,
        int pointsToRedeem) {
        return transactionService.processTransaction(cart, paymentAmount,
            pointsToRedeem);
    }

    /**
     * Generates a formatted string representation of the transaction receipt.
     *
     * @param transaction The Transaction object for which to generate the receipt.
     * @return A String containing the formatted receipt details.
     */
    public String generateReceipt(Transaction transaction) {
        return transactionService.generateReceipt(transaction);
    }

    /**
     * Attempts to save the transaction receipt to a specified file path.
     *
     * @param transaction The Transaction object whose receipt should be saved.
     * @param filePath The path to the file where the receipt will be written.
     * @throws RuntimeException if the file saving operation fails.
     */
    public void saveReceiptToFile(Transaction transaction, String filePath) {
        try {
            transactionService.saveReceiptToFile(transaction, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save receipt to file: "
                + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a list of all historical transactions recorded in the system.
     *
     * @return A List of all Transaction objects.
     */
    public List<Transaction> getAllTransactions() {
        return transactionService.findAll();
    }

    /**
     * Retrieves a single transaction by its unique identifier.
     *
     * @param transactionId The ID of the transaction to find.
     * @return An Optional containing the Transaction if found, or an empty Optional otherwise.
     */
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionService.findById(transactionId);
    }

    /**
     * Retrieves a list of transactions associated with a specific customer ID.
     *
     * @param customerId The ID of the customer whose transactions are to be found.
     * @return A List of Transaction objects made by the specified customer.
     */
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionService.findByCustomerId(customerId);
    }

    /**
     * Retrieves a list of transactions that occurred on a specific date.
     *
     * @param date The LocalDate to filter transactions by.
     * @return A List of Transaction objects that occurred on the specified date.
     */
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactionService.findByDate(date);
    }

    /**
     * Retrieves a list of transactions that occurred within a specific date range (inclusive).
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return A List of Transaction objects within the specified date range.
     */
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate,
        LocalDate endDate) {
        return transactionService.findByDateRange(startDate, endDate);
    }

    /**
     * Calculates the total sales revenue across all recorded transactions.
     *
     * @return The total monetary value of all sales.
     */
    public double getTotalSales() {
        return transactionService.getTotalSales();
    }

    /**
     * Calculates the total sales revenue for a specific date.
     *
     * @param date The LocalDate for which to calculate total sales.
     * @return The total monetary value of sales on the specified date.
     */
    public double getTotalSalesByDate(LocalDate date) {
        return transactionService.getTotalSalesByDate(date);
    }

    /**
     * Calculates the total sales revenue within a specific date range (inclusive).
     *
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return The total monetary value of sales within the specified date range.
     */
    public double getTotalSalesByDateRange(LocalDate startDate,
        LocalDate endDate) {
        return transactionService.getTotalSalesByDateRange(startDate, endDate);
    }

    /**
     * Persists the current list of transactions to permanent storage.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    public boolean saveData() {
        return transactionService.saveTransactions();
    }

    /**
     * Loads the transaction data from permanent storage into the application memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    public boolean loadData() {
        return transactionService.loadTransactions();
    }
}

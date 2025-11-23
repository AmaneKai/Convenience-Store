package com.konbini.controller;

import com.konbini.model.Cart;
import com.konbini.model.Transaction;
import com.konbini.service.TransactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing transaction operations including processing transactions,
 * generating receipts, retrieving transaction data, and calculating sales analytics.
 */
public class TransactionController {
    private final TransactionService transactionService;

    /**
     * Constructs a TransactionController with the specified transaction service.
     *
     * @param transactionService the service for transaction operations
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Processes a transaction from a cart, handling payment and points redemption.
     *
     * @param cart the cart containing items to purchase
     * @param paymentAmount the amount paid by the customer
     * @param pointsToRedeem the number of loyalty points to redeem
     * @return the processed Transaction object
     */
    public Transaction processTransaction(Cart cart, double paymentAmount,
        int pointsToRedeem) {
        return transactionService.processTransaction(cart, paymentAmount,
            pointsToRedeem);
    }

    /**
     * Generates a formatted receipt string for a transaction.
     *
     * @param transaction the transaction to generate a receipt for
     * @return a formatted receipt string
     */
    public String generateReceipt(Transaction transaction) {
        return transactionService.generateReceipt(transaction);
    }

    /**
     * Saves a transaction receipt to a file.
     *
     * @param transaction the transaction to save
     * @param filePath the file path where the receipt should be saved
     * @throws RuntimeException if saving to file fails
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
     * Retrieves all transactions from the system.
     *
     * @return a list of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return transactionService.findAll();
    }

    /**
     * Finds a transaction by its unique identifier.
     *
     * @param transactionId the ID of the transaction to find
     * @return an Optional containing the transaction if found, empty otherwise
     */
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionService.findById(transactionId);
    }

    /**
     * Retrieves all transactions for a specific customer.
     *
     * @param customerId the ID of the customer
     * @return a list of transactions for the specified customer
     */
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionService.findByCustomerId(customerId);
    }

    /**
     * Retrieves all transactions that occurred on a specific date.
     *
     * @param date the date to filter transactions by
     * @return a list of transactions from the specified date
     */
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactionService.findByDate(date);
    }

    /**
     * Retrieves all transactions that occurred within a date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a list of transactions within the specified date range
     */
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate,
        LocalDate endDate) {
        return transactionService.findByDateRange(startDate, endDate);
    }

    /**
     * Calculates the total sales amount across all transactions.
     *
     * @return the total sales amount
     */
    public double getTotalSales() {
        return transactionService.getTotalSales();
    }

    /**
     * Calculates the total sales amount for a specific date.
     *
     * @param date the date to calculate sales for
     * @return the total sales amount for the specified date
     */
    public double getTotalSalesByDate(LocalDate date) {
        return transactionService.getTotalSalesByDate(date);
    }

    /**
     * Calculates the total sales amount within a date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return the total sales amount within the specified date range
     */
    public double getTotalSalesByDateRange(LocalDate startDate,
        LocalDate endDate) {
        return transactionService.getTotalSalesByDateRange(startDate, endDate);
    }

    /**
     * Saves all transaction data to persistent storage.
     *
     * @return true if save operation was successful, false otherwise
     */
    public boolean saveData() {
        return transactionService.saveTransactions();
    }

    /**
     * Loads all transaction data from persistent storage.
     *
     * @return true if load operation was successful, false otherwise
     */
    public boolean loadData() {
        return transactionService.loadTransactions();
    }
}
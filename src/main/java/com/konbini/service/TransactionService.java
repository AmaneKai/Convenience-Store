package com.konbini.service;

import com.konbini.model.Cart;
import com.konbini.model.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for transaction processing and management operations.
 * Handles transaction processing, receipt generation, sales analytics,
 * and data persistence.
 */
public interface TransactionService {

    /**
     * Processes a transaction from a cart, handling payment and points redemption.
     *
     * @param cart the cart containing items to purchase
     * @param paymentAmount the amount paid by the customer
     * @param pointsToRedeem the number of loyalty points to redeem
     * @return the processed Transaction object
     */
    Transaction processTransaction(Cart cart, double paymentAmount,
        int pointsToRedeem);

    /**
     * Generates a formatted receipt string for a transaction.
     *
     * @param transaction the transaction to generate a receipt for
     * @return a formatted receipt string
     */
    String generateReceipt(Transaction transaction);

    /**
     * Saves a transaction receipt to a file.
     *
     * @param transaction the transaction to save
     * @param filePath the file path where the receipt should be saved
     * @return true if the save operation was successful, false otherwise
     * @throws IOException if an I/O error occurs during file writing
     */
    boolean saveReceiptToFile(Transaction transaction, String filePath)
        throws IOException;

    /**
     * Finds a transaction by its unique identifier.
     *
     * @param transactionId the transaction ID to search for
     * @return an Optional containing the transaction if found, empty otherwise
     */
    Optional<Transaction> findById(String transactionId);

    /**
     * Retrieves all transactions in the system.
     *
     * @return a list of all transactions, empty list if no transactions exist
     */
    List<Transaction> findAll();

    /**
     * Finds all transactions for a specific customer.
     *
     * @param customerId the ID of the customer
     * @return a list of transactions for the specified customer
     */
    List<Transaction> findByCustomerId(String customerId);

    /**
     * Finds all transactions that occurred on a specific date.
     *
     * @param date the date to filter transactions by
     * @return a list of transactions from the specified date
     */
    List<Transaction> findByDate(LocalDate date);

    /**
     * Finds all transactions that occurred within a date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a list of transactions within the specified date range
     */
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Calculates the total sales amount across all transactions.
     *
     * @return the total sales amount
     */
    double getTotalSales();

    /**
     * Calculates the total sales amount for a specific date.
     *
     * @param date the date to calculate sales for
     * @return the total sales amount for the specified date
     */
    double getTotalSalesByDate(LocalDate date);

    /**
     * Calculates the total sales amount within a date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return the total sales amount within the specified date range
     */
    double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Saves all transaction data to persistent storage.
     *
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveTransactions();

    /**
     * Loads transaction data from persistent storage.
     *
     * @return true if the load operation was successful, false otherwise
     */
    boolean loadTransactions();
}
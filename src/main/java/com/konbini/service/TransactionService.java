package com.konbini.service;

import com.konbini.model.Cart;
import com.konbini.model.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining the core business logic layer for handling and managing sales transactions.
 * This service is responsible for processing a sale (calculating totals, applying discounts,
 * updating inventory/points), generating reports, and persisting transaction records.
 */
public interface TransactionService {
    /**
     * Finalizes and records a sales transaction.
     * This method orchestrates all steps: calculating final totals (tax, discount, points earned),
     * applying payment, updating customer loyalty points, and saving the immutable transaction record.
     *
     * @param cart The Cart containing the items and customer details (or a dummy customer).
     * @param paymentAmount The amount of money tendered by the customer.
     * @param pointsToRedeem The number of loyalty points the customer wishes to use.
     * @return The finalized, immutable Transaction object.
     * @throws IllegalArgumentException if the payment amount is insufficient or points redemption is invalid.
     */
    Transaction processTransaction(Cart cart, double paymentAmount,
        int pointsToRedeem);

    /**
     * Generates a formatted string representation of the transaction suitable for printing as a receipt.
     *
     * @param transaction The completed Transaction object.
     * @return A string containing the formatted receipt details.
     */
    String generateReceipt(Transaction transaction);

    /**
     * Saves the generated receipt content to a specified file path.
     *
     * @param transaction The completed Transaction object.
     * @param filePath The destination path for the receipt file.
     * @return True if the file was saved successfully, false otherwise.
     * @throws IOException if an error occurs during file writing.
     */
    boolean saveReceiptToFile(Transaction transaction, String filePath)
        throws IOException;

    /**
     * Retrieves a transaction record by its unique identifier.
     *
     * @param transactionId The ID of the transaction to find.
     * @return An Optional containing the Transaction if found, or an empty Optional otherwise.
     */
    Optional<Transaction> findById(String transactionId);

    /**
     * Retrieves all transaction records stored in the system.
     *
     * @return A List of all Transaction objects.
     */
    List<Transaction> findAll();

    /**
     * Retrieves all transactions associated with a specific customer ID.
     *
     * @param customerId The ID of the customer whose transactions are to be retrieved.
     * @return A List of Transaction objects belonging to the specified customer.
     */
    List<Transaction> findByCustomerId(String customerId);

    /**
     * Retrieves all transactions that occurred on a specific date.
     *
     * @param date The LocalDate to filter transactions by.
     * @return A List of transactions completed on the given date.
     */
    List<Transaction> findByDate(LocalDate date);

    /**
     * Retrieves all transactions that occurred within a specific range of dates (inclusive).
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A List of transactions completed within the specified date range.
     */
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Calculates the cumulative total sales amount across all stored transactions.
     *
     * @return The total sales revenue (sum of all final transaction totals).
     */
    double getTotalSales();

    /**
     * Calculates the total sales amount for a specific date.
     *
     * @param date The LocalDate for which to calculate total sales.
     * @return The total sales revenue for that date.
     */
    double getTotalSalesByDate(LocalDate date);

    /**
     * Calculates the cumulative total sales amount within a specific range of dates (inclusive).
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return The total sales revenue for the specified date range.
     */
    double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Persists all transaction data to the underlying storage mechanism.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    boolean saveTransactions();

    /**
     * Loads all transaction data from the underlying storage mechanism into memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    boolean loadTransactions();
}

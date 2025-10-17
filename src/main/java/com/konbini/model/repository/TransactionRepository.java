package com.konbini.model.repository;

import com.konbini.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining the contract for data access and reporting operations related to the Transaction model.
 * Implementations manage the persistent storage of completed sales transactions and provide
 * methods for retrieval by ID, customer, date, and sales reporting.
 */
public interface TransactionRepository {
    /**
     * Persists a completed transaction record to the repository.
     *
     * @param transaction The Transaction object to be added.
     */
    void addTransaction(Transaction transaction);

    /**
     * Finds and retrieves a transaction by its unique identifier.
     *
     * @param transactionId The ID of the transaction to find.
     * @return An Optional containing the Transaction if found, or an empty Optional otherwise.
     */
    Optional<Transaction> findById(String transactionId);

    /**
     * Retrieves all transaction records stored in the repository.
     *
     * @return A List of all Transaction objects.
     */
    List<Transaction> findAll();

    /**
     * Finds and retrieves all transactions associated with a specific customer ID.
     *
     * @param customerId The ID of the customer whose transactions are to be retrieved.
     * @return A List of Transaction objects belonging to the specified customer.
     */
    List<Transaction> findByCustomerId(String customerId);

    /**
     * Finds and retrieves all transactions that occurred on a specific date.
     *
     * @param date The LocalDate to filter transactions by.
     * @return A List of transactions completed on the given date.
     */
    List<Transaction> findByDate(LocalDate date);

    /**
     * Finds and retrieves all transactions that occurred within a specific range of dates (inclusive).
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
     * Persists the current state of the repository data to its storage mechanism (e.g., file, database).
     *
     * @return True if the save operation was successful, false otherwise.
     */
    boolean save();

    /**
     * Loads the repository data from its persistent storage mechanism into memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    boolean load();
}

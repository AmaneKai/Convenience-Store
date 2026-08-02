package com.konbini.domain.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for transactions and sales reporting.
 */
public interface TransactionRepository {

    /**
     * Persists a completed transaction.
     *
     * @param transaction the transaction to add
     */
    void add(Transaction transaction);

    /**
     * Finds a transaction by ID.
     *
     * @param transactionId the transaction ID
     * @return an Optional containing the transaction if found
     */
    Optional<Transaction> findById(String transactionId);

    /**
     * Returns all transactions.
     *
     * @return all transactions
     */
    List<Transaction> findAll();

    /**
     * Finds transactions belonging to a customer.
     *
     * @param customerId the customer ID
     * @return matching transactions
     */
    List<Transaction> findByCustomerId(String customerId);

    /**
     * Finds transactions on a specific date.
     *
     * @param date the date
     * @return matching transactions
     */
    List<Transaction> findByDate(LocalDate date);

    /**
     * Finds transactions within an inclusive date range.
     *
     * @param startDate the inclusive start date
     * @param endDate the inclusive end date
     * @return matching transactions
     */
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate);
}

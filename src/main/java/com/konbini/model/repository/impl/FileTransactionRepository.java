package com.konbini.model.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.model.Transaction;
import com.konbini.model.repository.TransactionRepository;

/**
 * FileTransactionRepository provides a file-based implementation of the TransactionRepository interface.
 * This implementation stores transaction data in a serialized file format and maintains an in-memory
 * cache of transaction objects. It supports transaction management, customer-specific queries,
 * date-based filtering, and sales reporting capabilities.
 */
public class FileTransactionRepository implements TransactionRepository {
    /** In-memory cache of transactions stored by transaction ID */
    private final Map<String, Transaction> transactions;

    /** File path where transaction data is persisted */
    private final String filePath;

    /**
     * Constructs a new FileTransactionRepository with the specified file path.
     * Initializes the in-memory transaction cache.
     *
     * @param filePath the file path where transaction data will be stored and loaded from
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public FileTransactionRepository(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.transactions = new HashMap<>();
        this.filePath = filePath;
    }

    /**
     * Adds a new transaction to the repository.
     * The transaction is added to the in-memory cache but not automatically persisted to disk.
     *
     * @param transaction the Transaction object to add
     * @throws IllegalArgumentException if transaction is null
     */
    @Override
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactions.put(transaction.getId(), transaction);
    }

    /**
     * Finds a transaction by its ID.
     *
     * @param transactionId the ID of the transaction to find
     * @return an Optional containing the Transaction if found, empty Optional otherwise
     */
    @Override
    public Optional<Transaction> findById(String transactionId) {
        Optional<Transaction> temp = Optional.empty();

        if (transactionId != null && !transactionId.trim().isEmpty()) {
            temp = Optional.ofNullable(transactions.get(transactionId));
        }

        return temp;
    }

    /**
     * Retrieves all transactions from the repository.
     *
     * @return a List containing all Transaction objects in the repository
     */
    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    /**
     * Finds transactions by customer ID.
     * Retrieves all transactions associated with a specific customer.
     *
     * @param customerId the ID of the customer to filter by
     * @return a List of transactions for the specified customer, empty list if none found
     */
    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        List<Transaction> temp = new ArrayList<>();

        if (customerId != null && !customerId.trim().isEmpty()) {
            temp = transactions.values().stream()
                    .filter(transaction -> transaction.getCustomer().getId().equals(customerId))
                    .collect(Collectors.toList());
        }

        return temp;
    }

    /**
     * Finds transactions by specific date.
     * Retrieves all transactions that occurred on the specified date.
     *
     * @param date the date to filter transactions by
     * @return a List of transactions for the specified date, empty list if none found
     */
    @Override
    public List<Transaction> findByDate(LocalDate date) {
        List<Transaction> temp = new ArrayList<>();

        if (date != null) {
            temp = transactions.values().stream()
                    .filter(transaction -> transaction.getTimestamp().toLocalDate().equals(date))
                    .collect(Collectors.toList());
        }

        return temp;
    }

    /**
     * Finds transactions within a date range.
     * Retrieves all transactions that occurred between the start and end dates (inclusive).
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a List of transactions within the specified date range, empty list if none found
     */
    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> temp = new ArrayList<>();

        if (startDate != null && endDate != null) {
            temp = transactions.values().stream()
                    .filter(transaction -> {
                        LocalDate transactionDate = transaction.getTimestamp().toLocalDate();
                        return (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate));
                    })
                    .collect(Collectors.toList());
        }

        return temp;
    }

    /**
     * Calculates the total sales amount across all transactions.
     *
     * @return the sum of all transaction totals as a double
     */
    @Override
    public double getTotalSales() {
        return transactions.values().stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

    /**
     * Calculates the total sales amount for a specific date.
     *
     * @param date the date to calculate sales for
     * @return the sum of transaction totals for the specified date as a double
     */
    @Override
    public double getTotalSalesByDate(LocalDate date) {
        return findByDate(date).stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

    /**
     * Calculates the total sales amount for a date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return the sum of transaction totals within the specified date range as a double
     */
    @Override
    public double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return findByDateRange(startDate, endDate).stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

    /**
     * Saves all transaction data to the file system.
     * Serializes the current in-memory transaction cache to the specified file path.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean save() {
        boolean temp = false;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(transactions.values()));
            temp = true;
        } catch (IOException e) {
            System.err.println("Error saving transaction data to file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
        }

        return temp;
    }

    /**
     * Loads transaction data from the file system.
     * Deserializes transaction data from the specified file path into the in-memory cache.
     * If the file doesn't exist, the operation fails silently and returns false.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    public boolean load() {
        boolean temp = false;
        File file = new File(filePath);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                List<Transaction> loadedTransactions = (List<Transaction>) ois.readObject();

                if (loadedTransactions != null) {
                    transactions.clear();
                    loadedTransactions.forEach(transaction -> {
                        if (transaction != null) {
                            transactions.put(transaction.getId(), transaction);
                        }
                    });
                    temp = true;
                } else {
                    transactions.clear();
                }
            } catch (IOException e) {
                System.err.println("Error reading transaction data from file: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Transaction class definition mismatch: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error loading transaction data: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            }
        }

        return temp;
    }
}
package com.konbini.model.repository.impl;

import com.konbini.model.Transaction;
import com.konbini.model.repository.TransactionRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the TransactionRepository interface that uses file serialization
 * for persistence. Transaction data is stored in memory using a Map for fast access
 * and saved/loaded from a file on the disk using Java's built-in serialization mechanism.
 * This class also provides methods for sales reporting and querying by date and customer.
 */
public class FileTransactionRepository implements TransactionRepository {
    /**
     * In-memory storage for transaction records, mapped by their unique ID.
     */
    private final Map<String, Transaction> transactions;
    /**
     * The file path used for saving and loading the serialized transaction data.
     */
    private final String filePath;

    /**
     * Constructs a new FileTransactionRepository.
     * Initializes the in-memory map and sets the path for persistent storage.
     *
     * @param filePath The path to the file where transaction data will be serialized.
     */
    public FileTransactionRepository(String filePath) {
        this.transactions = new HashMap<>();
        this.filePath = filePath;
    }

    /**
     * Adds a completed transaction record to the in-memory repository.
     *
     * @param transaction The Transaction object to be added.
     */
    @Override
    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }

    /**
     * Finds and retrieves a transaction by its unique identifier.
     *
     * @param transactionId The ID of the transaction to find.
     * @return An Optional containing the Transaction if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }

    /**
     * Retrieves all transaction records stored in the repository.
     *
     * @return A new List containing all Transaction objects.
     */
    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    /**
     * Finds and retrieves all transactions associated with a specific customer ID.
     *
     * @param customerId The ID of the customer whose transactions are to be retrieved.
     * @return A List of Transaction objects belonging to the specified customer.
     */
    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getCustomer()
                .getId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * Finds and retrieves all transactions that occurred on a specific date.
     *
     * @param date The LocalDate to filter transactions by.
     * @return A List of transactions completed on the given date.
     */
    @Override
    public List<Transaction> findByDate(LocalDate date) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getTimestamp()
                .toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Finds and retrieves all transactions that occurred within a specific range of dates (inclusive).
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A List of transactions completed within the specified date range.
     */
    @Override
    public List<Transaction> findByDateRange
        (LocalDate startDate, LocalDate endDate) {
        return transactions.values().stream()
                .filter(transaction -> {
                    LocalDate transactionDate = transaction
                            .getTimestamp().toLocalDate();
                    // Check if date is on or after startDate AND on or before endDate
                    return (transactionDate.isEqual(startDate) ||
                            transactionDate.isAfter(startDate)) &&
                           (transactionDate.isEqual(endDate) ||
                            transactionDate.isBefore(endDate));
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculates the cumulative total sales amount across all stored transactions.
     *
     * @return The total sales revenue (sum of all final transaction totals).
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
     * @param date The LocalDate for which to calculate total sales.
     * @return The total sales revenue for that date.
     */
    @Override
    public double getTotalSalesByDate(LocalDate date) {
        return findByDate(date).stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

    /**
     * Calculates the cumulative total sales amount within a specific range of dates (inclusive).
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return The total sales revenue for the specified date range.
     */
    @Override
    public double getTotalSalesByDateRange(LocalDate startDate,
        LocalDate endDate) {
        return findByDateRange(startDate, endDate).stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

    /**
     * Serializes and persists the current in-memory transaction data to the configured file path.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    @Override
    public boolean save() {
        try (ObjectOutputStream oos = new ObjectOutputStream
            (new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(transactions.values()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the transaction data from the serialized file into the in-memory repository.
     * If the file does not exist, the load operation fails silently, and the repository remains empty.
     *
     * @return True if the load operation was successful, false otherwise (including file not found or deserialization errors).
     */
    @Override
    public boolean load() {
        File file = new File(filePath);

        if (!file.exists()) {
            // It's not an error if the data file doesn't exist on first run
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream
            (new FileInputStream(file))) {
            List<Transaction> loadedTransactions = (List<Transaction>) ois
                .readObject();
            transactions.clear();
            loadedTransactions.forEach(transaction -> transactions
                .put(transaction.getId(), transaction));
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}

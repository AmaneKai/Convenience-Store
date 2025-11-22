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

public class FileTransactionRepository implements TransactionRepository {
    private final Map<String, Transaction> transactions;
    private final String filePath;

    public FileTransactionRepository(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.transactions = new HashMap<>();
        this.filePath = filePath;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        Optional<Transaction> temp = Optional.empty();

        if (transactionId != null && !transactionId.trim().isEmpty()) {
            temp = Optional.ofNullable(transactions.get(transactionId));
        }

        return temp;
    }
    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

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
    @Override
    public double getTotalSales() {
        return transactions.values().stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

    @Override
    public double getTotalSalesByDate(LocalDate date) {
        return findByDate(date).stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

    @Override
    public double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        return findByDateRange(startDate, endDate).stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }

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
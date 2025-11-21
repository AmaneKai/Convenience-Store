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
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(transactions.get(transactionId));
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return transactions.values().stream()
                .filter(transaction -> transaction.getCustomer().getId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByDate(LocalDate date) {
        if (date == null) {
            return new ArrayList<>();
        }
        return transactions.values().stream()
                .filter(transaction -> transaction.getTimestamp().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        return transactions.values().stream()
                .filter(transaction -> {
                    LocalDate transactionDate = transaction.getTimestamp().toLocalDate();
                    return (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                           (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate));
                })
                .collect(Collectors.toList());
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(transactions.values()));
            return true;
        } catch (IOException e) {
            System.err.println("Error saving transaction data to file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean load() {
        File file = new File(filePath);

        if (!file.exists()) {
            return false; 
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<Transaction> loadedTransactions = (List<Transaction>) ois.readObject();
            
            if (loadedTransactions == null) {
                transactions.clear();
                return false;
            }

            transactions.clear();
            loadedTransactions.forEach(transaction -> {
                if (transaction != null) {
                    transactions.put(transaction.getId(), transaction);
                }
            });
            return true;
        } catch (IOException e) {
            System.err.println("Error reading transaction data from file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Transaction class definition mismatch: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error loading transaction data: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        }
    }
}
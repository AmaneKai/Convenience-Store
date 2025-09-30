package com.konbini.model.repository.impl;

import com.konbini.model.Transaction;
import com.konbini.model.repository.TransactionRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FileTransactionRepository implements TransactionRepository {
    private final Map<String, Transaction> transactions;
    private final String filePath;
    
    public FileTransactionRepository(String filePath) {
        this.transactions = new HashMap<>();
        this.filePath = filePath;
    }
    
    @Override
    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
    }
    
    @Override
    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }
    
    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }
    
    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getCustomer()
                .getId().equals(customerId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByDate(LocalDate date) {
        return transactions.values().stream()
                .filter(transaction -> transaction.getTimestamp()
                .toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Transaction> findByDateRange
        (LocalDate startDate, LocalDate endDate) {
        return transactions.values().stream()
                .filter(transaction -> {
                    LocalDate transactionDate = transaction
                            .getTimestamp().toLocalDate();
                    return (transactionDate.isEqual(startDate) || 
                            transactionDate.isAfter(startDate)) &&
                           (transactionDate.isEqual(endDate) || 
                            transactionDate.isBefore(endDate));
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
    public double getTotalSalesByDateRange(LocalDate startDate, 
        LocalDate endDate) {
        return findByDateRange(startDate, endDate).stream()
                .mapToDouble(Transaction::getTotal)
                .sum();
    }
    
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
    
    @Override
    public boolean load() {
        File file = new File(filePath);
        
        if (!file.exists()) {
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

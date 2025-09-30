package com.konbini.model.repository;

import com.konbini.model.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    void addTransaction(Transaction transaction);
    Optional<Transaction> findById(String transactionId);
    List<Transaction> findAll();
    List<Transaction> findByCustomerId(String customerId);
    List<Transaction> findByDate(LocalDate date);
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate);
    double getTotalSales();
    double getTotalSalesByDate(LocalDate date);
    double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate);
    boolean save();
    boolean load();
}

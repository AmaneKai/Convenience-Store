package com.konbini.service;

import com.konbini.model.Cart;
import com.konbini.model.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction processTransaction(Cart cart, double paymentAmount, 
        int pointsToRedeem);
    String generateReceipt(Transaction transaction);
    boolean saveReceiptToFile(Transaction transaction, String filePath) 
        throws IOException;
    Optional<Transaction> findById(String transactionId);
    List<Transaction> findAll();
    List<Transaction> findByCustomerId(String customerId);
    List<Transaction> findByDate(LocalDate date);
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate);
    double getTotalSales();
    double getTotalSalesByDate(LocalDate date);
    double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate);
    boolean saveTransactions();
    boolean loadTransactions();
}

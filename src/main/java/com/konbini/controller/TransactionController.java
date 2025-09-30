package com.konbini.controller;

import com.konbini.model.Cart;
import com.konbini.model.Transaction;
import com.konbini.service.TransactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionController {
    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    public Transaction processTransaction(Cart cart, double paymentAmount, 
        int pointsToRedeem) {
        return transactionService.processTransaction(cart, paymentAmount, 
            pointsToRedeem);
    }
    
    public String generateReceipt(Transaction transaction) {
        return transactionService.generateReceipt(transaction);
    }
    
    public void saveReceiptToFile(Transaction transaction, String filePath) {
        try {
            transactionService.saveReceiptToFile(transaction, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save receipt to file: " 
                + e.getMessage(), e);
        }
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionService.findAll();
    }
    
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionService.findById(transactionId);
    }
    
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionService.findByCustomerId(customerId);
    }
    
    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactionService.findByDate(date);
    }
    
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, 
        LocalDate endDate) {
        return transactionService.findByDateRange(startDate, endDate);
    }
    
    public double getTotalSales() {
        return transactionService.getTotalSales();
    }
    
    public double getTotalSalesByDate(LocalDate date) {
        return transactionService.getTotalSalesByDate(date);
    }
    
    public double getTotalSalesByDateRange(LocalDate startDate, 
        LocalDate endDate) {
        return transactionService.getTotalSalesByDateRange(startDate, endDate);
    }
    
    public boolean saveData() {
        return transactionService.saveTransactions();
    }
    
    public boolean loadData() {
        return transactionService.loadTransactions();
    }
}

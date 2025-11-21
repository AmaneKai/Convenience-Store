package com.konbini.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Product;
import com.konbini.model.Receipt;
import com.konbini.model.Transaction;
import com.konbini.model.repository.TransactionRepository;
import com.konbini.service.TransactionService;
import com.konbini.service.discount.PointsRedemptionStrategy;
import com.konbini.service.discount.SeniorDiscountStrategy;
import com.konbini.service.tax.VATTaxStrategy;


public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        if (transactionRepository == null) {
            throw new IllegalArgumentException("Transaction repository cannot be null");
        }
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction processTransaction(Cart cart, double paymentAmount, int pointsToRedeem) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        
        validateCartInventory(cart);
        
        Transaction.Builder transactionBuilder = new Transaction.Builder(cart.getCustomer(), cart)
            .withTaxStrategy(new VATTaxStrategy());
        
        transactionBuilder.addDiscountStrategy(new SeniorDiscountStrategy());
        
        if (pointsToRedeem > 0) {
            PointsRedemptionStrategy pointsStrategy = new PointsRedemptionStrategy(pointsToRedeem);
            if (pointsStrategy.isApplicable(cart.getCustomer())) {
                transactionBuilder.addDiscountStrategy(pointsStrategy);
                transactionBuilder.withPointsRedeemed(pointsToRedeem);
            }
        }
        
        transactionBuilder.withPointsEarned();
        
        transactionBuilder.withPayment(paymentAmount);
        
        Transaction transaction = transactionBuilder.build();
        transactionRepository.addTransaction(transaction);
        
        return transaction;
    }

    @Override
    public String generateReceipt(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        Receipt receipt = new Receipt(transaction);
        return receipt.generateReceiptText();
    }

    @Override
    public boolean saveReceiptToFile(Transaction transaction, String filePath) throws IOException {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        String receiptText = generateReceipt(transaction);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(receiptText);
            return true;
        }
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return Optional.empty();
        }
        return transactionRepository.findById(transactionId);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return transactionRepository.findAll();
        }
        return transactionRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Transaction> findByDate(LocalDate date) {
        if (date == null) {
            return transactionRepository.findAll();
        }
        return transactionRepository.findByDate(date);
    }

    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return transactionRepository.findAll();
        }
        return transactionRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public double getTotalSales() {
        double total = 0.0;
        for (Transaction transaction : transactionRepository.findAll()) {
            total += transaction.getTotal();
        }
        return total;
    }

    @Override
    public double getTotalSalesByDate(LocalDate date) {
        if (date == null) {
            return 0.0;
        }
        
        double total = 0.0;
        for (Transaction transaction : transactionRepository.findByDate(date)) {
            total += transaction.getTotal();
        }
        return total;
    }

    @Override
    public double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0.0;
        }
        
        double total = 0.0;
        for (Transaction transaction : transactionRepository.findByDateRange(startDate, endDate)) {
            total += transaction.getTotal();
        }
        return total;
    }

    @Override
    public boolean saveTransactions() {
        return transactionRepository.save();
    }

    @Override
    public boolean loadTransactions() {
        return transactionRepository.load();
    }

   private void validateCartInventory(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            
            if (product == null) {
                throw new IllegalArgumentException("Cart contains null product");
            }
            
            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException(
                    "Insufficient quantity for product: " + product.getName() + 
                    ". Available: " + product.getQuantity() + 
                    ", Requested: " + item.getQuantity());
            }
            
            if (product.isExpired()) {
                throw new IllegalArgumentException("Product is expired: " + product.getName());
            }
        }
    }
}
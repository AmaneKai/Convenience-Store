package com.konbini.service.impl;

import com.konbini.model.*;
import com.konbini.model.repository.ProductRepository;
import com.konbini.model.repository.TransactionRepository;
import com.konbini.service.TransactionService;
import com.konbini.service.discount.PointsRedemptionStrategy;
import com.konbini.service.discount.SeniorDiscountStrategy;
import com.konbini.service.tax.VATTaxStrategy;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    
    public TransactionServiceImpl(TransactionRepository transactionRepository, 
        ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }
    
    @Override
    public Transaction processTransaction(Cart cart, double paymentAmount, int pointsToRedeem) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        
        // Validate and update inventory
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            
            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient quantity for product: " + product.getName());
            }
            
            if (product.isExpired()) {
                throw new IllegalArgumentException("Product is expired: " + product.getName());
            }
            
            // Update product quantity
            product.decreaseQuantity(item.getQuantity());
            productRepository.updateProduct(product);
        }
        
        // Create transaction builder with tax strategy
        Transaction.Builder transactionBuilder = new Transaction.Builder(cart.getCustomer(), cart)
            .withTaxStrategy(new VATTaxStrategy());
        
        // Apply senior discount if applicable
        transactionBuilder.addDiscountStrategy(new SeniorDiscountStrategy());
        
        // Apply points redemption if applicable
        if (pointsToRedeem > 0) {
            PointsRedemptionStrategy pointsStrategy = new PointsRedemptionStrategy(pointsToRedeem);
            
            if (pointsStrategy.isApplicable(cart.getCustomer())) {
                transactionBuilder.addDiscountStrategy(pointsStrategy);
                transactionBuilder.withPointsRedeemed(pointsToRedeem);
                pointsStrategy.processRedemption(cart.getCustomer());
            }
        }
        
        // Calculate points earned
        transactionBuilder.withPointsEarned();
        
        // Process payment
        transactionBuilder.withPayment(paymentAmount);
        
        // Build the transaction
        Transaction transaction = transactionBuilder.build();
        
        // Save the transaction
        transactionRepository.addTransaction(transaction);
        
        return transaction;
    }
    
    @Override
    public String generateReceipt(Transaction transaction) {
        Receipt receipt = new Receipt(transaction);
        return receipt.generateReceiptText();
    }
    
    @Override
    public boolean saveReceiptToFile(Transaction transaction, 
        String filePath) throws IOException {
        String receiptText = generateReceipt(transaction);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(receiptText);
            return true;
        }
    }
    
    @Override
    public Optional<Transaction> findById(String transactionId) {
        return transactionRepository.findById(transactionId);
    }
    
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
    
    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }
    
    @Override
    public List<Transaction> findByDate(LocalDate date) {
        return transactionRepository.findByDate(date);
    }
    
    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, 
        LocalDate endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }
    
    @Override
    public double getTotalSales() {
        return transactionRepository.getTotalSales();
    }
    
    @Override
    public double getTotalSalesByDate(LocalDate date) {
        return transactionRepository.getTotalSalesByDate(date);
    }
    
    @Override
    public double getTotalSalesByDateRange(LocalDate startDate, 
        LocalDate endDate) {
        return transactionRepository
            .getTotalSalesByDateRange(startDate, endDate);
    }
    
    @Override
    public boolean saveTransactions() {
        return transactionRepository.save();
    }
    
    @Override
    public boolean loadTransactions() {
        return transactionRepository.load();
    }
}

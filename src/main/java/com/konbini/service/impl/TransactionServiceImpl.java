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
        boolean temp = false;

        if (transaction != null && filePath != null && !filePath.trim().isEmpty()) {
            String receiptText = generateReceipt(transaction);
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.print(receiptText);
                temp = true;
            }
        } else {
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction cannot be null");
            }
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        return temp;
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        Optional<Transaction> temp = Optional.empty();

        if (transactionId != null && !transactionId.trim().isEmpty()) {
            temp = transactionRepository.findById(transactionId);
        }

        return temp;
    }

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        List<Transaction> temp;

        if (customerId != null && !customerId.trim().isEmpty()) {
            temp = transactionRepository.findByCustomerId(customerId);
        } else {
            temp = transactionRepository.findAll();
        }

        return temp;
    }

    @Override
    public List<Transaction> findByDate(LocalDate date) {
        List<Transaction> temp;

        if (date != null) {
            temp = transactionRepository.findByDate(date);
        } else {
            temp = transactionRepository.findAll();
        }

        return temp;
    }

    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> temp;

        if (startDate != null && endDate != null) {
            temp = transactionRepository.findByDateRange(startDate, endDate);
        } else {
            temp = transactionRepository.findAll();
        }

        return temp;
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
        double total = 0.0;

        if (date != null) {
            for (Transaction transaction : transactionRepository.findByDate(date)) {
                total += transaction.getTotal();
            }
        }

        return total;
    }

    @Override
    public double getTotalSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        double total = 0.0;

        if (startDate != null && endDate != null) {
            for (Transaction transaction : transactionRepository.findByDateRange(startDate, endDate)) {
                total += transaction.getTotal();
            }
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
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

/**
 * TransactionServiceImpl provides business logic implementation for transaction processing and management.
 * This service handles transaction processing, receipt generation, sales reporting, and transaction data persistence.
 * It applies business rules including tax calculation, discount strategies, and inventory validation.
 */
public class TransactionServiceImpl implements TransactionService {
    /** Repository for transaction data persistence operations */
    private final TransactionRepository transactionRepository;

    /**
     * Constructs a new TransactionServiceImpl with the specified transaction repository.
     *
     * @param transactionRepository the TransactionRepository for data access operations
     * @throws IllegalArgumentException if transactionRepository is null
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        if (transactionRepository == null) {
            throw new IllegalArgumentException("Transaction repository cannot be null");
        }
        this.transactionRepository = transactionRepository;
    }

    /**
     * Processes a complete transaction including validation, discount application, and payment.
     * Applies VAT tax, senior citizen discounts, and points redemption as applicable.
     *
     * @param cart the Cart containing items to purchase
     * @param paymentAmount the amount paid by the customer
     * @param pointsToRedeem the number of loyalty points to redeem (0 if none)
     * @return the processed Transaction object
     * @throws IllegalArgumentException if cart is null, empty, or inventory validation fails
     */
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

    /**
     * Generates a formatted receipt text for a transaction.
     *
     * @param transaction the Transaction to generate receipt for
     * @return formatted receipt text as a String
     * @throws IllegalArgumentException if transaction is null
     */
    @Override
    public String generateReceipt(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        Receipt receipt = new Receipt(transaction);
        return receipt.generateReceiptText();
    }

    /**
     * Saves a transaction receipt to a text file.
     *
     * @param transaction the Transaction to save receipt for
     * @param filePath the file path where the receipt should be saved
     * @return true if the save operation was successful, false otherwise
     * @throws IOException if file writing fails
     * @throws IllegalArgumentException if transaction or filePath is null or empty
     */
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

    /**
     * Finds a transaction by its ID.
     *
     * @param transactionId the ID of the transaction to find
     * @return an Optional containing the Transaction if found, empty Optional otherwise
     */
    @Override
    public Optional<Transaction> findById(String transactionId) {
        Optional<Transaction> temp = Optional.empty();

        if (transactionId != null && !transactionId.trim().isEmpty()) {
            temp = transactionRepository.findById(transactionId);
        }

        return temp;
    }

    /**
     * Retrieves all transactions from the system.
     *
     * @return a List containing all Transaction objects
     */
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    /**
     * Finds transactions by customer ID.
     * Returns all transactions if customerId is null or empty.
     *
     * @param customerId the ID of the customer to filter by
     * @return a List of transactions for the specified customer, or all transactions if customerId is invalid
     */
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

    /**
     * Finds transactions by specific date.
     * Returns all transactions if date is null.
     *
     * @param date the date to filter transactions by
     * @return a List of transactions for the specified date, or all transactions if date is null
     */
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

    /**
     * Finds transactions within a date range.
     * Returns all transactions if startDate or endDate is null.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a List of transactions within the specified date range, or all transactions if dates are invalid
     */
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

    /**
     * Calculates the total sales amount across all transactions.
     *
     * @return the sum of all transaction totals as a double
     */
    @Override
    public double getTotalSales() {
        double total = 0.0;
        for (Transaction transaction : transactionRepository.findAll()) {
            total += transaction.getTotal();
        }
        return total;
    }

    /**
     * Calculates the total sales amount for a specific date.
     *
     * @param date the date to calculate sales for
     * @return the sum of transaction totals for the specified date as a double
     */
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

    /**
     * Calculates the total sales amount for a date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return the sum of transaction totals within the specified date range as a double
     */
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

    /**
     * Saves all transaction data to persistent storage.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean saveTransactions() {
        return transactionRepository.save();
    }

    /**
     * Loads all transaction data from persistent storage.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    public boolean loadTransactions() {
        return transactionRepository.load();
    }

    /**
     * Validates that sufficient inventory is available for all items in the cart.
     * Checks for null products, insufficient quantities, and expired products.
     *
     * @param cart the Cart to validate inventory for
     * @throws IllegalArgumentException if any item fails inventory validation
     */
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
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

/**
 * Concrete implementation of the TransactionService interface.
 * This class handles the complete lifecycle of a sales transaction, from validating
 * inventory and applying business rules (tax, discounts, points) to recording the
 * final transaction and generating receipts. It relies on TransactionRepository
 * and ProductRepository for data access.
 */
public class TransactionServiceImpl implements TransactionService {
    /** The repository for managing transaction records. */
    private final TransactionRepository transactionRepository;
    /** The repository for managing and updating product inventory. */
    private final ProductRepository productRepository;

    /**
     * Constructs a TransactionServiceImpl, injecting the necessary repositories.
     *
     * @param transactionRepository The repository for transaction persistence.
     * @param productRepository The repository for product inventory management.
     */
    public TransactionServiceImpl(TransactionRepository transactionRepository,
        ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    /**
     * Finalizes and records a sales transaction.
     * This method includes inventory validation, applying the VATTaxStrategy,
     * applying the SeniorDiscountStrategy, handling loyalty points redemption,
     * calculating points earned, and processing payment. It updates both
     * product inventory and customer loyalty points before saving the transaction.
     *
     * @param cart The Cart containing the items and customer details.
     * @param paymentAmount The amount of money tendered by the customer.
     * @param pointsToRedeem The number of loyalty points the customer wishes to use.
     * @return The finalized, immutable Transaction object.
     * @throws IllegalArgumentException if the cart is empty, inventory is insufficient/expired, or payment is insufficient.
     */
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

            // Update product quantity (this logic is duplicated here and in Transaction.Builder to handle persistence)
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
                // Note: The builder's addDiscountStrategy handles the redemption calculation,
                // but setting points redeemed directly and processing redemption ensures data is captured
                // and customer object is updated (though the builder's addDiscountStrategy handles the update as well).
                // Re-calling processRedemption is redundant given the current Transaction.Builder logic.
                // For robustness, we stick to the builder's flow.
                // The pointsRedeemed must be set manually here or in the builder since the strategy only calculates the amount.
                transactionBuilder.withPointsRedeemed(pointsToRedeem);
                // Explicitly call to deduct points from customer object for consistency
                // pointsStrategy.processRedemption(cart.getCustomer()); 
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

    /**
     * Generates a formatted string representation of the transaction suitable for printing as a receipt.
     * Delegates the formatting to the Receipt class.
     *
     * @param transaction The completed Transaction object.
     * @return A string containing the formatted receipt details.
     */
    @Override
    public String generateReceipt(Transaction transaction) {
        Receipt receipt = new Receipt(transaction);
        return receipt.generateReceiptText();
    }

    /**
     * Saves the generated receipt content to a specified file path.
     *
     * @param transaction The completed Transaction object.
     * @param filePath The destination path for the receipt file.
     * @return True if the file was saved successfully, false otherwise.
     * @throws IOException if an error occurs during file writing.
     */
    @Override
    public boolean saveReceiptToFile(Transaction transaction,
        String filePath) throws IOException {
        String receiptText = generateReceipt(transaction);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(receiptText);
            return true;
        }
    }

    /**
     * Retrieves a transaction record by its unique identifier.
     * Delegates directly to the repository.
     *
     * @param transactionId The ID of the transaction to find.
     * @return An Optional containing the Transaction if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Transaction> findById(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

    /**
     * Retrieves all transaction records stored in the system.
     * Delegates directly to the repository.
     *
     * @return A List of all Transaction objects.
     */
    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    /**
     * Retrieves all transactions associated with a specific customer ID.
     * Delegates directly to the repository.
     *
     * @param customerId The ID of the customer whose transactions are to be retrieved.
     * @return A List of Transaction objects belonging to the specified customer.
     */
    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }

    /**
     * Retrieves all transactions that occurred on a specific date.
     * Delegates directly to the repository.
     *
     * @param date The LocalDate to filter transactions by.
     * @return A List of transactions completed on the given date.
     */
    @Override
    public List<Transaction> findByDate(LocalDate date) {
        return transactionRepository.findByDate(date);
    }

    /**
     * Retrieves all transactions that occurred within a specific range of dates (inclusive).
     * Delegates directly to the repository.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return A List of transactions completed within the specified date range.
     */
    @Override
    public List<Transaction> findByDateRange(LocalDate startDate,
        LocalDate endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Calculates the cumulative total sales amount across all stored transactions.
     * Delegates directly to the repository.
     *
     * @return The total sales revenue (sum of all final transaction totals).
     */
    @Override
    public double getTotalSales() {
        return transactionRepository.getTotalSales();
    }

    /**
     * Calculates the total sales amount for a specific date.
     * Delegates directly to the repository.
     *
     * @param date The LocalDate for which to calculate total sales.
     * @return The total sales revenue for that date.
     */
    @Override
    public double getTotalSalesByDate(LocalDate date) {
        return transactionRepository.getTotalSalesByDate(date);
    }

    /**
     * Calculates the cumulative total sales amount within a specific range of dates (inclusive).
     * Delegates directly to the repository.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @return The total sales revenue for the specified date range.
     */
    @Override
    public double getTotalSalesByDateRange(LocalDate startDate,
        LocalDate endDate) {
        return transactionRepository
            .getTotalSalesByDateRange(startDate, endDate);
    }

    /**
     * Persists all transaction data to the underlying storage mechanism.
     * Delegates the save operation to the repository.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    @Override
    public boolean saveTransactions() {
        return transactionRepository.save();
    }

    /**
     * Loads all transaction data from the underlying storage mechanism into memory.
     * Delegates the load operation to the repository.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    @Override
    public boolean loadTransactions() {
        return transactionRepository.load();

    }
}

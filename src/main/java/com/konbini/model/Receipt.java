package com.konbini.model;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates formatted receipt text for transactions.
 * Creates a professionally formatted receipt with transaction details,
 * item breakdown, pricing, discounts, and loyalty point information.
 */
public class Receipt {

    private final Transaction transaction;

    /**
     * Constructs a Receipt generator for the specified transaction.
     *
     * @param transaction the transaction to generate a receipt for
     */
    public Receipt(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Generates a formatted receipt text for the transaction.
     * Includes store header, transaction details, itemized list,
     * financial breakdown, and loyalty point information.
     *
     * @return a formatted string representing the receipt
     */
    public String generateReceiptText() {
        StringBuilder receipt = new StringBuilder();

        // Store header
        receipt.append("===================================\n");
        receipt.append("          KONBINI STORE           \n");
        receipt.append("===================================\n\n");

        // Transaction information
        receipt.append("Receipt #: ")
                .append(transaction.getId()).append("\n");
        receipt.append("Date: ")
                .append(transaction.getTimestamp()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n");
        receipt.append("Customer: ").append(transaction
                .getCustomer().getName()).append("\n\n");

        // Items section
        receipt.append("Items:\n");
        receipt.append("-----------------------------------\n");
        for (CartItem item : transaction.getItems()) {
            Product product = item.getProduct();
            double subtotal = item.getSubtotal();

            receipt.append(String.format("%-20s x%d\n",
                product.getName(), item.getQuantity()));
            receipt.append(String.format("  ₱%.2f x %d = ₱%.2f\n",
                product.getPrice(), item.getQuantity(), subtotal));
        }
        receipt.append("-----------------------------------\n\n");

        // Financial breakdown
        receipt.append(String.format("Subtotal: ₱%.2f\n",
            transaction.getSubtotal()));
        receipt.append(String.format("%s: ₱%.2f\n",
            transaction.getTaxName(), transaction.getTax()));

        // Discounts section
        if (transaction.getDiscount() > 0) {
            receipt.append(String.format("Discount: ₱%.2f\n",
                transaction.getDiscount()));

            // Add applied discounts details
            List<String> appliedDiscounts = transaction.getAppliedDiscounts();
            if (appliedDiscounts != null && !appliedDiscounts.isEmpty()) {
                for (String discount : appliedDiscounts) {
                    receipt.append(String.format("  - %s\n", discount));
                }
            }
        }

        receipt.append(String.format("Total: ₱%.2f\n\n",
            transaction.getTotal()));

        // Payment information
        receipt.append(String.format("Amount Paid: ₱%.2f\n",
            transaction.getAmountPaid()));
        receipt.append(String.format("Change: ₱%.2f\n\n",
            transaction.getChange()));

        // Loyalty points section (if customer has membership)
        if (transaction.getCustomer().hasMembershipCard()) {
            if (transaction.getPointsRedeemed() > 0) {
                receipt.append(String.format("Points Redeemed: %d\n",
                    transaction.getPointsRedeemed()));
            }

            if (transaction.getPointsEarned() > 0) {
                receipt.append(String.format("Points Earned: %d\n",
                    transaction.getPointsEarned()));
            }

            receipt.append(String.format("Current Points Balance: %d\n\n",
                    transaction.getCustomer()
                            .getMembershipCard().getPoints()));
        }

        // Footer
        receipt.append("===================================\n");
        receipt.append("         Thank You! Come Again!    \n");
        receipt.append("===================================\n");

        return receipt.toString();
    }
}
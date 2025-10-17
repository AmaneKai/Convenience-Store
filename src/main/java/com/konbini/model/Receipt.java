package com.konbini.model;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class responsible for formatting and generating a printable text receipt
 * based on a completed Transaction.
 * It pulls all necessary details from the Transaction object, including items,
 * financial totals, customer information, and loyalty points summary.
 */
public class Receipt {
    /**
     * The completed transaction record used to generate the receipt.
     */
    private final Transaction transaction;

    /**
     * Constructs a Receipt generator using a specific Transaction.
     *
     * @param transaction The Transaction object containing all purchase details.
     */
    public Receipt(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Generates a formatted multi-line String representing the sales receipt.
     * The receipt includes store branding, transaction ID, date, customer name,
     * itemized list, financial summary (subtotal, tax, discount, total, paid, change),
     * and loyalty points activity if applicable.
     *
     * @return A formatted String suitable for console output or printing.
     */
    public String generateReceiptText() {
        StringBuilder receipt = new StringBuilder();

        receipt.append("===================================\n");
        receipt.append("          KONBINI STORE           \n");
        receipt.append("===================================\n\n");

        receipt.append("Receipt #: ")
                .append(transaction.getId()).append("\n");
        receipt.append("Date: ")
                .append(transaction.getTimestamp()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n");
        receipt.append("Customer: ").append(transaction
                .getCustomer().getName()).append("\n\n");

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

        receipt.append(String.format("Subtotal: ₱%.2f\n",
            transaction.getSubtotal()));
        receipt.append(String.format("%s: ₱%.2f\n",
            transaction.getTaxName(), transaction.getTax()));

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

        receipt.append(String.format("Amount Paid: ₱%.2f\n",
            transaction.getAmountPaid()));
        receipt.append(String.format("Change: ₱%.2f\n\n",
            transaction.getChange()));

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

        receipt.append("===================================\n");
        receipt.append("         Thank You! Come Again!    \n");
        receipt.append("===================================\n");

        return receipt.toString();
    }
}

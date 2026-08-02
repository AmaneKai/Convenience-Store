package com.konbini.domain.transaction;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formats a {@link Transaction} into a human-readable receipt string.
 */
public class Receipt {

    private final Transaction transaction;

    /**
     * Constructs a receipt renderer for a transaction.
     *
     * @param transaction the transaction to render
     * @throws IllegalArgumentException if the transaction is null
     */
    public Receipt(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        this.transaction = transaction;
    }

    /**
     * Renders the full receipt text.
     *
     * @return the formatted receipt
     */
    public String generateReceiptText() {
        StringBuilder receipt = new StringBuilder();

        receipt.append("===================================\n");
        receipt.append("           KONBINI STORE           \n");
        receipt.append("===================================\n\n");

        receipt.append("Receipt #: ").append(transaction.getId()).append("\n");
        receipt.append("Date: ")
                .append(transaction.getTimestamp()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n");
        receipt.append("Customer: ").append(transaction.getCustomer().getName()).append("\n\n");

        receipt.append("Items:\n");
        receipt.append("-----------------------------------\n");
        for (CartItem item : transaction.getItems()) {
            String productName = item.getProduct().getName();
            int quantity = item.getQuantity();
            BigDecimal unitPrice = item.getProduct().getPrice();
            BigDecimal subtotal = item.getSubtotal();

            receipt.append(String.format("%-20s x%d\n", productName, quantity));
            receipt.append(String.format("   %s x %d = %s\n",
                    money(unitPrice), quantity, money(subtotal)));
        }
        receipt.append("-----------------------------------\n\n");

        receipt.append(String.format("Subtotal: %s\n", money(transaction.getSubtotal())));
        receipt.append(String.format("%s: %s\n", transaction.getTaxName(), money(transaction.getTax())));

        if (transaction.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            receipt.append(String.format("Discount: %s\n", money(transaction.getDiscount())));
            List<String> appliedDiscounts = transaction.getAppliedDiscounts();
            for (String discount : appliedDiscounts) {
                receipt.append(String.format("   - %s\n", discount));
            }
        }

        receipt.append(String.format("Total: %s\n\n", money(transaction.getTotal())));
        receipt.append(String.format("Amount Paid: %s\n", money(transaction.getAmountPaid())));
        receipt.append(String.format("Change: %s\n\n", money(transaction.getChange())));

        if (transaction.getCustomer().hasMembershipCard()) {
            if (transaction.getPointsRedeemed() > 0) {
                receipt.append(String.format("Points Redeemed: %d\n", transaction.getPointsRedeemed()));
            }
            if (transaction.getPointsEarned() > 0) {
                receipt.append(String.format("Points Earned: %d\n", transaction.getPointsEarned()));
            }
            receipt.append(String.format("Current Points Balance: %d\n\n",
                    transaction.getCustomer().getMembershipCard().getPoints()));
        }

        receipt.append("===================================\n");
        receipt.append("         Thank You! Come Again!     \n");
        receipt.append("===================================\n");

        return receipt.toString();
    }

    /**
     * Formats an amount as a peso currency string.
     *
     * @param amount the amount
     * @return the formatted string
     */
    private String money(BigDecimal amount) {
        return "₱" + amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}

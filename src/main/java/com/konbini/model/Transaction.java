package com.konbini.model;

import com.konbini.service.discount.DiscountStrategy;
import com.konbini.service.discount.PointsRedemptionStrategy;
import com.konbini.service.tax.TaxStrategy;
import com.konbini.util.IdGenerator;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a final, immutable record of a completed sales transaction.
 * This class stores all necessary data for accounting and analysis, including
 * customer details, items purchased, and the final calculated financials (tax,
 * discount, total, payment, and change). The Transaction is created using the
 * Builder pattern to ensure correct calculation order and final immutability.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The unique identifier for this transaction. */
    private final String id;
    /** The customer who made the purchase. */
    private final Customer customer;
    /** The finalized list of items purchased. */
    private final List<CartItem> items;
    /** The date and time the transaction was completed. */
    private final LocalDateTime timestamp;
    /** The sum of all item prices before any tax or discount. */
    private final double subtotal;
    /** The final tax amount applied. */
    private final double tax;
    /** The total monetary discount applied. */
    private final double discount;
    /** The final price due (subtotal + tax - discount). */
    private final double total;
    /** The actual amount of money tendered by the customer. */
    private final double amountPaid;
    /** The difference returned to the customer (amountPaid - total). */
    private final double change;
    /** The number of loyalty points earned by the customer in this transaction. */
    private final int pointsEarned;
    /** The number of loyalty points redeemed by the customer in this transaction. */
    private final int pointsRedeemed;
    /** A list of names describing the discounts that were applied. */
    private final List<String> appliedDiscounts;
    /** The name of the tax applied (e.g., "VAT"). */
    private final String taxName;

    /**
     * Builder class for constructing an immutable Transaction object.
     * This ensures thread safety and allows for a flexible, step-by-step
     * calculation of all financial fields before the Transaction is finalized.
     */
    public static class Builder {
        private final String id;
        private final Customer customer;
        private final List<CartItem> items;
        private final LocalDateTime timestamp;
        private final double subtotal;

        private double tax = 0;
        private double discount = 0;
        private double total = 0;
        private double amountPaid = 0;
        private double change = 0;
        private int pointsEarned = 0;
        private int pointsRedeemed = 0;
        private List<String> appliedDiscounts = new ArrayList<>();
        private String taxName = "Tax";
        private TaxStrategy taxStrategy;
        private List<DiscountStrategy> discountStrategies = new ArrayList<>();

        /**
         * Initializes the Builder with the mandatory customer and cart data.
         * This step generates the transaction ID, sets the timestamp, copies
         * cart items, deducts the purchased quantities from product stock, and
         * sets the initial subtotal and total.

         *
         * @param customer The Customer making the purchase.
         * @param cart The Cart containing the items.
         */
        public Builder(Customer customer, Cart cart) {

            if (customer == null) {
                throw new IllegalArgumentException("Customer cannot be null");
            }
            if (cart == null) {
                throw new IllegalArgumentException("Cart cannot be null");
            }
            if (cart.isEmpty()) {
                throw new IllegalArgumentException("Cart cannot be empty");
            }
            
            this.id = IdGenerator.getInstance().generateId("transaction");
            this.customer = customer;
            this.items = new ArrayList<>();

            // Copy items from cart and decrease product quantities
            for (CartItem cartItem : cart.getItems()) {
                Product product = cartItem.getProduct();
                int quantity = cartItem.getQuantity();

                // Restore this line for test compatibility
                product.decreaseQuantity(quantity);

                // Add a new item instance to the transaction record
                this.items.add(new CartItem(product, quantity));
            }

            this.timestamp = LocalDateTime.now();
            this.subtotal = cart.getSubtotal();
            this.total = subtotal; // Initial total before tax/discount
        }
       /**
         * Applies a tax strategy to calculate tax based on the subtotal.
         * Updates the tax amount, tax name, and recalculates the total.
         *
         * @param taxStrategy The TaxStrategy implementation to use.
         * @return The current Builder instance for chaining.
         */
        public Builder withTaxStrategy(TaxStrategy taxStrategy) {
            this.taxStrategy = taxStrategy;
            this.tax = taxStrategy.calculateTax(subtotal);
            this.taxName = taxStrategy.getName();
            this.total = subtotal + tax - discount;
            return this;
        }
        /**
         * Applies an applicable discount strategy.
         * Updates the discount amount, records the discount name, processes
         * points redemption if applicable, and recalculates the total.
         *
         * @param discountStrategy The DiscountStrategy to attempt to apply.
         * @return The current Builder instance for chaining.
        */
        public Builder addDiscountStrategy(DiscountStrategy discountStrategy) {
            if (discountStrategy.isApplicable(customer)) {
                discountStrategies.add(discountStrategy);
                double strategyDiscount = discountStrategy.calculateDiscount(subtotal);
                this.discount += strategyDiscount;
                this.appliedDiscounts.add(discountStrategy.getName());

                // If it's a points redemption strategy, process the redemption and update points redeemed
                if (discountStrategy instanceof PointsRedemptionStrategy) {
                    PointsRedemptionStrategy prs = (PointsRedemptionStrategy) discountStrategy;
                    prs.processRedemption(customer);
                    // Update pointsRedeemed field - FIX: Call getPointsToRedeem() instead of getPointsRedeemed()
                    this.pointsRedeemed = prs.getPointsToRedeem();
                }

                this.total = subtotal + tax - discount;
            }
            return this;
        }
        /**
         * Applies a tax rate directly to the subtotal.
         * Note: This method is a simplified alternative to using a TaxStrategy.
         *
         * @param taxRate The tax rate (e.g., 0.12 for 12%).
         * @return The current Builder instance for chaining.
         * @deprecated This method is marked for deprecation as using TaxStrategy is preferred.
         */
        @Deprecated
        public Builder withTax(double taxRate) {
            this.tax = subtotal * taxRate;
            this.total = subtotal + tax - discount;
            return this;
        }

        /**
         * Applies a senior citizen discount if the customer is eligible.
         *
         * @param discountRate The discount rate (e.g., 0.20 for 20%).
         * @return The current Builder instance for chaining.
         * @deprecated This logic is better handled by a dedicated DiscountStrategy.
         */
        @Deprecated
        public Builder withSeniorDiscount(double discountRate) {
            if (customer.isSeniorCitizen()) {
                double seniorDiscount = subtotal * discountRate;
                this.discount += seniorDiscount;
                this.appliedDiscounts.add("Senior Citizen Discount");
                this.total = subtotal + tax - discount;
            }
            return this;
        }

        /**
         * Applies a points redemption discount using the customer's membership card.
         * This deducts points from the card and adds a discount to the transaction total.
         *
         * @param points The number of points to redeem (1 point = 1 unit of currency).
         * @return The current Builder instance for chaining.
         * @deprecated This logic is better handled by a dedicated PointsRedemptionStrategy.
         */
        @Deprecated
        public Builder withPointsRedemption(int points) {
            if (customer.hasMembershipCard() && customer
                .getMembershipCard().getPoints() >= points) {
                this.pointsRedeemed = points;
                this.discount += points; // 1 point = 1 peso
                this.appliedDiscounts.add("Points Redemption");
                this.total = subtotal + tax - discount;
                customer.getMembershipCard().deductPoints(points);
            }
            return this;
        }

        /**
         * Sets the total points redeemed. This is often used by a PointsRedemptionStrategy.
         *
         * @param points The number of points redeemed.
         * @return The current Builder instance for chaining.
         */
        public Builder withPointsRedeemed(int points) {
            this.pointsRedeemed = points;
            return this;
        }

        /**
         * Calculates and applies loyalty points earned based on the final total.
         * The earned points are added to the customer's membership card.
         * Rule: 1 point earned per P50 spent.
         *
         * @return The current Builder instance for chaining.
         */
        public Builder withPointsEarned() {
            if (customer.hasMembershipCard()) {
                this.pointsEarned = (int) (total / 50); // 1 point per P50
                customer.getMembershipCard().addPoints(pointsEarned);
            }
            return this;
        }

        /**
         * Finalizes the payment details. Calculates the change.
         * This must be the final step before calling build().
         *
         * @param amount The amount of money paid by the customer.
         * @return The current Builder instance for chaining.
         * @throws IllegalArgumentException if the payment amount is insufficient.
         */
        public Builder withPayment(double amount) {
            if (amount < total) {
                throw new IllegalArgumentException
                ("Payment amount is insufficient");
            }

            this.amountPaid = amount;
            this.change = amount - total;
            return this;
        }

        /**
         * Constructs the final immutable Transaction object.
         *
         * @return A new, fully calculated Transaction instance.
         */
        public Transaction build() {
            if (amountPaid < total) {
                throw new IllegalArgumentException("Payment amount is less than total");
            }
            if (amountPaid <= 0) {
                throw new IllegalArgumentException("Payment amount must be greater than 0");
            }
             
            return new Transaction(this);
        }
        
    }

    /**
     * Private constructor used by the Builder to create an immutable Transaction.
     *
     * @param builder The configured Builder instance.
     */
    private Transaction(Builder builder) {
        this.id = builder.id;
        this.customer = builder.customer;
        this.items = builder.items;
        this.timestamp = builder.timestamp;
        this.subtotal = builder.subtotal;
        this.tax = builder.tax;
        this.discount = builder.discount;
        this.total = builder.total;
        this.amountPaid = builder.amountPaid;
        this.change = builder.change;
        this.pointsEarned = builder.pointsEarned;
        this.pointsRedeemed = builder.pointsRedeemed;
        this.appliedDiscounts = builder.appliedDiscounts;
        this.taxName = builder.taxName;
    }

    /**
     * Retrieves the unique identifier of the transaction.
     * @return The transaction ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the customer associated with the transaction.
     * @return The Customer object.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Retrieves the list of items purchased in the transaction.
     * @return The list of CartItem records.
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * Retrieves the timestamp of the transaction completion.
     * @return The LocalDateTime of the transaction.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Retrieves the total price of all items before tax and discount.
     * @return The transaction subtotal.
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Retrieves the final tax amount applied to the transaction.
     * @return The tax amount.
     */
    public double getTax() {
        return tax;
    }

    /**
     * Retrieves the name of the tax applied (e.g., "VAT").
     * @return The tax name string.
     */
    public String getTaxName() {
        return taxName;
    }

    /**
     * Retrieves the total monetary discount applied to the transaction.
     * @return The total discount amount.
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Retrieves the list of names for all discounts that were applied.
     * @return A list of descriptive discount strings.
     */
    public List<String> getAppliedDiscounts() {
        return appliedDiscounts;
    }

    /**
     * Retrieves the final total amount due (subtotal + tax - discount).
     * @return The final total price.
     */
    public double getTotal() {
        return total;
    }

    /**
     * Retrieves the amount of money paid by the customer.
     * @return The amount paid.
     */
    public double getAmountPaid() {
        return amountPaid;
    }

    /**
     * Retrieves the change given back to the customer.
     * @return The change amount.
     */
    public double getChange() {
        return change;
    }

    /**
     * Retrieves the number of loyalty points earned in this transaction.
     * @return The points earned.
     */
    public int getPointsEarned() {
        return pointsEarned;
    }

    /**
     * Retrieves the number of loyalty points redeemed in this transaction.
     * @return The points redeemed.
     */
    public int getPointsRedeemed() {
        return pointsRedeemed;
    }
}

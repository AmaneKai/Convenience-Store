package com.konbini.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.konbini.service.discount.DiscountStrategy;
import com.konbini.service.discount.PointsRedemptionStrategy;
import com.konbini.service.tax.TaxStrategy;
import com.konbini.util.IdGenerator;

/**
 * Represents a completed sales transaction with all financial details and customer information.
 * Uses the Builder pattern for flexible transaction creation with various tax and discount strategies.
 * Implements Serializable to support persistence.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final Customer customer;
    private final List<CartItem> items;
    private final LocalDateTime timestamp;
    private final double subtotal;
    private final double tax;
    private final double discount;
    private final double total;
    private final double amountPaid;
    private final double change;
    private final int pointsEarned;
    private final int pointsRedeemed;
    private final List<String> appliedDiscounts;
    private final String taxName;

    /**
     * Builder class for constructing Transaction objects with flexible configuration.
     * Handles tax calculation, discount application, payment processing, and inventory management.
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
        private List<DiscountStrategy> discountStrategies = new ArrayList<>();
        private boolean inventoryDecremented = false;
        private TaxStrategy taxStrategy;

        /**
         * Constructs a new Builder for creating a Transaction.
         *
         * @param customer the customer making the purchase
         * @param cart the cart containing items to purchase
         * @throws IllegalArgumentException if customer or cart is null, or if cart is empty
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

            // Create copies of cart items to preserve state at transaction time
            for (CartItem cartItem : cart.getItems()) {
                this.items.add(new CartItem(cartItem.getProduct(), cartItem.getQuantity()));
            }

            this.timestamp = LocalDateTime.now();
            this.subtotal = cart.getSubtotal();
            this.total = subtotal;
        }

        /**
         * Applies a tax strategy to calculate tax for the transaction.
         *
         * @param taxStrategy the tax strategy to apply
         * @return the Builder instance for method chaining
         * @throws IllegalArgumentException if taxStrategy is null
         */
        public Builder withTaxStrategy(TaxStrategy taxStrategy) {
            if (taxStrategy == null) {
                throw new IllegalArgumentException("Tax strategy cannot be null");
            }
            this.taxStrategy = taxStrategy;
            this.taxName = taxStrategy.getName();
            recalculateTaxAndTotal();
            return this;
        }

        /**
         * Recalculates tax and total.
         * Tax is calculated on the original subtotal, then discounts are deducted from the total.
         */
        private void recalculateTaxAndTotal() {
            if (taxStrategy != null) {
                this.tax = taxStrategy.calculateTax(subtotal);
            }
            this.total = subtotal + tax - discount;
        }

        /**
         * Adds a discount strategy to the transaction if applicable to the customer.
         *
         * @param discountStrategy the discount strategy to apply
         * @return the Builder instance for method chaining
         * @throws IllegalArgumentException if discountStrategy is null
         */
        public Builder addDiscountStrategy(DiscountStrategy discountStrategy) {
            if (discountStrategy == null) {
                throw new IllegalArgumentException("Discount strategy cannot be null");
            }

            if (!discountStrategy.isApplicable(customer)) {
                return this;
            }

            discountStrategies.add(discountStrategy);
            double strategyDiscount = discountStrategy.calculateDiscount(subtotal);
            this.discount += strategyDiscount;
            this.appliedDiscounts.add(discountStrategy.getName());

            if (discountStrategy instanceof PointsRedemptionStrategy) {
                PointsRedemptionStrategy prs = (PointsRedemptionStrategy) discountStrategy;
                this.pointsRedeemed = prs.getPointsToRedeem();
                prs.processRedemption(customer);
            }

            recalculateTaxAndTotal();
            return this;
        }

        /**
         * Applies a tax rate to the transaction (deprecated - use withTaxStrategy instead).
         *
         * @param taxRate the tax rate to apply (e.g., 0.12 for 12%)
         * @return the Builder instance for method chaining
         * @deprecated Use withTaxStrategy(TaxStrategy) instead for more flexible tax calculation
         */
        @Deprecated
        public Builder withTax(double taxRate) {
            this.tax = subtotal * taxRate;
            this.total = subtotal + tax - discount;
            return this;
        }

        /**
         * Applies senior citizen discount if customer is eligible (deprecated).
         *
         * @param discountRate the discount rate to apply
         * @return the Builder instance for method chaining
         * @deprecated Use addDiscountStrategy with appropriate discount strategy instead
         */
        @Deprecated
        public Builder withSeniorDiscount(double discountRate) {
            if (customer.isSeniorCitizen()) {
                double seniorDiscount = subtotal * discountRate;
                this.discount += seniorDiscount;
                this.appliedDiscounts.add("Senior Citizen Discount");
                recalculateTaxAndTotal();
            }
            return this;
        }

        /**
         * Applies points redemption if customer has sufficient points (deprecated).
         *
         * @param points the number of points to redeem
         * @return the Builder instance for method chaining
         * @deprecated Use addDiscountStrategy with PointsRedemptionStrategy instead
         */
        @Deprecated
        public Builder withPointsRedemption(int points) {
            if (customer.hasMembershipCard() && customer
                .getMembershipCard().getPoints() >= points) {
                this.pointsRedeemed = points;
                this.discount += points;
                this.appliedDiscounts.add("Points Redemption");
                recalculateTaxAndTotal();
                customer.getMembershipCard().deductPoints(points);
            }
            return this;
        }

        /**
         * Sets the number of points redeemed in this transaction.
         *
         * @param points the number of points redeemed
         * @return the Builder instance for method chaining
         * @throws IllegalArgumentException if points is negative
         */
        public Builder withPointsRedeemed(int points) {
            if (points < 0) {
                throw new IllegalArgumentException("Points redeemed cannot be negative");
            }
            this.pointsRedeemed = points;
            return this;
        }

        /**
         * Calculates and sets points earned based on transaction total.
         * Awards 1 point for every ₱50 spent.
         *
         * @return the Builder instance for method chaining
         */
        public Builder withPointsEarned() {
            if (customer.hasMembershipCard() && !customer.getMembershipCard().isExpired()) {
                this.pointsEarned = (int) (total / 50);
            }
            return this;
        }

        /**
         * Processes payment for the transaction.
         *
         * @param amount the amount paid by the customer
         * @return the Builder instance for method chaining
         * @throws IllegalArgumentException if amount is insufficient for the total
         */
        public Builder withPayment(double amount) {
            if (amount < total) {
                throw new IllegalArgumentException("Payment amount is insufficient");
            }

            this.amountPaid = amount;
            this.change = amount - total;
            return this;
        }

        /**
         * Builds and returns the completed Transaction.
         * Decrements inventory and awards loyalty points if applicable.
         *
         * @return the completed Transaction object
         * @throws IllegalArgumentException if payment amount is insufficient or invalid
         */
        public Transaction build() {
            // Ensure tax and total are correctly calculated with all discounts applied
            recalculateTaxAndTotal();

            if (amountPaid < total) {
                throw new IllegalArgumentException("Payment amount is less than total");
            }
            if (amountPaid <= 0) {
                throw new IllegalArgumentException("Payment amount must be greater than 0");
            }

            // Decrement inventory for all purchased items
            if (!inventoryDecremented) {
                for (CartItem item : items) {
                    Product product = item.getProduct();
                    int quantity = item.getQuantity();
                    product.decreaseQuantity(quantity);
                }
                inventoryDecremented = true;
            }

            // Award loyalty points if customer has valid membership card
            if (customer.hasMembershipCard() && !customer.getMembershipCard().isExpired()) {
                if (pointsEarned > 0) {
                    customer.getMembershipCard().addPoints(pointsEarned);
                }
            }

            Transaction transaction = new Transaction(this);
            return transaction;
        }
    }

    /**
     * Private constructor used by the Builder pattern.
     *
     * @param builder the Builder containing all transaction data
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

    // Getters - all return immutable or copied data to preserve transaction integrity

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<CartItem> getItems() { return new ArrayList<>(items); }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public String getTaxName() { return taxName; }
    public double getDiscount() { return discount; }
    public List<String> getAppliedDiscounts() { return new ArrayList<>(appliedDiscounts); }
    public double getTotal() { return total; }
    public double getAmountPaid() { return amountPaid; }
    public double getChange() { return change; }
    public int getPointsEarned() { return pointsEarned; }
    public int getPointsRedeemed() { return pointsRedeemed; }
}
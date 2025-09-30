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
        
        public Builder(Customer customer, Cart cart) {
            this.id = IdGenerator.getInstance().generateId("transaction");
            this.customer = customer;
            this.items = new ArrayList<>();
            
            // Copy items from cart and decrease product quantities
            for (CartItem cartItem : cart.getItems()) {
                Product product = cartItem.getProduct();
                int quantity = cartItem.getQuantity();
                
                // Decrease product quantity
                product.decreaseQuantity(quantity);
                
                // Add item to transaction
                this.items.add(new CartItem(product, quantity));
            }
            
            this.timestamp = LocalDateTime.now();
            this.subtotal = cart.getSubtotal();
            this.total = subtotal;
        }
        
        public Builder withTaxStrategy(TaxStrategy taxStrategy) {
            this.taxStrategy = taxStrategy;
            this.tax = taxStrategy.calculateTax(subtotal);
            this.taxName = taxStrategy.getName();
            this.total = subtotal + tax - discount;
            return this;
        }
        
        public Builder addDiscountStrategy(DiscountStrategy discountStrategy) {
            if (discountStrategy.isApplicable(customer)) {
                discountStrategies.add(discountStrategy);
                double strategyDiscount = discountStrategy
                    .calculateDiscount(subtotal);
                this.discount += strategyDiscount;
                this.appliedDiscounts.add(discountStrategy.getName());
                
                // If it's a points redemption strategy, process the redemption
                if (discountStrategy instanceof PointsRedemptionStrategy) {
                    ((PointsRedemptionStrategy) discountStrategy)
                        .processRedemption(customer);
                }
                
                this.total = subtotal + tax - discount;
            }
            return this;
        }
         
        public Builder withTax(double taxRate) {
            this.tax = subtotal * taxRate;
            this.total = subtotal + tax - discount;
            return this;
        }
        
        public Builder withSeniorDiscount(double discountRate) {
            if (customer.isSeniorCitizen()) {
                double seniorDiscount = subtotal * discountRate;
                this.discount += seniorDiscount;
                this.appliedDiscounts.add("Senior Citizen Discount");
                this.total = subtotal + tax - discount;
            }
            return this;
        }
        
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
        
        public Builder withPointsRedeemed(int points) {
            this.pointsRedeemed = points;
            return this;
        }
        
        public Builder withPointsEarned() {
            if (customer.hasMembershipCard()) {
                this.pointsEarned = (int) (total / 50); // 1 point per P50
                customer.getMembershipCard().addPoints(pointsEarned);
            }
            return this;
        }
        
        public Builder withPayment(double amount) {
            if (amount < total) {
                throw new IllegalArgumentException
                ("Payment amount is insufficient");
            }
            
            this.amountPaid = amount;
            this.change = amount - total;
            return this;
        }
        
        public Transaction build() {
            return new Transaction(this);
        }
    }
    
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
    
    public String getId() {
        return id;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public double getTax() {
        return tax;
    }
    
    public String getTaxName() {
        return taxName;
    }
    
    public double getDiscount() {
        return discount;
    }
    
    public List<String> getAppliedDiscounts() {
        return appliedDiscounts;
    }
    
    public double getTotal() {
        return total;
    }
    
    public double getAmountPaid() {
        return amountPaid;
    }
    
    public double getChange() {
        return change;
    }
    
    public int getPointsEarned() {
        return pointsEarned;
    }
    
    public int getPointsRedeemed() {
        return pointsRedeemed;
    }
}

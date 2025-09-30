package com.konbini.model;

import com.konbini.util.IdGenerator;
import java.io.Serializable;
import java.time.LocalDateTime;
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
        
        public Builder(Customer customer, Cart cart) {
            this.id = IdGenerator.getInstance().generateId("transaction");
            this.customer = customer;
            this.items = cart.getItems().stream()
                    .map(item -> new CartItem(item.getProduct(), item.getQuantity()))
                    .collect(Collectors.toList());
            this.timestamp = LocalDateTime.now();
            this.subtotal = cart.getSubtotal();
            this.total = subtotal;
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
                this.total = subtotal + tax - discount;
            }
            return this;
        }
        
        public Builder withPointsRedemption(int points) {
            if (customer.hasMembershipCard() && customer
                        .getMembershipCard().getPoints() >= points) {
                this.pointsRedeemed = points;
                this.discount += points; // 1 point = 1 peso
                this.total = subtotal + tax - discount;
                customer.getMembershipCard().deductPoints(points);
            }
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
    
    public double getDiscount() {
        return discount;
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

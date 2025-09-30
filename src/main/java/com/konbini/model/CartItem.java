package com.konbini.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Product product;
    private int quantity;
    
    public CartItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Cannot add more items than available in stock");
        }
        
        this.product = product;
        this.quantity = quantity;
    }
    
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity)  {
        if (quantity <= 0) {
            throw new IllegalArgumentException
            ("Quantity must be positive");
        }

        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException 
            ("Cannot add more items that available in stock");
        }

        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException
            ("Amount must be positive");
        }

        if (this.quantity - amount <= 0) {
            throw new IllegalArgumentException
            ("Cannot add more items than available in stock");
        }

        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException
            ("Amount must be positive");
        }

        if (this.quantity - amount <= 0) {
            throw new IllegalArgumentException
            ("Cannot decrease quantity to zero or less");
        }

        this.quantity -= amount;
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}

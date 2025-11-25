package com.konbini.model;

import java.io.Serializable;

/**
 * Represents an item in a shopping cart, containing a product and its quantity.
 * Provides functionality to manage quantity and calculate pricing for individual cart items.
 * Implements Serializable to support persistence.
 */
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Product product;
    private int quantity;

    /**
     * Constructs a new CartItem with the specified product and quantity.
     *
     * @param product the product to add to the cart
     * @param quantity the quantity of the product
     * @throws IllegalArgumentException if product is null, quantity is not positive,
     *                                  quantity exceeds available stock, or product is expired
     */
    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                "Cannot add more items than available in stock");
        }
        if (product.isExpired()) {
            throw new IllegalArgumentException(
                "Cannot add expired product: " + product.getName());
        }

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Gets the product associated with this cart item.
     *
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the quantity of the product in this cart item.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product in this cart item.
     *
     * @param quantity the new quantity to set
     * @throws IllegalArgumentException if quantity is not positive or exceeds available stock
     */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                "Cannot set quantity higher than available stock (" + product.getQuantity() + ")");
        }
        this.quantity = quantity;
    }

    /**
     * Increases the quantity of this cart item by the specified amount.
     *
     * @param amount the amount to increase the quantity by
     * @throws IllegalArgumentException if amount is not positive or the increase would exceed available stock
     */
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity + amount > product.getQuantity()) {
            throw new IllegalArgumentException(
                "Cannot add more items than available in stock");
        }
        this.quantity += amount;
    }

    /**
     * Decreases the quantity of this cart item by the specified amount.
     *
     * @param amount the amount to decrease the quantity by
     * @throws IllegalArgumentException if amount is not positive or the decrease would result in zero or negative quantity
     */
    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity - amount <= 0) {
            throw new IllegalArgumentException(
                "Cannot decrease quantity to zero or less");
        }
        this.quantity -= amount;
    }

    /**
     * Calculates the subtotal for this cart item (product price × quantity).
     *
     * @return the subtotal amount
     */
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}
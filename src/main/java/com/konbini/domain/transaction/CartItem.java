package com.konbini.domain.transaction;

import com.konbini.domain.product.Product;
import java.math.BigDecimal;
import lombok.Getter;

/**
 * Line item within a cart or transaction, associating a product with a quantity.
 */
@Getter
public class CartItem {

    private final Product product;
    private int quantity;

    /**
     * Constructs a cart item.
     *
     * @param product the product
     * @param quantity the quantity
     * @throws IllegalArgumentException if the product is null, the quantity is not
     *                                  positive, the quantity exceeds stock, or the product is expired
     */
    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Cannot add more items than available in stock");
        }
        if (product.isExpired()) {
            throw new IllegalArgumentException("Cannot add expired product: " + product.getName());
        }

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Changes the quantity.
     *
     * @param newQuantity the new quantity
     * @throws IllegalArgumentException if the quantity is not positive or exceeds stock
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (newQuantity > product.getQuantity()) {
            throw new IllegalArgumentException(
                    "Cannot set quantity higher than available stock (" + product.getQuantity() + ")");
        }
        this.quantity = newQuantity;
    }

    /**
     * Increases the quantity by the given amount.
     *
     * @param amount the amount to add
     * @throws IllegalArgumentException if the amount is not positive or exceeds stock
     */
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity + amount > product.getQuantity()) {
            throw new IllegalArgumentException("Cannot add more items than available in stock");
        }
        this.quantity += amount;
    }

    /**
     * Decreases the quantity by the given amount.
     *
     * @param amount the amount to remove
     * @throws IllegalArgumentException if the amount is not positive or would make the quantity zero
     */
    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity - amount <= 0) {
            throw new IllegalArgumentException("Cannot decrease quantity to zero or less");
        }
        this.quantity -= amount;
    }

    /**
     * Returns the line subtotal (unit price times quantity).
     *
     * @return the subtotal
     */
    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}

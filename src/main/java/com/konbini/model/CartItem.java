package com.konbini.model;

import java.io.Serializable;

/**
 * Represents a single line item within a shopping cart.
 * A CartItem links a specific Product to the quantity of that product
 * the customer intends to purchase, and calculates the line item's subtotal.
 * It enforces business rules related to minimum quantity and stock availability.
 */
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The product being purchased. This reference is final and cannot be changed.
     */
    private final Product product;
    /**
     * The quantity of the product the customer wishes to purchase.
     */
    private int quantity;

    /**
     * Constructs a new CartItem.
     *
     * @param product The Product being added to the cart.
     * @param quantity The initial quantity of the product.
     * @throws IllegalArgumentException if the quantity is not positive or exceeds available stock.
     */
    public CartItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException
            ("Quantity must be positive");
        }

        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException
            ("Cannot add more items than available in stock");
        }

        if (product.isExpired()) {
            throw new IllegalArgumentException
            ("Cannot add expired product: " + product.getName());
        }

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Retrieves the product associated with this cart item.
     *
     * @return The Product object.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Retrieves the quantity of the product in this cart item.
     *
     * @return The quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets a new, explicit quantity for the item.
     *
     * @param quantity The new quantity to set.
     * @throws IllegalArgumentException if the new quantity is zero or less, or exceeds available stock.
     */
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

    /**
     * Increases the quantity of the item by a specified amount.
     *
     * @param amount The positive amount to add to the current quantity.
     * @throws IllegalArgumentException if the amount is not positive, or if the resulting
     * quantity exceeds the available stock.
     */
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException
            ("Amount must be positive");
        }
        
        // Note: The original implementation check `this.quantity + amount <= 0` seems incorrect
        // as `this.quantity` is always positive. A stock check is needed here instead.
        if (this.quantity + amount > product.getQuantity()) {
            throw new IllegalArgumentException
            ("Cannot add more items than available in stock");
        }

        this.quantity += amount;
    }

    /**
     * Decreases the quantity of the item by a specified amount.
     *
     * @param amount The positive amount to subtract from the current quantity.
     * @throws IllegalArgumentException if the amount is not positive, or if the resulting
     * quantity is zero or less.
     */
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

    /**
     * Calculates the subtotal for this line item (price * quantity).
     *
     * @return The monetary subtotal for this item.
     */
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }
}

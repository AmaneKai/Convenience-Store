package com.konbini.domain.transaction;

import com.konbini.domain.customer.Customer;
import com.konbini.domain.product.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shopping cart belonging to a single customer, aggregating cart items and
 * computing subtotals.
 */
public class Cart {

    private final Customer customer;
    private final List<CartItem> items;

    /**
     * Constructs an empty cart for the given customer.
     *
     * @param customer the cart owner
     * @throws IllegalArgumentException if the customer is null
     */
    public Cart(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    /**
     * Returns an unmodifiable view of the items.
     *
     * @return the cart items
     */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns the cart owner.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Adds a product to the cart, merging with an existing line for the same product.
     *
     * @param product the product to add
     * @param quantity the quantity to add
     * @throws IllegalArgumentException if the product is null, the quantity is not
     *                                  positive, or the product is expired
     */
    public void addItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (product.isExpired()) {
            throw new IllegalArgumentException("Cannot add expired product to cart");
        }

        CartItem existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    /**
     * Removes a product from the cart by ID.
     *
     * @param productId the product ID to remove
     * @throws IllegalArgumentException if the ID is invalid or not present
     */
    public void removeItem(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new IllegalArgumentException("Product not found in cart: " + productId);
        }
    }

    /**
     * Updates the quantity of a product in the cart.
     *
     * @param productId the product ID
     * @param newQuantity the new quantity
     * @throws IllegalArgumentException if the product is not present or quantity is invalid
     */
    public void updateItemQuantity(String productId, int newQuantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem item = items.stream()
                .filter(candidate -> candidate.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart: " + productId));

        try {
            item.updateQuantity(newQuantity);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Cannot update quantity: " + exception.getMessage());
        }
    }

    /**
     * Clears all items from the cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Returns the total unit count across all lines.
     *
     * @return the total number of items
     */
    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * Returns the sum of all line subtotals.
     *
     * @return the cart subtotal
     */
    public BigDecimal getSubtotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns whether the cart holds no items.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}

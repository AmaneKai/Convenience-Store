package com.konbini.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a shopping cart that holds items for a specific customer.
 * Provides functionality to add, remove, and update items in the cart,
 * as well as calculate totals and manage cart state.
 * Implements Serializable to support persistence.
 */
public class Cart {
    private final List<CartItem> items;
    private final Customer customer;

    /**
     * Constructs a new Cart for the specified customer.
     *
     * @param customer the customer who owns this cart
     * @throws IllegalArgumentException if customer is null
     */
    public Cart(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    /**
     * Gets an unmodifiable view of the items in the cart.
     *
     * @return an unmodifiable list of cart items
     */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Gets the customer who owns this cart.
     *
     * @return the cart owner
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Adds a product to the cart with the specified quantity.
     * If the product already exists in the cart, increases its quantity.
     *
     * @param product the product to add to the cart
     * @param quantity the quantity to add
     * @throws IllegalArgumentException if product is null, quantity is not positive,
     *                                  or product is expired
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

        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(product, quantity);
            items.add(newItem);
        }
    }

    /**
     * Removes a product from the cart by its ID.
     *
     * @param productId the ID of the product to remove
     * @throws IllegalArgumentException if productId is null, empty, or not found in cart
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
     * Updates the quantity of a specific product in the cart.
     *
     * @param productId the ID of the product to update
     * @param newQuantity the new quantity for the product
     * @throws IllegalArgumentException if productId is null/empty, newQuantity is not positive,
     *                                  or product is not found in cart
     */
    public void updateItemQuantity(String productId, int newQuantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Optional<CartItem> itemOptional = items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemOptional.isPresent()) {
            try {
                itemOptional.get().setQuantity(newQuantity);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cannot update quantity: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Product not found in cart: " + productId);
        }
    }

    /**
     * Removes all items from the cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Gets the total number of items in the cart (sum of all quantities).
     *
     * @return the total number of items
     */
    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * Calculates the subtotal of all items in the cart.
     *
     * @return the cart subtotal (sum of all item subtotals)
     */
    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    /**
     * Checks if the cart is empty.
     *
     * @return true if the cart contains no items, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
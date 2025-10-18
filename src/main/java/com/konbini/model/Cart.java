package com.konbini.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a shopping cart for a specific customer.
 * This class tracks the products a customer intends to purchase, along with the
 * associated customer and methods for managing the cart contents and calculating totals.
 */
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The list of items currently in the cart.
     */
    private final List<CartItem> items;
    /**
     * The customer associated with this cart, used for applying discounts
     * and managing loyalty points.
     */
    private final Customer customer;

    /**
     * Constructs a new, empty cart associated with a specific customer.
     *
     * @param customer The Customer object associated with this cart.
     */
    public Cart(Customer customer) {
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    /**
     * Retrieves an unmodifiable copy of the list of items in the cart.
     *
     * @return A List of CartItem objects.
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Retrieves the customer associated with this cart.
     *
     * @return The Customer object.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Adds a product to the cart or increases the quantity of an existing item.
     * If the product is already in the cart, its quantity is increased by the
     * specified amount; otherwise, a new CartItem is added.
     *
     * @param product The Product to add.
     * @param quantity The number of units to add.
     */
    public void addItem(Product product, int quantity) {

        if (product == null) {
            throw new IllegalArgumentException
                ("Product cannot be null");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException
                ("Quantity must be greater than 0");
        }
        
        if (product.isExpired()) {
            throw new IllegalArgumentException
                ("Cannot add expired product to cart");
        }

        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getProduct().getId()
                .equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(product, quantity);
            items.add(newItem);
        }
    }

    /**
     * Removes all instances of a product from the cart based on its ID.
     *
     * @param productId The ID of the product to remove.
     */
    public void removeItem(String productId) {
        boolean removed = items.removeIf(item -> item
            .getProduct().getId().equals(productId));
    
        if (!removed) {
            throw new IllegalArgumentException
                ("Product not found in cart: " + productId);
        }
    }

    /**
     * Updates the quantity of a specific product in the cart.
     * If the new quantity is zero or less, the item should typically be removed
     * by a preceding or subsequent check in the business logic layer.
     *
     * @param productId The ID of the product whose quantity should be updated.
     * @param newQuantity The new total quantity for the item.
     */
    public void updateItemQuantity(String productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException
                ("Quantity must be greater than 0");
        }

        Optional<CartItem> itemOptional = items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        
        if (itemOptional.isPresent()) {
            CartItem item = itemOptional.get();
            
            try {
                item.setQuantity(newQuantity);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException
                    ("Cannot update quantity: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException
                ("Product not found in cart: " + productId);
        }

    }
    /**
     * Clears all items from the cart, leaving the cart empty but still
     * associated with the customer.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calculates the total number of individual product units in the cart.
     *
     * @return The sum of the quantities of all items.
     */
    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * Calculates the total monetary value of all items in the cart before
     * any transaction-level discounts, taxes, or point redemptions.
     *
     * @return The subtotal of the cart.
     */
    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    /**
     * Checks if the cart contains any items.
     *
     * @return True if the list of items is empty, false otherwise.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}

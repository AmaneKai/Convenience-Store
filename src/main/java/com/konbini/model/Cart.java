package com.konbini.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<CartItem> items;
    private final Customer customer;

    public Cart(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Customer getCustomer() {
        return customer;
    }

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

    public void removeItem(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new IllegalArgumentException("Product not found in cart: " + productId);
        }
    }

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

    public void clear() {
        items.clear();
    }

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
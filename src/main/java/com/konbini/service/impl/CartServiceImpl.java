package com.konbini.service.impl;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Customer;
import com.konbini.model.Product;
import com.konbini.service.CartService;

/**
 * CartServiceImpl provides business logic implementation for shopping cart operations.
 * This service handles cart calculations including discounts, taxes, and inventory validation
 * to ensure business rules are enforced during the checkout process.
 */
public class CartServiceImpl implements CartService {

    /**
     * Calculates the total amount for a cart including discounts and taxes.
     * Applies a 20% discount for senior citizens and calculates 12% VAT tax on the discounted subtotal.
     *
     * @param cart the Cart object containing items to calculate total for
     * @param customer the Customer object to check for senior citizen eligibility
     * @return the calculated total amount including discounts and taxes
     * @throws IllegalArgumentException if cart or customer is null, or if calculated total is invalid
     */
    @Override
    public double calculateTotal(Cart cart, Customer customer) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        double subtotal = cart.getSubtotal();

        double discountAmount = 0;
        if (customer.isSeniorCitizen()) {
            discountAmount = subtotal * 0.20;
        }

        double discountedSubtotal = subtotal - discountAmount;

        double tax = discountedSubtotal * 0.12;

        double total = discountedSubtotal + tax;

        if (total < 0) {
            throw new IllegalArgumentException("Invalid total calculation");
        }

        return total;
    }

    /**
     * Validates that sufficient inventory is available for all items in the cart.
     * Checks each cart item against the available stock of the corresponding product.
     * Throws an exception if any item quantity exceeds available inventory.
     *
     * @param cart the Cart object to validate inventory for
     * @throws IllegalArgumentException if cart is null or if any item exceeds available stock
     */
    @Override
    public void validateInventoryAvailable(Cart cart) throws IllegalArgumentException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int cartQuantity = item.getQuantity();
            int availableStock = product.getQuantity();

            if (cartQuantity > availableStock) {
                throw new IllegalArgumentException(
                        product.getName() + " only has " + availableStock +
                                " in stock, but you have " + cartQuantity + " in cart.");
            }
        }
    }
}
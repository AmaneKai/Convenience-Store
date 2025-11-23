package com.konbini.service;

import com.konbini.model.Cart;
import com.konbini.model.Customer;

/**
 * Service interface for cart-related business operations.
 * Handles cart total calculation with customer-specific discounts
 * and inventory validation before checkout.
 */
public interface CartService {

    /**
     * Calculates the total amount for a cart including applicable discounts.
     * Applies customer-specific discounts such as senior citizen discounts
     * and membership benefits.
     *
     * @param cart the cart containing items to calculate total for
     * @param customer the customer to apply eligible discounts for
     * @return the total amount after applying all applicable discounts
     */
    double calculateTotal(Cart cart, Customer customer);

    /**
     * Validates that all items in the cart have sufficient inventory available.
     * Checks that product quantities in the cart do not exceed available stock
     * and that no expired products are being purchased.
     *
     * @param cart the cart to validate
     * @throws IllegalArgumentException if any item has insufficient inventory
     *                                  or contains expired products
     */
    void validateInventoryAvailable(Cart cart) throws IllegalArgumentException;
}
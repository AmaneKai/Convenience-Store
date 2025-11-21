package com.konbini.service.impl;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Customer;
import com.konbini.model.Product;
import com.konbini.service.CartService;

public class CartServiceImpl implements CartService {

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
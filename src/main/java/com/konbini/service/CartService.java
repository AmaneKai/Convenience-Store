package com.konbini.service;

import com.konbini.model.Cart;
import com.konbini.model.Customer;

public interface CartService {
    double calculateTotal(Cart cart, Customer customer);

    void validateInventoryAvailable(Cart cart) throws IllegalArgumentException;
}
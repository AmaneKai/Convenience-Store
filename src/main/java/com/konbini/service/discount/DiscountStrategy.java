package com.konbini.service.discount;

import com.konbini.model.Customer;

public interface DiscountStrategy {
    String getName();

    double calculateDiscount(double subtotal);

    boolean isApplicable(Customer customer);
}

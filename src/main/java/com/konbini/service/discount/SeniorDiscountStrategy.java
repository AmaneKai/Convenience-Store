package com.konbini.service.discount;

import com.konbini.model.Customer;

public class SeniorDiscountStrategy implements DiscountStrategy {
    private static final double SENIOR_DISCOUNT_RATE = 0.20; // 20% discount

    @Override
    public String getName() {
        return "Senior Citizen Discount";
    }

    @Override
    public double calculateDiscount(double subtotal) {
        return subtotal * SENIOR_DISCOUNT_RATE;
    }

    @Override
    public boolean isApplicable(Customer customer) {
        return customer.isSeniorCitizen();
    }
}

package com.konbini.service.discount;

import com.konbini.model.Customer;

/**
 * Discount strategy for senior citizen customers.
 * Provides a 20% discount on the transaction subtotal for eligible senior citizens.
 */
public class SeniorDiscountStrategy implements DiscountStrategy {
    /**
     * The discount rate for senior citizens (20%).
     */
    private static final double SENIOR_DISCOUNT_RATE = 0.20; // 20% discount

    /**
     * {@inheritDoc}
     * Returns "Senior Citizen Discount" as the discount name.
     */
    @Override
    public String getName() {
        return "Senior Citizen Discount";
    }

    /**
     * {@inheritDoc}
     * Calculates discount as 20% of the transaction subtotal.
     *
     * @return the discount amount (20% of subtotal)
     */
    @Override
    public double calculateDiscount(double subtotal) {
        return subtotal * SENIOR_DISCOUNT_RATE;
    }

    /**
     * {@inheritDoc}
     * Applicable only to customers marked as senior citizens.
     *
     * @return true if the customer is a senior citizen, false otherwise
     */
    @Override
    public boolean isApplicable(Customer customer) {
        return customer.isSeniorCitizen();
    }
}
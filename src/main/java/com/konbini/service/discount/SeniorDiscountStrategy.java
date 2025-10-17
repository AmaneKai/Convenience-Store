package com.konbini.service.discount;

import com.konbini.model.Customer;

/**
 * Concrete implementation of the DiscountStrategy interface that applies a fixed
 * percentage discount for eligible senior citizens.
 * This strategy checks the customer's status and calculates the discount based on the subtotal.
 */
public class SeniorDiscountStrategy implements DiscountStrategy {
    /**
     * The fixed discount rate applied to senior citizens (e.g., 0.20 for 20%).
     */
    private static final double SENIOR_DISCOUNT_RATE = 0.20; // 20% discount

    /**
     * Retrieves the descriptive name of this discount strategy.
     *
     * @return The string "Senior Citizen Discount".
     */
    @Override
    public String getName() {
        return "Senior Citizen Discount";
    }

    /**
     * Calculates the monetary discount amount by applying the fixed senior discount rate
     * to the transaction subtotal.
     *
     * @param subtotal The transaction subtotal before tax and current discounts.
     * @return The calculated 20% discount amount.
     */
    @Override
    public double calculateDiscount(double subtotal) {
        return subtotal * SENIOR_DISCOUNT_RATE;
    }

    /**
     * Determines if the senior citizen discount is applicable to the given customer.
     * It is applicable only if the customer's record is flagged as a senior citizen.
     *
     * @param customer The Customer object to check for eligibility.
     * @return True if the customer is a senior citizen, false otherwise.
     */
    @Override
    public boolean isApplicable(Customer customer) {
        return customer.isSeniorCitizen();
    }
}

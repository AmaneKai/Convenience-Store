package com.konbini.service.discount;

import com.konbini.model.Customer;

/**
 * Defines the contract for applying various discount rules to a transaction.
 * Each implementation represents a specific type of discount (e.g., senior,
 * loyalty, promotional) and is responsible for determining applicability and
 * calculating the monetary discount amount.
 */
public interface DiscountStrategy {
    /**
     * Retrieves the descriptive name of the discount strategy.
     * This name is used for record-keeping in the final Transaction object.
     *
     * @return The name of the discount (e.g., "Senior Citizen Discount").
     */
    String getName();

    /**
     * Calculates the monetary discount amount to be applied to a purchase.
     *
     * @param subtotal The transaction subtotal before tax and current discounts.
     * @return The calculated discount amount in the local currency.
     */
    double calculateDiscount(double subtotal);

    /**
     * Determines whether the discount rule can be applied to the given customer.
     * This may check for membership status, senior citizen status, or other eligibility criteria.
     *
     * @param customer The Customer object to check for eligibility.
     * @return True if the discount is applicable, false otherwise.
     */
    boolean isApplicable(Customer customer);
}

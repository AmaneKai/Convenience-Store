package com.konbini.service.discount;

import com.konbini.model.Customer;

/**
 * Strategy interface for implementing various discount calculation algorithms.
 * Allows for flexible discount application based on different business rules
 * and customer eligibility criteria.
 *
 * Implementations of this interface can provide different types of discounts
 * such as percentage-based, fixed amount, senior citizen, membership, or seasonal discounts.
 */
public interface DiscountStrategy {

    /**
     * Gets the display name of this discount strategy.
     * This name is typically used in receipts and transaction records.
     *
     * @return the name of the discount strategy
     */
    String getName();

    /**
     * Calculates the discount amount based on the transaction subtotal.
     *
     * @param subtotal the transaction subtotal before discounts and taxes
     * @return the calculated discount amount
     */
    double calculateDiscount(double subtotal);

    /**
     * Determines whether this discount strategy is applicable to the given customer.
     * Checks customer eligibility based on membership status, senior citizen status,
     * or other business rules.
     *
     * @param customer the customer to check for eligibility
     * @return true if the discount is applicable to this customer, false otherwise
     */
    boolean isApplicable(Customer customer);
}
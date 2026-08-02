package com.konbini.domain.transaction.discount;

import com.konbini.domain.customer.Customer;
import java.math.BigDecimal;

/**
 * Strategy contract for calculating customer-eligible discounts.
 */
public interface DiscountStrategy {

    /**
     * Returns the display name of this discount strategy.
     *
     * @return the discount name
     */
    String getName();

    /**
     * Computes the discount amount for a subtotal.
     *
     * @param subtotal the transaction subtotal
     * @return the discount amount
     */
    BigDecimal calculateDiscount(BigDecimal subtotal);

    /**
     * Determines whether this strategy applies to the given customer.
     *
     * @param customer the customer to evaluate
     * @return true if the discount is applicable
     */
    boolean isApplicable(Customer customer);
}

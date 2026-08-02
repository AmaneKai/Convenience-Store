package com.konbini.domain.transaction.discount;

import com.konbini.domain.customer.Customer;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Twenty-percent discount granted to senior citizens.
 */
public class SeniorDiscountStrategy implements DiscountStrategy {

    /**
     * The senior citizen discount rate.
     */
    public static final BigDecimal SENIOR_DISCOUNT_RATE = new BigDecimal("0.20");

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Senior Citizen Discount";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        return subtotal.multiply(SENIOR_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Customer customer) {
        return customer.isSeniorCitizen();
    }
}

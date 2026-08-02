package com.konbini.domain.transaction;

import com.konbini.domain.customer.Customer;
import com.konbini.domain.transaction.discount.PointsRedemptionStrategy;
import com.konbini.domain.transaction.discount.SeniorDiscountStrategy;
import com.konbini.domain.transaction.tax.VATTaxStrategy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes the financial breakdown of a cart — VAT, eligible discounts and
 * loyalty points to be earned — without mutating the cart, its customer, or
 * any inventory. Shared by the handler that commits a checkout and the query
 * that previews one, so the two can never quietly drift apart.
 */
public final class CheckoutCalculator {

    private CheckoutCalculator() {
    }

    /**
     * Calculates the financial breakdown for a cart and a requested points
     * redemption.
     *
     * @param cart the cart to price
     * @param pointsToRedeem the loyalty points the customer wants to redeem
     * @return the computed breakdown
     * @throws IllegalArgumentException if the resulting total would be negative
     */
    public static CheckoutCalculation calculate(Cart cart, int pointsToRedeem) {
        Customer customer = cart.getCustomer();
        BigDecimal subtotal = cart.getSubtotal();

        VATTaxStrategy taxStrategy = new VATTaxStrategy();
        BigDecimal tax = taxStrategy.calculateTax(subtotal);
        BigDecimal discount = BigDecimal.ZERO;
        int pointsRedeemed = 0;
        List<String> appliedDiscounts = new ArrayList<>();

        SeniorDiscountStrategy seniorStrategy = new SeniorDiscountStrategy();
        if (seniorStrategy.isApplicable(customer)) {
            discount = discount.add(seniorStrategy.calculateDiscount(subtotal));
            appliedDiscounts.add(seniorStrategy.getName());
        }

        if (pointsToRedeem > 0) {
            PointsRedemptionStrategy pointsStrategy = new PointsRedemptionStrategy(pointsToRedeem);
            if (pointsStrategy.isApplicable(customer)) {
                discount = discount.add(pointsStrategy.calculateDiscount(subtotal));
                pointsRedeemed = pointsToRedeem;
                appliedDiscounts.add(pointsStrategy.getName());
            }
        }

        BigDecimal total = subtotal.add(tax).subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid total calculation");
        }

        int pointsEarned = 0;
        if (customer.hasMembershipCard() && !customer.getMembershipCard().isExpired()) {
            pointsEarned = total.divide(BigDecimal.valueOf(50), RoundingMode.FLOOR).intValue();
        }

        return new CheckoutCalculation(subtotal, tax, taxStrategy.getName(), discount,
                List.copyOf(appliedDiscounts), pointsRedeemed, total, pointsEarned);
    }
}

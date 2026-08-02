package com.konbini.domain.transaction.discount;

import com.konbini.domain.customer.Customer;
import java.math.BigDecimal;

/**
 * Discount strategy redeeming loyalty points at one peso per point.
 */
public class PointsRedemptionStrategy implements DiscountStrategy {

    private final int pointsToRedeem;

    /**
     * Constructs the strategy for a given redemption amount.
     *
     * @param pointsToRedeem the number of points to redeem
     * @throws IllegalArgumentException if the amount is negative
     */
    public PointsRedemptionStrategy(int pointsToRedeem) {
        if (pointsToRedeem < 0) {
            throw new IllegalArgumentException("Points to redeem cannot be negative");
        }
        this.pointsToRedeem = pointsToRedeem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Points Redemption";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal) {
        return BigDecimal.valueOf(pointsToRedeem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(Customer customer) {
        if (pointsToRedeem <= 0 || !customer.hasMembershipCard()) {
            return false;
        }
        return customer.getMembershipCard().getPoints() >= pointsToRedeem
                && !customer.getMembershipCard().isExpired();
    }

    /**
     * Returns the configured redemption amount.
     *
     * @return the points to redeem
     */
    public int getPointsToRedeem() {
        return pointsToRedeem;
    }
}

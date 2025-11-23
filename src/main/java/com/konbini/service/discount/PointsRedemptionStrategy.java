package com.konbini.service.discount;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;

/**
 * Discount strategy for redeeming loyalty points as monetary discounts.
 * Implements a points-based discount system where 1 point = ₱1 discount.
 * Validates point availability and membership card status before application.
 */
public class PointsRedemptionStrategy implements DiscountStrategy {
    private final int pointsToRedeem;

    /**
     * Constructs a PointsRedemptionStrategy with the specified points to redeem.
     *
     * @param pointsToRedeem the number of loyalty points to redeem
     */
    public PointsRedemptionStrategy(int pointsToRedeem) {
        this.pointsToRedeem = pointsToRedeem;
    }

    /**
     * {@inheritDoc}
     * Returns "Points Redemption" as the discount name.
     */
    @Override
    public String getName() {
        return "Points Redemption";
    }

    /**
     * {@inheritDoc}
     * Calculates discount as 1 point = ₱1.
     *
     * @return the discount amount equal to the number of points redeemed
     */
    @Override
    public double calculateDiscount(double subtotal) {
        return pointsToRedeem; // 1 point = 1 peso
    }

    /**
     * {@inheritDoc}
     * Applicable only if:
     * - Points to redeem is greater than 0
     * - Customer has a membership card
     * - Customer has sufficient points
     * - Membership card is not expired
     */
    @Override
    public boolean isApplicable(Customer customer) {
        boolean temp = false;

        if (pointsToRedeem > 0 && customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();
            temp = card.getPoints() >= pointsToRedeem && !card.isExpired();
        }

        return temp;
    }

    /**
     * Processes the actual redemption of points from the customer's membership card.
     * Deducts the specified points if the redemption is applicable.
     *
     * @param customer the customer whose points will be redeemed
     */
    public void processRedemption(Customer customer) {
        if (isApplicable(customer)) {
            customer.getMembershipCard().deductPoints(pointsToRedeem);
        }
    }

    /**
     * Gets the number of points configured for redemption.
     *
     * @return the points to redeem
     */
    public int getPointsToRedeem() {
        return pointsToRedeem;
    }
}
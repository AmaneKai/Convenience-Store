package com.konbini.service.discount;

import com.konbini.model.Customer;

/**
 * Concrete implementation of the DiscountStrategy for loyalty point redemption.
 * This strategy allows a customer to use their accumulated loyalty points to
 * receive a monetary discount on their purchase, assuming a simple 1 point = 1 unit
 * of currency conversion. It handles both checking applicability and processing
 * the point deduction on the customer's card.
 */
public class PointsRedemptionStrategy implements DiscountStrategy {
    /**
     * The number of loyalty points the customer is attempting to redeem.
     */
    private final int pointsToRedeem;

    /**
     * Constructs a PointsRedemptionStrategy with a specified number of points.
     *
     * @param pointsToRedeem The number of points the customer intends to redeem.
     */
    public PointsRedemptionStrategy(int pointsToRedeem) {
        this.pointsToRedeem = pointsToRedeem;
    }

    /**
     * Retrieves the descriptive name of this discount strategy.
     *
     * @return The string "Points Redemption".
     */
    @Override
    public String getName() {
        return "Points Redemption";
    }

    /**
     * Calculates the monetary discount amount based on the points to redeem.
     * Assumes a conversion rate of 1 loyalty point equals 1 unit of currency.
     *
     * @param subtotal The transaction subtotal (not directly used in calculation, but required by interface).
     * @return The discount amount, which is numerically equal to the points to redeem.
     */
    @Override
    public double calculateDiscount(double subtotal) {
        return pointsToRedeem; // 1 point = 1 peso
    }

    /**
     * Determines if the point redemption is possible for the given customer.
     * Applicable only if the customer has a membership card and has a sufficient
     * number of points (greater than or equal to pointsToRedeem).
     *
     * @param customer The Customer object to check for eligibility.
     * @return True if the customer can redeem the specified points, false otherwise.
     */
    @Override
    public boolean isApplicable(Customer customer) {
        return customer.hasMembershipCard() &&
               customer.getMembershipCard().getPoints() >= pointsToRedeem;
    }

    /**
     * Processes the redemption by deducting the specified points from the customer's
     * membership card. This method should only be called if isApplicable returns true.
     *
     * @param customer The Customer whose points are to be deducted.
     */
    public void processRedemption(Customer customer) {
        if (isApplicable(customer)) {
            customer.getMembershipCard().deductPoints(pointsToRedeem);
        }
    }
}

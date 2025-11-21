package com.konbini.service.discount;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;

public class PointsRedemptionStrategy implements DiscountStrategy {
    private final int pointsToRedeem;

    public PointsRedemptionStrategy(int pointsToRedeem) {
        this.pointsToRedeem = pointsToRedeem;
    }

    @Override
    public String getName() {
        return "Points Redemption";
    }

    @Override
    public double calculateDiscount(double subtotal) {
        return pointsToRedeem; // 1 point = 1 peso
    }

    @Override
    public boolean isApplicable(Customer customer) {    
        if (pointsToRedeem <= 0) {
            return false;
        }
    
        if (!customer.hasMembershipCard()) {
            return false;
        }

        MembershipCard card = customer.getMembershipCard();

        return card.getPoints() >= pointsToRedeem && !card.isExpired();
    }

    public void processRedemption(Customer customer) {
        if (isApplicable(customer)) {
            customer.getMembershipCard().deductPoints(pointsToRedeem);
        }
    }

    public int getPointsToRedeem() {
        return pointsToRedeem;  // Use the correct field name
    }
}

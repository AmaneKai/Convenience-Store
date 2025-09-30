package com.konbini.service.discount;

import com.konbini.model.Customer;

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
        return customer.hasMembershipCard() && 
               customer.getMembershipCard().getPoints() >= pointsToRedeem;
    }

   public void processRedemption(Customer customer) {
        if (isApplicable(customer)) {
            customer.getMembershipCard().deductPoints(pointsToRedeem);
        }
    }
}

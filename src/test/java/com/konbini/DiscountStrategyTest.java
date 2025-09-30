package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;
import com.konbini.service.discount.DiscountStrategy;
import com.konbini.service.discount.PointsRedemptionStrategy;
import com.konbini.service.discount.SeniorDiscountStrategy;

import java.time.LocalDate;

public class DiscountStrategyTest {
    
    @Test
    public void testSeniorDiscountStrategy() {
        DiscountStrategy seniorStrategy = new SeniorDiscountStrategy();
        
        // Create a senior citizen customer
        Customer seniorCustomer = new Customer("Senior Customer", true);
        
        // Create a regular customer
        Customer regularCustomer = new Customer("Regular Customer", false);
        
        // Test if the strategy applies correctly
        assertTrue(seniorStrategy.isApplicable(seniorCustomer));
        assertFalse(seniorStrategy.isApplicable(regularCustomer));
        
        // Test discount calculation
        double subtotal = 100.0;
        double expectedDiscount = 20.0; // 20% of 100.0
        
        assertEquals(expectedDiscount, seniorStrategy.calculateDiscount(subtotal), 0.001);
        assertEquals("Senior Citizen Discount", seniorStrategy.getName());
    }
    
    @Test
    public void testPointsRedemptionStrategy() {
        int pointsToRedeem = 50;
        DiscountStrategy pointsStrategy = new PointsRedemptionStrategy(pointsToRedeem);
        
        // Create a customer with membership card and points
        Customer membershipCustomer = new Customer("Membership Customer", false);
        LocalDate expiryDate = LocalDate.now().plusYears(1);
        MembershipCard card = new MembershipCard("CARD123", expiryDate);
        card.addPoints(100);
        membershipCustomer.setMembershipCard(card);
        
        // Create a customer without membership card
        Customer regularCustomer = new Customer("Regular Customer", false);
        
        // Create a customer with membership card but insufficient points
        Customer lowPointsCustomer = new Customer("Low Points Customer", false);
        MembershipCard lowPointsCard = new MembershipCard("CARD456", expiryDate);
        lowPointsCard.addPoints(30);
        lowPointsCustomer.setMembershipCard(lowPointsCard);
        
        // Test if the strategy applies correctly
        assertTrue(pointsStrategy.isApplicable(membershipCustomer));
        assertFalse(pointsStrategy.isApplicable(regularCustomer));
        assertFalse(pointsStrategy.isApplicable(lowPointsCustomer));
        
        // Test discount calculation - points to peso conversion is 1:1
        double subtotal = 100.0;
        double expectedDiscount = 50.0; // 50 points = ₱50
        
        assertEquals(expectedDiscount, pointsStrategy.calculateDiscount(subtotal), 0.001);
        assertEquals("Points Redemption", pointsStrategy.getName());
        
        // Test points redemption
        ((PointsRedemptionStrategy) pointsStrategy).processRedemption(membershipCustomer);
        assertEquals(50, membershipCustomer.getMembershipCard().getPoints()); // 100 - 50 = 50
    }
}

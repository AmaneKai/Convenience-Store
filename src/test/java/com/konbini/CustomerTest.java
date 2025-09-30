package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;

import java.time.LocalDate;

public class CustomerTest {
    
    @Test
    public void testCustomerCreation() {
        Customer customer = new Customer("Test Customer", false);
        
        assertNotNull(customer);
        assertEquals("Test Customer", customer.getName());
        assertFalse(customer.isSeniorCitizen());
        assertFalse(customer.hasMembershipCard());
    }
    
    @Test
    public void testCustomerWithMembershipCard() {
        Customer customer = new Customer("Test Customer", false);
        LocalDate expiryDate = LocalDate.now().plusYears(1);
        MembershipCard card = new MembershipCard("CARD123", expiryDate);
        
        customer.setMembershipCard(card);
        
        assertTrue(customer.hasMembershipCard());
        assertEquals("CARD123", customer.getMembershipCard().getCardNumber());
        assertEquals(expiryDate, customer.getMembershipCard().getExpiryDate());
        assertEquals(0, customer.getMembershipCard().getPoints());
    }
    
    @Test
    public void testMembershipCardPoints() {
        Customer customer = new Customer("Test Customer", false);
        LocalDate expiryDate = LocalDate.now().plusYears(1);
        MembershipCard card = new MembershipCard("CARD123", expiryDate);
        
        customer.setMembershipCard(card);
        
        // Add points
        card.addPoints(100);
        assertEquals(100, card.getPoints());
        
        // Deduct points
        card.deductPoints(30);
        assertEquals(70, card.getPoints());
    }
    
    @Test
    public void testMembershipCardExpiration() {
        // Create an expired card
        LocalDate pastDate = LocalDate.now().minusDays(1);
        MembershipCard expiredCard = new MembershipCard("EXPIRED", pastDate);
        
        assertTrue(expiredCard.isExpired());
        
        // Create a valid card
        LocalDate futureDate = LocalDate.now().plusYears(1);
        MembershipCard validCard = new MembershipCard("VALID", futureDate);
        
        assertFalse(validCard.isExpired());
    }
}

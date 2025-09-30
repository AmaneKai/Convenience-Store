package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.*;
import com.konbini.service.discount.PointsRedemptionStrategy;
import com.konbini.service.discount.SeniorDiscountStrategy;
import com.konbini.service.tax.VATTaxStrategy;

import java.time.LocalDate;

public class IntegrationTest {
    
    @Test
    public void testCompleteShoppingFlow() {
        // 1. Create a customer
        Customer customer = new Customer("Test Customer", false);
        LocalDate expiryDate = LocalDate.now().plusYears(1);
        MembershipCard card = new MembershipCard("CARD123", expiryDate);
        customer.setMembershipCard(card);
        
        // 2. Create products
        Product product1 = new Product(
            "Product 1",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Brand 1",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        Product product2 = new Product(
            "Product 2",
            15.0,
            10,
            ProductCategory.BEVERAGE.getDisplayName(),
            "Brand 2",
            ProductSubcategory.COLD.getDisplayName(),
            LocalDate.now().plusDays(60)
        );
        
        // 3. Create a cart
        Cart cart = new Cart(customer);
        
        // 4. Add products to cart
        cart.addItem(product1, 2);
        cart.addItem(product2, 3);
        
        // 5. Verify cart contents
        assertFalse(cart.isEmpty());
        assertEquals(5, cart.getTotalItems());
        assertEquals(65.0, cart.getSubtotal()); // (2 * 10.0) + (3 * 15.0) = 20.0 + 45.0 = 65.0
        
        // 6. Process transaction
        Transaction.Builder builder = new Transaction.Builder(customer, cart)
            .withTaxStrategy(new VATTaxStrategy())
            .withPointsEarned()
            .withPayment(100.0);
        
        Transaction transaction = builder.build();
        
        // 7. Verify transaction details
        assertNotNull(transaction);
        assertEquals(customer, transaction.getCustomer());
        assertEquals(65.0, transaction.getSubtotal());
        assertEquals(7.8, transaction.getTax(), 0.001); // 12% of 65.0 = 7.8
        assertEquals(0.0, transaction.getDiscount());
        assertEquals(72.8, transaction.getTotal(), 0.001); // 65.0 + 7.8 = 72.8
        assertEquals(100.0, transaction.getAmountPaid());
        assertEquals(27.2, transaction.getChange(), 0.001); // 100.0 - 72.8 = 27.2
        
        // 8. Verify points earned
        int expectedPoints = (int)(72.8 / 50); // 72.8 / 50 = 1 point
        assertEquals(expectedPoints, transaction.getPointsEarned());
        assertEquals(expectedPoints, customer.getMembershipCard().getPoints());
        
        // 9. Verify product quantities were decreased
        assertEquals(3, product1.getQuantity()); // 5 - 2 = 3
        assertEquals(7, product2.getQuantity()); // 10 - 3 = 7
        
        // 10. Generate receipt
        Receipt receipt = new Receipt(transaction);
        String receiptText = receipt.generateReceiptText();
        
        // 11. Verify receipt content
        assertNotNull(receiptText);
        assertTrue(receiptText.contains("KONBINI STORE"));
        assertTrue(receiptText.contains("Customer: Test Customer"));
        assertTrue(receiptText.contains("Product 1"));
        assertTrue(receiptText.contains("Product 2"));
        assertTrue(receiptText.contains("Subtotal: ₱65.00"));
        assertTrue(receiptText.contains("Value Added Tax (VAT): ₱7.80"));
        assertTrue(receiptText.contains("Total: ₱72.80"));
        assertTrue(receiptText.contains("Amount Paid: ₱100.00"));
        assertTrue(receiptText.contains("Change: ₱27.20"));
    }
}

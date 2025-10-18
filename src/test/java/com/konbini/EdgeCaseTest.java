package com.konbini;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.*;
import com.konbini.service.discount.DiscountStrategy;
import com.konbini.service.discount.PointsRedemptionStrategy;
import com.konbini.service.discount.SeniorDiscountStrategy;
import com.konbini.service.tax.VATTaxStrategy;
import com.konbini.util.IdGenerator;

import java.time.LocalDate;

/**
 * Additional comprehensive tests for edge cases, error conditions, and boundary scenarios
 */
public class EdgeCaseTest {
    
    private Customer testCustomer;
    private Product testProduct;
    
    @BeforeEach
    public void setUp() {
        testCustomer = new Customer("Edge Case Customer", false);
        testProduct = new Product(
            "Edge Case Product",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
    }
    
    // ==================== PRODUCT ERROR CONDITION TESTS ====================
    
    @Test
    public void testProductWithNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(
                "Invalid Product",
                -10.0,
                5,
                ProductCategory.FOOD.getDisplayName(),
                "Brand",
                ProductSubcategory.SNACK.getDisplayName(),
                LocalDate.now().plusDays(30)
            );
        }, "Product creation should fail with negative price");
    }
    
    @Test
    public void testProductWithNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(
                "Invalid Product",
                10.0,
                -5,
                ProductCategory.FOOD.getDisplayName(),
                "Brand",
                ProductSubcategory.SNACK.getDisplayName(),
                LocalDate.now().plusDays(30)
            );
        }, "Product creation should fail with negative quantity");
    }
    
    @Test
    public void testProductWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(
                null,
                10.0,
                5,
                ProductCategory.FOOD.getDisplayName(),
                "Brand",
                ProductSubcategory.SNACK.getDisplayName(),
                LocalDate.now().plusDays(30)
            );
        }, "Product creation should fail with null name");
    }
    
    @Test
    public void testProductWithZeroPrice() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Product(
                "Free Product",
                0.0,
                5,
                ProductCategory.FOOD.getDisplayName(),
                "Brand",
                ProductSubcategory.SNACK.getDisplayName(),
                LocalDate.now().plusDays(30)
            );
        }, "Product creation should fail with zero price");
    }
    
    @Test
    public void testDecreaseQuantityMoreThanAvailable() {
        assertThrows(IllegalArgumentException.class, () -> {
            testProduct.decreaseQuantity(10); // Only 5 available
        }, "Should not allow decreasing more than available quantity");
    }
    
    @Test
    public void testDecreaseQuantityToZero() {
        testProduct.decreaseQuantity(5); // Decrease all 5
        assertEquals(0, testProduct.getQuantity(), "Quantity should be 0");
        assertTrue(testProduct.isLowStock(), "Product with 0 stock should be low stock");
    }
    
    @Test
    public void testDecreaseQuantityByNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            testProduct.decreaseQuantity(-5);
        }, "Should not allow decreasing by negative amount");
    }
    
    // ==================== CART ERROR CONDITION TESTS ====================
    
    @Test
    public void testAddZeroQuantityToCart() {
        Cart cart = new Cart(testCustomer);
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(testProduct, 0);
        }, "Should not allow adding 0 quantity to cart");
    }
    
    @Test
    public void testAddNegativeQuantityToCart() {
        Cart cart = new Cart(testCustomer);
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(testProduct, -5);
        }, "Should not allow adding negative quantity to cart");
    }
    
    @Test
    public void testAddExpiredProductToCart() {
        Product expiredProduct = new Product(
            "Expired Product",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().minusDays(1) // Expired yesterday
        );
        
        Cart cart = new Cart(testCustomer);
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(expiredProduct, 1);
        }, "Should not allow adding expired product to cart");
    }
    
    @Test
    public void testAddMoreQuantityThanAvailable() {
        Cart cart = new Cart(testCustomer);
        assertThrows(IllegalArgumentException.class, () -> {
            cart.addItem(testProduct, 10); // Only 5 available
        }, "Should not allow adding more quantity than available");
    }
    
    @Test
    public void testRemoveNonExistentItemFromCart() {
        Cart cart = new Cart(testCustomer);
        cart.addItem(testProduct, 2);
        
        assertThrows(IllegalArgumentException.class, () -> {
            cart.removeItem("NONEXISTENT_ID");
        }, "Should throw error when removing non-existent item");
    }
    
    @Test
    public void testUpdateCartItemQuantityToZero() {
        Cart cart = new Cart(testCustomer);
        cart.addItem(testProduct, 2);
        
        assertThrows(IllegalArgumentException.class, () -> {
            cart.updateItemQuantity(testProduct.getId(), 0);
        }, "Should not allow updating item quantity to 0");
    }
    
    @Test
    public void testUpdateCartItemQuantityToNegative() {
        Cart cart = new Cart(testCustomer);
        cart.addItem(testProduct, 2);
        
        assertThrows(IllegalArgumentException.class, () -> {
            cart.updateItemQuantity(testProduct.getId(), -5);
        }, "Should not allow updating item quantity to negative");
    }
    
    // ==================== MEMBERSHIP CARD TESTS ====================
    
    @Test
    public void testMembershipCardWithNegativePoints() {
        Customer customer = new Customer("Test", false);
        MembershipCard card = new MembershipCard("CARD123", LocalDate.now().plusYears(1));
        
        assertThrows(IllegalArgumentException.class, () -> {
            card.addPoints(-50);
        }, "Should not allow adding negative points");
    }
    
    @Test
    public void testDeductMorePointsThanAvailable() {
        MembershipCard card = new MembershipCard("CARD123", LocalDate.now().plusYears(1));
        card.addPoints(30);
        
        assertThrows(IllegalArgumentException.class, () -> {
            card.deductPoints(50); // Only 30 available
        }, "Should not allow deducting more points than available");
    }
    
    @Test
    public void testExpiredMembershipCard() {
        Customer customer = new Customer("Test", false);
        LocalDate expiredDate = LocalDate.now().minusDays(1);
        MembershipCard expiredCard = new MembershipCard("EXPIRED_CARD", expiredDate);
        
        assertTrue(expiredCard.isExpired(), "Card should be expired");
        
        // Attempting to add points to expired card should fail
        assertThrows(IllegalStateException.class, () -> {
            expiredCard.addPoints(50);
        }, "Should not allow adding points to expired card");
    }
    
    @Test
    public void testMembershipCardExpiresToday() {
        MembershipCard card = new MembershipCard("CARD123", LocalDate.now());
        assertFalse(card.isExpired(), "Card expiring today should not be expired yet");
    }
    
    @Test
    public void testMembershipCardExpiresYesterday() {
        MembershipCard card = new MembershipCard("CARD123", LocalDate.now().minusDays(1));
        assertTrue(card.isExpired(), "Card that expired yesterday should be expired");
    }
    
    // ==================== TRANSACTION ERROR TESTS ====================
    
    @Test
    public void testTransactionWithZeroPayment() {
        Cart cart = new Cart(testCustomer);
        cart.addItem(testProduct, 2);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction.Builder(testCustomer, cart)
                .withTaxStrategy(new VATTaxStrategy())
                .withPayment(0.0)
                .build();
        }, "Should not allow zero payment");
    }
    
    @Test
    public void testTransactionWithNegativePayment() {
        Cart cart = new Cart(testCustomer);
        cart.addItem(testProduct, 2);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction.Builder(testCustomer, cart)
                .withTaxStrategy(new VATTaxStrategy())
                .withPayment(-100.0)
                .build();
        }, "Should not allow negative payment");
    }
    
    @Test
    public void testTransactionWithInsufficientPayment() {
        Cart cart = new Cart(testCustomer);
        cart.addItem(testProduct, 2); // Subtotal: 20.0, Tax: 2.4, Total: 22.4
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction.Builder(testCustomer, cart)
                .withTaxStrategy(new VATTaxStrategy())
                .withPayment(10.0) // Less than total
                .build();
        }, "Should not allow payment less than total");
    }
    
    @Test
    public void testTransactionWithEmptyCart() {
        Cart emptyCart = new Cart(testCustomer);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Transaction.Builder(testCustomer, emptyCart)
                .withTaxStrategy(new VATTaxStrategy())
                .withPayment(100.0)
                .build();
        }, "Should not allow transaction with empty cart");
    }
    
    // ==================== DISCOUNT BOUNDARY TESTS ====================
    
    @Test
    public void testPointsRedemptionWithZeroPoints() {
        Customer customer = new Customer("Test", false);
        MembershipCard card = new MembershipCard("CARD123", LocalDate.now().plusYears(1));
        customer.setMembershipCard(card);
        
        Cart cart = new Cart(customer);
        cart.addItem(testProduct, 1);
        
        DiscountStrategy strategy = new PointsRedemptionStrategy(0);
        assertFalse(strategy.isApplicable(customer), "Should not apply discount for 0 points");
    }
    
    @Test
    public void testPointsRedemptionExceedsTotal() {
        Customer customer = new Customer("Test", false);
        MembershipCard card = new MembershipCard("CARD123", LocalDate.now().plusYears(1));
        card.addPoints(1000); // 1000 points = ₱1000
        customer.setMembershipCard(card);
        
        Cart cart = new Cart(customer);
        cart.addItem(testProduct, 1); // Subtotal: 10.0
        
        PointsRedemptionStrategy strategy = new PointsRedemptionStrategy(1000);
        double discount = strategy.calculateDiscount(10.0);
        
        assertTrue(discount > 10.0, "Discount can exceed subtotal (system should handle this)");
    }
    
    @Test
    public void testMultipleDiscountsApplied() {
        Customer seniorWithMembership = new Customer("Senior", true);
        MembershipCard card = new MembershipCard("CARD123", LocalDate.now().plusYears(1));
        card.addPoints(100);
        seniorWithMembership.setMembershipCard(card);
        
        // Create a new product with enough stock
        Product product = new Product(
            "Test Product",
            10.0,
            50,  // 50 items - enough stock
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        Cart cart = new Cart(seniorWithMembership);
        cart.addItem(product, 10); // Subtotal: 100.0

        Transaction.Builder builder = new Transaction.Builder(seniorWithMembership, cart)
            .withTaxStrategy(new VATTaxStrategy())
            .addDiscountStrategy(new SeniorDiscountStrategy())
            .addDiscountStrategy(new PointsRedemptionStrategy(50))
            .withPayment(200.0);
        
        Transaction transaction = builder.build();
        
        // Verify both discounts are applied
        assertTrue(transaction.getAppliedDiscounts().contains("Senior Citizen Discount"));
        assertTrue(transaction.getAppliedDiscounts().contains("Points Redemption"));
        assertTrue(transaction.getDiscount() > 0, "Multiple discounts should be applied");
    }
    
    // ==================== CUSTOMER BOUNDARY TESTS ====================
    
    @Test
    public void testCustomerWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer(null, false);
        }, "Customer creation should fail with null name");
    }
    
    @Test
    public void testCustomerWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Customer("", false);
        }, "Customer creation should fail with empty name");
    }
    
    @Test
    public void testSetInvalidMembershipCardToCustomer() {
        Customer customer = new Customer("Test", false);
        
        assertThrows(IllegalArgumentException.class, () -> {
            customer.setMembershipCard(null);
        }, "Should not allow setting null membership card");
    }
    
    // ==================== ID GENERATOR STRESS TESTS ====================
    
    @Test
    public void testIdGeneratorUniquenessWithHighLoad() {
        IdGenerator idGen = IdGenerator.getInstance();
        int count = 10000;
        java.util.Set<String> ids = new java.util.HashSet<>();
        
        for (int i = 0; i < count; i++) {
            ids.add(idGen.generateId("stress"));
        }
        
        assertEquals(count, ids.size(), "All " + count + " generated IDs should be unique");
    }
    
    @Test
    public void testIdGeneratorSequentialOrder() {
        IdGenerator idGen = IdGenerator.getInstance();
        
        String id1 = idGen.generateId("seq");
        String id2 = idGen.generateId("seq");
        String id3 = idGen.generateId("seq");
        
        int num1 = Integer.parseInt(id1.substring(3));
        int num2 = Integer.parseInt(id2.substring(3));
        int num3 = Integer.parseInt(id3.substring(3));
        
        assertEquals(num1 + 1, num2, "IDs should be sequential");
        assertEquals(num2 + 1, num3, "IDs should be sequential");
    }
    
    // ==================== TAX CALCULATION BOUNDARY TESTS ====================
    
    @Test
    public void testVATTaxOnZeroAmount() {
        VATTaxStrategy vat = new VATTaxStrategy();
        assertEquals(0.0, vat.calculateTax(0.0), "Tax on zero should be zero");
    }
    
    @Test
    public void testVATTaxPrecision() {
        VATTaxStrategy vat = new VATTaxStrategy();
        double subtotal = 33.33;
        double expectedTax = 3.9996; // 12% of 33.33
        
        assertEquals(expectedTax, vat.calculateTax(subtotal), 0.001, 
            "Tax calculation should maintain precision");
    }
}

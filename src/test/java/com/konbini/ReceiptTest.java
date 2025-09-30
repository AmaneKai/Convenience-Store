package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.Cart;
import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;
import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.model.Receipt;
import com.konbini.model.Transaction;
import com.konbini.service.discount.SeniorDiscountStrategy;
import com.konbini.service.tax.VATTaxStrategy;

import java.time.LocalDate;

public class ReceiptTest {
    
    @Test
    public void testGenerateReceipt() {
        // Create a customer
        Customer customer = new Customer("Test Customer", false);
        
        // Create products
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
        
        // Create a cart
        Cart cart = new Cart(customer);
        cart.addItem(product1, 2);
        cart.addItem(product2, 3);
        
        // Create a transaction
        Transaction.Builder builder = new Transaction.Builder(customer, cart)
            .withTaxStrategy(new VATTaxStrategy())
            .withPayment(100.0);
        
        Transaction transaction = builder.build();
        
        // Generate receipt
        Receipt receipt = new Receipt(transaction);
        String receiptText = receipt.generateReceiptText();
        
        // Verify receipt content
        assertNotNull(receiptText);
        assertTrue(receiptText.contains("KONBINI STORE"));
        assertTrue(receiptText.contains("Receipt #: " + transaction.getId()));
        assertTrue(receiptText.contains("Customer: " + customer.getName()));
        assertTrue(receiptText.contains("Product 1"));
        assertTrue(receiptText.contains("Product 2"));
        assertTrue(receiptText.contains("Subtotal: ₱65.00")); // 20.0 + 45.0 = 65.0
        assertTrue(receiptText.contains("Value Added Tax (VAT): ₱7.80")); // 12% of 65.0 = 7.8
        assertTrue(receiptText.contains("Total: ₱72.80")); // 65.0 + 7.8 = 72.8
        assertTrue(receiptText.contains("Amount Paid: ₱100.00"));
        assertTrue(receiptText.contains("Change: ₱27.20")); // 100.0 - 72.8 = 27.2
    }
    
    @Test
    public void testReceiptWithDiscounts() {
        // Create a senior customer with membership card
        Customer customer = new Customer("Senior Customer", true);
        LocalDate expiryDate = LocalDate.now().plusYears(1);
        MembershipCard card = new MembershipCard("CARD123", expiryDate);
        customer.setMembershipCard(card);
        
        // Create a product
        Product product = new Product(
            "Test Product",
            100.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        // Create a cart
        Cart cart = new Cart(customer);
        cart.addItem(product, 2);
        
        // Create a transaction with senior discount
        Transaction.Builder builder = new Transaction.Builder(customer, cart)
            .withTaxStrategy(new VATTaxStrategy())
            .addDiscountStrategy(new SeniorDiscountStrategy())
            .withPointsEarned()
            .withPayment(200.0);
        
        Transaction transaction = builder.build();
        
        // Generate receipt
        Receipt receipt = new Receipt(transaction);
        String receiptText = receipt.generateReceiptText();
        
        // Verify receipt content
        assertNotNull(receiptText);
        assertTrue(receiptText.contains("KONBINI STORE"));
        assertTrue(receiptText.contains("Customer: Senior Customer"));
        assertTrue(receiptText.contains("Test Product"));
        assertTrue(receiptText.contains("Subtotal: ₱200.00")); // 2 * 100.0 = 200.0
        assertTrue(receiptText.contains("Value Added Tax (VAT): ₱24.00")); // 12% of 200.0 = 24.0
        assertTrue(receiptText.contains("Discount: ₱40.00")); // 20% of 200.0 = 40.0
        assertTrue(receiptText.contains("Senior Citizen Discount")); // Discount name
        assertTrue(receiptText.contains("Total: ₱184.00")); // 200.0 + 24.0 - 40.0 =
        assertTrue(receiptText.contains("Total: ₱184.00")); // 200.0 + 24.0 - 40.0 = 184.0
        assertTrue(receiptText.contains("Amount Paid: ₱200.00"));
        assertTrue(receiptText.contains("Change: ₱16.00")); // 200.0 - 184.0 = 16.0
        
        // Verify points earned section
        assertTrue(receiptText.contains("Points Earned:"));
        
        // Points earned (1 point per ₱50)
        int expectedPoints = (int)(184.0 / 50); // 184 / 50 = 3 points
        assertTrue(receiptText.contains("Points Earned: " + expectedPoints));
        assertTrue(receiptText.contains("Current Points Balance: " + expectedPoints));
    }
}

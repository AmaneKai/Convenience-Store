package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Customer;
import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.time.LocalDate;
import java.util.List;

public class CartTest {
    
    @Test
    public void testCartCreation() {
        Customer customer = new Customer("Test Customer", false);
        Cart cart = new Cart(customer);
        
        assertNotNull(cart);
        assertEquals(customer, cart.getCustomer());
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getTotalItems());
        assertEquals(0.0, cart.getSubtotal());
    }
    
    @Test
    public void testAddItemToCart() {
        Customer customer = new Customer("Test Customer", false);
        Cart cart = new Cart(customer);
        
        Product product = new Product(
            "Test Product",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        cart.addItem(product, 2);
        
        assertFalse(cart.isEmpty());
        assertEquals(2, cart.getTotalItems());
        assertEquals(20.0, cart.getSubtotal()); // 2 * 10.0 = 20.0
        
        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        
        CartItem item = items.get(0);
        assertEquals(product, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(20.0, item.getSubtotal());
    }
    
    @Test
    public void testAddMultipleItemsToCart() {
        Customer customer = new Customer("Test Customer", false);
        Cart cart = new Cart(customer);
        
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
        
        cart.addItem(product1, 2);
        cart.addItem(product2, 3);
        
        assertFalse(cart.isEmpty());
        assertEquals(5, cart.getTotalItems()); // 2 + 3 = 5
        assertEquals(65.0, cart.getSubtotal()); // (2 * 10.0) + (3 * 15.0) = 20.0 + 45.0 = 65.0
        
        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
    }
    
    @Test
    public void testRemoveItemFromCart() {
        Customer customer = new Customer("Test Customer", false);
        Cart cart = new Cart(customer);
        
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
        
        cart.addItem(product1, 2);
        cart.addItem(product2, 3);
        
        cart.removeItem(product1.getId());
        
        assertEquals(3, cart.getTotalItems());
        assertEquals(45.0, cart.getSubtotal()); // 3 * 15.0 = 45.0
        
        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(product2, items.get(0).getProduct());
    }
    
    @Test
    public void testUpdateCartItemQuantity() {
        Customer customer = new Customer("Test Customer", false);
        Cart cart = new Cart(customer);
        
        Product product = new Product(
            "Test Product",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        cart.addItem(product, 2);
        
        cart.updateItemQuantity(product.getId(), 4);
        
        assertEquals(4, cart.getTotalItems());
        assertEquals(40.0, cart.getSubtotal()); // 4 * 10.0 = 40.0
        
        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(4, items.get(0).getQuantity());
    }
    
    @Test
    public void testClearCart() {
        Customer customer = new Customer("Test Customer", false);
        Cart cart = new Cart(customer);
        
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
        
        cart.addItem(product1, 2);
        cart.addItem(product2, 3);
        
        cart.clear();
        
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getTotalItems());
        assertEquals(0.0, cart.getSubtotal());
    }
}

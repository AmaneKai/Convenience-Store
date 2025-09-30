package com.konbini;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.time.LocalDate;

public class ProductTest {
    
    @Test
    public void testProductCreation() {
        Product product = new Product(
            "Test Product",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        assertNotNull(product);
        assertEquals("Test Product", product.getName());
        assertEquals(10.0, product.getPrice());
        assertEquals(5, product.getQuantity());
        assertEquals(ProductCategory.FOOD.getDisplayName(), product.getCategory());
        assertEquals("Test Brand", product.getBrand());
        assertEquals(ProductSubcategory.SNACK.getDisplayName(), product.getVariant());
    }
    
    @Test
    public void testProductExpiration() {
        // Create a product that expires tomorrow
        Product expiringProduct = new Product(
            "Expiring Product",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(1)
        );
        
        assertFalse(expiringProduct.isExpired());
        
        // Create a product that expired yesterday
        Product expiredProduct = new Product(
            "Expired Product",
            10.0,
            5,
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().minusDays(1)
        );
        
        assertTrue(expiredProduct.isExpired());
    }
    
    @Test
    public void testProductLowStock() {
        // Create a product with low stock
        Product lowStockProduct = new Product(
            "Low Stock Product",
            10.0,
            3, // Less than 5
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        assertTrue(lowStockProduct.isLowStock());
        
        // Create a product with sufficient stock
        Product sufficientStockProduct = new Product(
            "Sufficient Stock Product",
            10.0,
            10, // More than 5
            ProductCategory.FOOD.getDisplayName(),
            "Test Brand",
            ProductSubcategory.SNACK.getDisplayName(),
            LocalDate.now().plusDays(30)
        );
        
        assertFalse(sufficientStockProduct.isLowStock());
    }
}

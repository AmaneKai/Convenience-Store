package com.konbini.controller;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.service.ProductService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing product operations including retrieval by various criteria,
 * adding, updating, removing products, and inventory management.
 */
public class ProductController {

    private final ProductService productService;

    /**
     * Constructs a ProductController with the specified product service.
     *
     * @param productService the service for product operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves all products from the inventory.
     *
     * @return a list of all products
     */
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    /**
     * Retrieves products belonging to a specific category.
     *
     * @param category the product category to filter by
     * @return a list of products in the specified category
     */
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productService.findByCategory(category);
    }

    /**
     * Retrieves products belonging to a specific subcategory.
     *
     * @param subcategory the product subcategory to filter by
     * @return a list of products in the specified subcategory
     */
    public List<Product> getProductsBySubcategory(ProductSubcategory subcategory) {
        return productService.findBySubcategory(subcategory);
    }

    /**
     * Searches for products by name (case-insensitive partial match).
     *
     * @param name the name or partial name to search for
     * @return a list of products matching the search criteria
     */
    public List<Product> searchProductsByName(String name) {
        return productService.findByName(name);
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param productId the ID of the product to find
     * @return an Optional containing the product if found, empty otherwise
     */
    public Optional<Product> getProductById(String productId) {
        return productService.findById(productId);
    }

    /**
     * Retrieves products with low stock levels (typically below a threshold).
     *
     * @return a list of products with low stock
     */
    public List<Product> getLowStockProducts() {
        return productService.findLowStock();
    }

    /**
     * Retrieves products that have expired or are nearing expiration.
     *
     * @return a list of expired or soon-to-expire products
     */
    public List<Product> getExpiredProducts() {
        return productService.findExpired();
    }

    /**
     * Adds a new product to the inventory.
     *
     * @param name the product name
     * @param price the product price
     * @param quantity the initial stock quantity
     * @param category the product category
     * @param brand the product brand
     * @param subcategory the product subcategory
     * @param expirationDate the product expiration date
     */
    public void addProduct(String name, double price, int quantity,
        ProductCategory category, String brand, ProductSubcategory subcategory,
        LocalDate expirationDate) {
        productService.addProduct(name, price, quantity, category, brand,
            subcategory, expirationDate);
    }

    /**
     * Updates an existing product's information.
     *
     * @param productId the ID of the product to update
     * @param name the updated product name
     * @param price the updated product price
     * @param quantity the updated stock quantity
     * @param category the updated product category
     * @param brand the updated product brand
     * @param subcategory the updated product subcategory
     * @param expirationDate the updated expiration date
     */
    public void updateProduct(String productId, String name,
        double price, int quantity, ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate) {
        productService.updateProduct(productId, name, price, quantity,
            category, brand, subcategory, expirationDate);
    }

    /**
     * Removes a product from the inventory.
     *
     * @param productId the ID of the product to remove
     */
    public void removeProduct(String productId) {
        productService.removeProduct(productId);
    }

    /**
     * Restocks a product by adding to its current quantity.
     *
     * @param productId the ID of the product to restock
     * @param quantity the quantity to add to current stock
     */
    public void restockProduct(String productId, int quantity) {
        productService.restockProduct(productId, quantity);
    }

    /**
     * Saves all product data to persistent storage.
     *
     * @return true if save operation was successful, false otherwise
     */
    public boolean saveData() {
        return productService.saveInventory();
    }

    /**
     * Loads all product data from persistent storage.
     *
     * @return true if load operation was successful, false otherwise
     */
    public boolean loadData() {
        return productService.loadInventory();
    }
}
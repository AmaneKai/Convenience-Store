package com.konbini.controller;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.service.ProductService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller class responsible for managing all product-related business logic and
 * inventory operations. It acts as the layer between the application's view
 * and the core data manipulation service (ProductService).
 */
public class ProductController {
    /**
     * The service dependency used for all data persistence and business logic
     * related to Product entities and inventory management.
     */
    private final ProductService productService;

    /**
     * Constructs the ProductController, injecting the required product service.
     *
     * @param productService The service providing data access and business logic for products.
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves a list of all products currently in the inventory.
     *
     * @return A List of all Product objects.
     */
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    /**
     * Retrieves a list of products filtered by their primary category.
     *
     * @param category The ProductCategory to filter by.
     * @return A List of Product objects belonging to the specified category.
     */
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productService.findByCategory(category);
    }

    /**
     * Retrieves a list of products filtered by their subcategory.
     *
     * @param subcategory The ProductSubcategory to filter by.
     * @return A List of Product objects belonging to the specified subcategory.
     */
    public List<Product> getProductsBySubcategory
        (ProductSubcategory subcategory) {
        return productService.findBySubcategory(subcategory);
    }

    /**
     * Searches for products whose names contain the specified search term.
     *
     * @param name The name or partial name to search for.
     * @return A List of Product objects matching the search criteria.
     */
    public List<Product> searchProductsByName(String name) {
        return productService.findByName(name);
    }

    /**
     * Retrieves a single product by its unique identifier.
     *
     * @param productId The ID of the product to find.
     * @return An Optional containing the Product if found, or an empty Optional otherwise.
     */
    public Optional<Product> getProductById(String productId) {
        return productService.findById(productId);
    }

    /**
     * Retrieves a list of products whose current stock quantity is below a
     * predefined low-stock threshold.
     *
     * @return A List of Product objects considered to be low in stock.
     */
    public List<Product> getLowStockProducts() {
        return productService.findLowStock();
    }

    /**
     * Retrieves a list of products whose expiration date has passed.
     *
     * @return A List of expired Product objects.
     */
    public List<Product> getExpiredProducts() {
        return productService.findExpired();
    }

    /**
     * Creates and adds a new product to the inventory.
     *
     * @param name The name of the new product.
     * @param price The selling price of the product.
     * @param quantity The initial stock quantity.
     * @param category The primary category of the product.
     * @param brand The brand name.
     * @param subcategory The subcategory of the product.
     * @param expirationDate The expiration date of the product.
     */
    public void addProduct(String name, double price, int quantity,
        ProductCategory category, String brand, ProductSubcategory subcategory,
        LocalDate expirationDate) {
        productService.addProduct(name, price, quantity, category, brand,
            subcategory, expirationDate);
    }

    /**
     * Updates the details of an existing product, identified by its ID.
     *
     * @param productId The ID of the product to update.
     * @param name The new name.
     * @param price The new selling price.
     * @param quantity The new stock quantity.
     * @param category The new primary category.
     * @param brand The new brand name.
     * @param subcategory The new subcategory.
     * @param expirationDate The new expiration date.
     */
    public void updateProduct(String productId, String name,
        double price, int quantity, ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate) {
        productService.updateProduct(productId, name, price, quantity,
            category, brand, subcategory, expirationDate);
    }

    /**
     * Removes a product from the inventory based on its ID.
     *
     * @param productId The ID of the product to remove.
     */
    public void removeProduct(String productId) {
        productService.removeProduct(productId);
    }

    /**
     * Increases the stock quantity for an existing product.
     *
     * @param productId The ID of the product to restock.
     * @param quantity The amount by which to increase the stock.
     */
    public void restockProduct(String productId, int quantity) {
        productService.restockProduct(productId, quantity);
    }

    /**
     * Persists the current inventory data to permanent storage.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    public boolean saveData() {
        return productService.saveInventory();
    }

    /**
     * Loads the product inventory data from permanent storage into the
     * application memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    public boolean loadData() {
        return productService.loadInventory();
    }
}

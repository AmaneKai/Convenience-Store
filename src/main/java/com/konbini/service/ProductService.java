package com.konbini.service;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for product inventory management operations.
 * Handles product lifecycle, inventory control, search functionality,
 * and data persistence.
 */
public interface ProductService {

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
    void addProduct(String name, double price, int quantity,
        ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate);

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
    void updateProduct(String productId, String name, double price,
        int quantity, ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate);

    /**
     * Removes a product from the inventory.
     *
     * @param productId the ID of the product to remove
     */
    void removeProduct(String productId);

    /**
     * Restocks a product by adding to its current quantity.
     *
     * @param productId the ID of the product to restock
     * @param quantity the quantity to add to current stock
     */
    void restockProduct(String productId, int quantity);

    /**
     * Finds a product by its unique identifier.
     *
     * @param productId the product ID to search for
     * @return an Optional containing the product if found, empty otherwise
     */
    Optional<Product> findById(String productId);

    /**
     * Retrieves all products in the inventory.
     *
     * @return a list of all products, empty list if no products exist
     */
    List<Product> findAll();

    /**
     * Finds products belonging to a specific category.
     *
     * @param category the product category to filter by
     * @return a list of products in the specified category
     */
    List<Product> findByCategory(ProductCategory category);

    /**
     * Finds products belonging to a specific subcategory.
     *
     * @param subcategory the product subcategory to filter by
     * @return a list of products in the specified subcategory
     */
    List<Product> findBySubcategory(ProductSubcategory subcategory);

    /**
     * Searches for products by name (case-insensitive partial match).
     *
     * @param name the name or partial name to search for
     * @return a list of products matching the search criteria
     */
    List<Product> findByName(String name);

    /**
     * Finds products with low stock levels.
     * Typically returns products with quantity below a defined threshold.
     *
     * @return a list of products with low stock
     */
    List<Product> findLowStock();

    /**
     * Finds expired or soon-to-expire products.
     *
     * @return a list of expired products
     */
    List<Product> findExpired();

    /**
     * Saves all product inventory data to persistent storage.
     *
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveInventory();

    /**
     * Loads product inventory data from persistent storage.
     *
     * @return true if the load operation was successful, false otherwise
     */
    boolean loadInventory();

    /**
     * Validates a product price.
     *
     * @param price the price to validate
     * @throws IllegalArgumentException if the price is invalid
     */
    void validatePrice(double price) throws IllegalArgumentException;

    /**
     * Validates a product quantity.
     *
     * @param quantity the quantity to validate
     * @throws IllegalArgumentException if the quantity is invalid
     */
    void validateQuantity(int quantity) throws IllegalArgumentException;
}
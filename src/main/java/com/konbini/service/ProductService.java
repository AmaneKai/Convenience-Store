package com.konbini.service;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining the business logic layer for managing Product inventory data.
 * This service mediates between the presentation layer and the data repository,
 * handling product management, inventory updates, and various lookup functions for reporting.
 */
public interface ProductService {
    /**
     * Creates and adds a new product to the inventory.
     * A unique ID will be generated for the new product.
     *
     * @param name The name of the product.
     * @param price The unit price of the product.
     * @param quantity The initial stock quantity.
     * @param category The primary category of the product.
     * @param brand The brand name of the product.
     * @param subcategory The subcategory or variant of the product.
     * @param expirationDate The product's expiration date.
     */
    void addProduct(String name, double price, int quantity,
        ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate);

    /**
     * Updates the details of an existing product, identified by its unique ID.
     *
     * @param productId The unique ID of the product to update.
     * @param name The new name of the product.
     * @param price The new unit price.
     * @param quantity The new stock quantity.
     * @param category The new primary category.
     * @param brand The new brand name.
     * @param subcategory The new subcategory or variant.
     * @param expirationDate The new expiration date.
     */
    void updateProduct(String productId, String name, double price,
        int quantity, ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate);

    /**
     * Removes a product from the inventory based on its unique ID.
     *
     * @param productId The ID of the product to remove.
     */
    void removeProduct(String productId);

    /**
     * Increases the stock quantity of an existing product.
     *
     * @param productId The ID of the product to restock.
     * @param quantity The amount to add to the current stock.
     */
    void restockProduct(String productId, int quantity);

    /**
     * Retrieves a product record by its unique identifier.
     *
     * @param productId The ID of the product to find.
     * @return An Optional containing the Product if found, or an empty Optional otherwise.
     */
    Optional<Product> findById(String productId);

    /**
     * Retrieves all product records currently in the inventory.
     *
     * @return A List of all Product objects.
     */
    List<Product> findAll();

    /**
     * Retrieves all products belonging to a specified primary category.
     *
     * @param category The ProductCategory to filter by.
     * @return A List of products matching the category.
     */
    List<Product> findByCategory(ProductCategory category);

    /**
     * Retrieves all products belonging to a specified subcategory.
     *
     * @param subcategory The ProductSubcategory to filter by.
     * @return A List of products matching the subcategory.
     */
    List<Product> findBySubcategory(ProductSubcategory subcategory);

    /**
     * Retrieves all products whose names contain the specified search string (case-insensitive).
     *
     * @param name The search string to match against product names.
     * @return A List of products matching the search criteria.
     */
    List<Product> findByName(String name);

    /**
     * Retrieves a list of all products currently flagged as low stock.
     *
     * @return A List of products with stock levels below their threshold.
     */
    List<Product> findLowStock();

    /**
     * Retrieves a list of all products that have passed their expiration date.
     *
     * @return A List of expired products.
     */
    List<Product> findExpired();

    /**
     * Persists the current state of the product inventory data to the underlying storage mechanism.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    boolean saveInventory();

    /**
     * Loads the product inventory data from the underlying storage mechanism into memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    boolean loadInventory();
}

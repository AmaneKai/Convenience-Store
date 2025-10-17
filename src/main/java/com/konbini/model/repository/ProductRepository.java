package com.konbini.model.repository;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining the contract for data access operations related to the Product model.
 * Implementations of this interface manage the persistent storage and retrieval
 * of product inventory data, including lookup methods for specific categories and inventory status.
 */
public interface ProductRepository {
    /**
     * Persists a new product record to the repository.
     *
     * @param product The Product object to be added to inventory.
     */
    void addProduct(Product product);

    /**
     * Updates an existing product record in the repository.
     * The product's unique ID is used to locate the record to update.
     *
     * @param product The Product object with updated data (e.g., new price, updated quantity).
     */
    void updateProduct(Product product);

    /**
     * Removes a product record from the repository based on its unique ID.
     *
     * @param productId The ID of the product to remove.
     */
    void removeProduct(String productId);

    /**
     * Finds and retrieves a product by its unique identifier.
     *
     * @param productId The ID of the product to find.
     * @return An Optional containing the Product if found, or an empty Optional otherwise.
     */
    Optional<Product> findById(String productId);

    /**
     * Retrieves all product records currently stored in the repository.
     *
     * @return A List of all available Product objects.
     */
    List<Product> findAll();

    /**
     * Finds and retrieves all products belonging to a specified primary category.
     *
     * @param category The ProductCategory to filter by.
     * @return A List of products matching the category.
     */
    List<Product> findByCategory(ProductCategory category);

    /**
     * Finds and retrieves all products belonging to a specified subcategory.
     *
     * @param subcategory The ProductSubcategory to filter by.
     * @return A List of products matching the subcategory.
     */
    List<Product> findBySubcategory(ProductSubcategory subcategory);

    /**
     * Finds and retrieves all products whose names contain the specified search string.
     *
     * @param name The search string to match against product names.
     * @return A List of products matching the search criteria.
     */
    List<Product> findByName(String name);

    /**
     * Retrieves a list of all products whose current stock quantity is below their low stock threshold.
     *
     * @return A List of products flagged as low stock.
     */
    List<Product> findLowStock();

    /**
     * Retrieves a list of all products that have passed their expiration date.
     *
     * @return A List of expired products.
     */
    List<Product> findExpired();

    /**
     * Persists the current state of the repository data to its storage mechanism (e.g., file, database).
     *
     * @return True if the save operation was successful, false otherwise.
     */
    boolean save();

    /**
     * Loads the repository data from its persistent storage mechanism into memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    boolean load();
}

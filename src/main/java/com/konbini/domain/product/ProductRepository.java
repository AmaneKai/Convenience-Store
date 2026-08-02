package com.konbini.domain.product;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for products. Implementations are storage-agnostic
 * (CSV, database, in-memory) and are injected via the IoC container.
 */
public interface ProductRepository {

    /**
     * Persists a new product.
     *
     * @param product the product to add
     */
    void add(Product product);

    /**
     * Updates an existing product.
     *
     * @param product the product with updated values
     */
    void update(Product product);

    /**
     * Removes a product by ID.
     *
     * @param productId the product ID
     */
    void remove(String productId);

    /**
     * Finds a product by ID.
     *
     * @param productId the product ID
     * @return an Optional containing the product if found
     */
    Optional<Product> findById(String productId);

    /**
     * Returns all products.
     *
     * @return all products
     */
    List<Product> findAll();

    /**
     * Finds products whose category matches the given display name.
     *
     * @param categoryDisplayName the category display name
     * @return matching products
     */
    List<Product> findByCategory(String categoryDisplayName);

    /**
     * Finds products whose name contains the search term (case-insensitive).
     *
     * @param name the search term
     * @return matching products
     */
    List<Product> findByName(String name);

    /**
     * Returns all products currently below their low-stock threshold.
     *
     * @return low-stock products
     */
    List<Product> findLowStock();

    /**
     * Returns all products that have expired.
     *
     * @return expired products
     */
    List<Product> findExpired();
}

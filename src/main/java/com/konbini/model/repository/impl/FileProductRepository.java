package com.konbini.model.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.model.repository.ProductRepository;

/**
 * FileProductRepository provides a file-based implementation of the ProductRepository interface.
 * This implementation stores product data in a serialized file format and maintains an in-memory
 * cache of product objects for fast access. It supports basic CRUD operations and advanced
 * search capabilities including category filtering, low stock detection, and expiration tracking.
 */
public class FileProductRepository implements ProductRepository {
    /** In-memory cache of products stored by product ID */
    private final Map<String, Product> products;

    /** File path where product data is persisted */
    private final String filePath;

    /**
     * Constructs a new FileProductRepository with the specified file path.
     * Initializes the in-memory product cache.
     *
     * @param filePath the file path where product data will be stored and loaded from
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public FileProductRepository(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.products = new HashMap<>();
        this.filePath = filePath;
    }

    /**
     * Adds a new product to the repository.
     * The product is added to the in-memory cache but not automatically persisted to disk.
     *
     * @param product the Product object to add
     * @throws IllegalArgumentException if product is null
     */
    @Override
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.put(product.getId(), product);
    }

    /**
     * Updates an existing product in the repository.
     * Replaces the product with the same ID in the in-memory cache.
     *
     * @param product the Product object with updated information
     * @throws IllegalArgumentException if product is null or not found in repository
     */
    @Override
    public void updateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (!products.containsKey(product.getId())) {
            throw new IllegalArgumentException("Product not found: " + product.getId());
        }
        products.put(product.getId(), product);
    }

    /**
     * Removes a product from the repository by ID.
     * Removes the product from the in-memory cache but not automatically from disk.
     *
     * @param productId the ID of the product to remove
     * @throws IllegalArgumentException if productId is null or empty
     */
    @Override
    public void removeProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        products.remove(productId);
    }

    /**
     * Finds a product by its ID.
     *
     * @param productId the ID of the product to find
     * @return an Optional containing the Product if found, empty Optional otherwise
     */
    @Override
    public Optional<Product> findById(String productId) {
        Optional<Product> temp = Optional.empty();

        if (productId != null && !productId.trim().isEmpty()) {
            temp = Optional.ofNullable(products.get(productId));
        }

        return temp;
    }

    /**
     * Retrieves all products from the repository.
     *
     * @return a List containing all Product objects in the repository
     */
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * Finds products by category.
     * Filters products based on the specified product category.
     *
     * @param category the ProductCategory to filter by
     * @return a List of products belonging to the specified category, empty list if none found
     */
    @Override
    public List<Product> findByCategory(ProductCategory category) {
        List<Product> temp = new ArrayList<>();

        if (category != null) {
            temp = products.values().stream()
                    .filter(product -> product.getCategory().equals(category.getDisplayName()))
                    .collect(Collectors.toList());
        }

        return temp;
    }

    /**
     * Finds products by subcategory.
     * Filters products based on the specified product subcategory.
     *
     * @param subcategory the ProductSubcategory to filter by
     * @return a List of products belonging to the specified subcategory, empty list if none found
     */
    @Override
    public List<Product> findBySubcategory(ProductSubcategory subcategory) {
        List<Product> temp = new ArrayList<>();

        if (subcategory != null) {
            String subcategoryStr = subcategory.getDisplayName();
            temp = products.values().stream()
                    .filter(product -> product.getVariant() != null &&
                            product.getVariant().equals(subcategoryStr))
                    .collect(Collectors.toList());
        }

        return temp;
    }

    /**
     * Finds products by name using case-insensitive partial matching.
     * Searches for products whose names contain the specified search string.
     *
     * @param name the name or partial name to search for
     * @return a List of products matching the search criteria, empty list if none found
     */
    @Override
    public List<Product> findByName(String name) {
        List<Product> temp = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            temp = products.values().stream()
                    .filter(product -> product.getName().toLowerCase()
                            .contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return temp;
    }

    /**
     * Finds products with low stock levels.
     * Uses the Product.isLowStock() method to determine low stock status.
     *
     * @return a List of products that are low in stock, empty list if none found
     */
    @Override
    public List<Product> findLowStock() {
        return products.values().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    /**
     * Finds expired products.
     * Uses the Product.isExpired() method to determine expiration status.
     *
     * @return a List of products that have expired, empty list if none found
     */
    @Override
    public List<Product> findExpired() {
        return products.values().stream()
                .filter(Product::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * Saves all product data to the file system.
     * Serializes the current in-memory product cache to the specified file path.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean save() {
        boolean temp = false;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(products.values()));
            temp = true;
        } catch (IOException e) {
            System.err.println("Error saving product data to file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
        }

        return temp;
    }

    /**
     * Loads product data from the file system.
     * Deserializes product data from the specified file path into the in-memory cache.
     * If the file doesn't exist, the operation fails silently and returns false.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    public boolean load() {
        boolean temp = false;
        File file = new File(filePath);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                List<Product> loadedProducts = (List<Product>) ois.readObject();

                if (loadedProducts != null) {
                    products.clear();
                    loadedProducts.forEach(product -> {
                        if (product != null) {
                            products.put(product.getId(), product);
                        }
                    });
                    temp = true;
                } else {
                    products.clear();
                }
            } catch (IOException e) {
                System.err.println("Error reading product data from file: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Product class definition mismatch: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error loading product data: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            }
        }

        return temp;
    }
}
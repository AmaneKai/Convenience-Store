package com.konbini.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.model.repository.ProductRepository;
import com.konbini.service.ProductService;

/**
 * ProductServiceImpl provides business logic implementation for product management operations.
 * This service handles product CRUD operations, inventory management, search functionality,
 * and validation of product data according to business rules.
 */
public class ProductServiceImpl implements ProductService {
    /** Repository for product data persistence operations */
    private final ProductRepository productRepository;

    /**
     * Constructs a new ProductServiceImpl with the specified product repository.
     *
     * @param productRepository the ProductRepository for data access operations
     * @throws IllegalArgumentException if productRepository is null
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new IllegalArgumentException("Product repository cannot be null");
        }
        this.productRepository = productRepository;
    }

    /**
     * Adds a new product to the inventory with comprehensive validation.
     *
     * @param name the name of the product
     * @param price the price of the product (must be non-negative)
     * @param quantity the initial quantity in stock (must be non-negative)
     * @param category the product category (cannot be null)
     * @param brand the product brand (can be null)
     * @param subcategory the product subcategory (can be null, but must belong to category if provided)
     * @param expirationDate the expiration date (can be null, but cannot be in the past if provided)
     * @throws IllegalArgumentException if any parameter fails validation
     */
    @Override
    public void addProduct(String name, double price, int quantity,
                           ProductCategory category, String brand,
                           ProductSubcategory subcategory, LocalDate expirationDate) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (subcategory != null && subcategory.getCategory() != category) {
            throw new IllegalArgumentException("Subcategory must belong to the specified category");
        }
        if (expirationDate != null && expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date cannot be in the past");
        }

        Product product = new Product(
                name,
                price,
                quantity,
                category.getDisplayName(),
                brand,
                subcategory != null ? subcategory.getDisplayName() : null,
                expirationDate
        );

        productRepository.addProduct(product);
    }

    /**
     * Updates an existing product's information with partial updates.
     * Only updates provided non-null and valid values.
     *
     * @param productId the ID of the product to update
     * @param name the new product name (optional, can be null to keep existing)
     * @param price the new price (optional, must be positive if provided)
     * @param quantity the new quantity (optional, must be non-negative if provided)
     * @param category the new category (optional, can be null to keep existing)
     * @param brand the new brand (optional, can be null to clear existing)
     * @param subcategory the new subcategory (optional, can be null to clear existing)
     * @param expirationDate the new expiration date (optional, can be null to clear existing)
     * @throws IllegalArgumentException if product not found or any parameter fails validation
     */
    @Override
    public void updateProduct(String productId, String name, double price,
                              int quantity, ProductCategory category, String brand,
                              ProductSubcategory subcategory, LocalDate expirationDate) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (name != null && !name.isEmpty()) {
            product.setName(name);
        }

        if (price > 0) {
            product.setPrice(price);
        }

        if (quantity >= 0) {
            product.setQuantity(quantity);
        }

        if (category != null) {
            product.setCategory(category.getDisplayName());
        }

        product.setBrand(brand);

        if (subcategory != null) {
            if (category != null && subcategory.getCategory() != category) {
                throw new IllegalArgumentException("Subcategory must belong to the specified category");
            }
            product.setVariant(subcategory.getDisplayName());
        }

        if (expirationDate != null) {
            product.setExpirationDate(expirationDate);
        }

        productRepository.updateProduct(product);
    }

    /**
     * Removes a product from the inventory.
     *
     * @param productId the ID of the product to remove
     * @throws IllegalArgumentException if product not found
     */
    @Override
    public void removeProduct(String productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        productRepository.removeProduct(productId);
    }

    /**
     * Restocks a product by adding to its current quantity.
     *
     * @param productId the ID of the product to restock
     * @param quantity the quantity to add (must be positive)
     * @throws IllegalArgumentException if quantity not positive or product not found
     */
    @Override
    public void restockProduct(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        product.increaseQuantity(quantity);
        productRepository.updateProduct(product);
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
            temp = productRepository.findById(productId);
        }

        return temp;
    }

    /**
     * Retrieves all products from the inventory.
     *
     * @return a List containing all Product objects
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Finds products by category.
     * Returns all products if category is null.
     *
     * @param category the ProductCategory to filter by
     * @return a List of products belonging to the specified category, or all products if category is null
     */
    @Override
    public List<Product> findByCategory(ProductCategory category) {
        List<Product> temp;

        if (category == null) {
            temp = productRepository.findAll();
        } else {
            temp = productRepository.findByCategory(category);
        }

        return temp;
    }

    /**
     * Finds products by subcategory.
     * Returns all products if subcategory is null.
     *
     * @param subcategory the ProductSubcategory to filter by
     * @return a List of products belonging to the specified subcategory, or all products if subcategory is null
     */
    @Override
    public List<Product> findBySubcategory(ProductSubcategory subcategory) {
        List<Product> temp;

        if (subcategory == null) {
            temp = productRepository.findAll();
        } else {
            temp = productRepository.findBySubcategory(subcategory);
        }

        return temp;
    }

    /**
     * Finds products by name using case-insensitive partial matching.
     * Returns all products if name is null or empty.
     *
     * @param name the name or partial name to search for
     * @return a List of products matching the search criteria, or all products if name is null or empty
     */
    @Override
    public List<Product> findByName(String name) {
        List<Product> temp;

        if (name == null || name.trim().isEmpty()) {
            temp = productRepository.findAll();
        } else {
            temp = productRepository.findByName(name);
        }

        return temp;
    }

    /**
     * Finds products with low stock levels.
     *
     * @return a List of products that are low in stock
     */
    @Override
    public List<Product> findLowStock() {
        return productRepository.findLowStock();
    }

    /**
     * Finds expired products.
     *
     * @return a List of products that have expired
     */
    @Override
    public List<Product> findExpired() {
        return productRepository.findExpired();
    }

    /**
     * Saves all product data to persistent storage.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean saveInventory() {
        return productRepository.save();
    }

    /**
     * Loads all product data from persistent storage.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    public boolean loadInventory() {
        return productRepository.load();
    }

    /**
     * Validates that a product price is greater than 0.
     *
     * @param price the price to validate
     * @throws IllegalArgumentException if price is not greater than 0
     */
    @Override
    public void validatePrice(double price) throws IllegalArgumentException {
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
    }

    /**
     * Validates that a product quantity is non-negative.
     *
     * @param quantity the quantity to validate
     * @throws IllegalArgumentException if quantity is negative
     */
    @Override
    public void validateQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
    }
}
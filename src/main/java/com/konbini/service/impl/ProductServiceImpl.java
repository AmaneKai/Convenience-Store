package com.konbini.service.impl;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.model.repository.ProductRepository;
import com.konbini.service.ProductService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of the ProductService interface.
 * This class provides the business logic for managing product inventory,
 * including validation, CRUD operations, and delegation of data persistence
 * to the ProductRepository.
 */
public class ProductServiceImpl implements ProductService {
    /**
     * The data access object responsible for persistent storage and retrieval of Product data.
     */
    private final ProductRepository productRepository;

    /**
     * Constructs a ProductServiceImpl with a dependency on a ProductRepository.
     *
     * @param productRepository The repository used for data persistence.
     */
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates and adds a new product to the inventory after validating the input data.
     *
     * @param name The name of the product.
     * @param price The unit price of the product.
     * @param quantity The initial stock quantity.
     * @param category The primary category of the product.
     * @param brand The brand name of the product.
     * @param subcategory The subcategory or variant of the product (optional).
     * @param expirationDate The product's expiration date (optional).
     * @throws IllegalArgumentException if any validation rule fails (e.g., name is empty, price is negative, expiration date is in the past).
     */
    @Override
    public void addProduct(String name, double price, int quantity,
        ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException
            ("Product name cannot be empty");
        }

        if (price < 0) {
            throw new IllegalArgumentException
            ("Price cannot be negative");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException
            ("Quantity cannot be negative");
        }

        if (category == null) {
            throw new IllegalArgumentException
            ("Category cannot be null");
        }

        if (subcategory != null && subcategory.getCategory() != category) {
            throw new IllegalArgumentException
            ("Subcategory must belong to the specified category");
        }

        if (expirationDate != null && expirationDate
            .isBefore(LocalDate.now())) {
            throw new IllegalArgumentException
            ("Expiration date cannot be in the past");
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
     * Updates the details of an existing product. Only fields with valid, non-null, or non-negative values are updated.
     *
     * @param productId The unique ID of the product to update.
     * @param name The new name of the product (null to keep current name).
     * @param price The new unit price (ignored if negative).
     * @param quantity The new stock quantity (ignored if negative).
     * @param category The new primary category (null to keep current category).
     * @param brand The new brand name (null to keep current brand).
     * @param subcategory The new subcategory or variant (null to keep current subcategory).
     * @param expirationDate The new expiration date (null to keep current date).
     * @throws IllegalArgumentException if the product ID is not found, or if subcategory/category mismatch occurs.
     */
    @Override
    public void updateProduct(String productId, String name, double price,
        int quantity, ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate) {
        Optional<Product> optionalProduct = productRepository
            .findById(productId);

        if (!optionalProduct.isPresent()) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        Product product = optionalProduct.get();

        if (name != null ) {
            if (name.isEmpty()) {
                throw new IllegalArgumentException
                ("Product name cannot be empty");
            }
            product.setName(name);
        }

        if (price >= 0) {
            product.setPrice(price);
        }

        if (quantity >= 0) {
            product.setQuantity(quantity);
        }

        if (category != null) {
            product.setCategory(category.getDisplayName());
        }

        // Brand can be null or empty
        product.setBrand(brand);

        if (subcategory != null) {
            // Check for category mismatch if a new category was provided in the update
            if (category != null && subcategory.getCategory() != category) {
                throw new IllegalArgumentException
                ("Subcategory must belong to the specified category");
            }
            product.setVariant(subcategory.getDisplayName());
        }

        if (expirationDate != null) {
            product.setExpirationDate(expirationDate);
        }

        productRepository.updateProduct(product);
    }

    /**
     * Removes a product from the inventory based on its unique ID.
     *
     * @param productId The ID of the product to remove.
     * @throws IllegalArgumentException if the product ID is not found.
     */
    @Override
    public void removeProduct(String productId) {
        Optional<Product> optionalProduct = productRepository
            .findById(productId);

        if (!optionalProduct.isPresent()) {
            throw new IllegalArgumentException
            ("Product not found: " + productId);
        }

        productRepository.removeProduct(productId);
    }

    /**
     * Increases the stock quantity of an existing product.
     *
     * @param productId The ID of the product to restock.
     * @param quantity The amount to add to the current stock.
     * @throws IllegalArgumentException if the restock quantity is not positive or the product ID is not found.
     */
    @Override
    public void restockProduct(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException
            ("Restock quantity must be positive");
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (!optionalProduct.isPresent()) {
            throw new IllegalArgumentException
            ("Product not found: " + productId);
        }

        Product product = optionalProduct.get();
        product.increaseQuantity(quantity);
        productRepository.updateProduct(product);
    }

    /**
     * Retrieves a product record by its unique identifier.
     * Delegates directly to the repository.
     *
     * @param productId The ID of the product to find.
     * @return An Optional containing the Product if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Product> findById(String productId) {
        return productRepository.findById(productId);
    }

    /**
     * Retrieves all product records in the inventory.
     * Delegates directly to the repository.
     *
     * @return A List of all Product objects.
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Retrieves all products belonging to a specified primary category.
     * Delegates directly to the repository.
     *
     * @param category The ProductCategory to filter by.
     * @return A List of products matching the category.
     */
    @Override
    public List<Product> findByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Retrieves all products belonging to a specified subcategory.
     * Delegates directly to the repository.
     *
     * @param subcategory The ProductSubcategory to filter by.
     * @return A List of products matching the subcategory.
     */
    @Override
    public List<Product> findBySubcategory(ProductSubcategory subcategory) {
        return productRepository.findBySubcategory(subcategory);
    }

    /**
     * Retrieves all products whose names contain the specified search string (case-insensitive).
     * Delegates directly to the repository.
     *
     * @param name The search string to match against product names.
     * @return A List of products matching the search criteria.
     */
    @Override
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    /**
     * Retrieves a list of all products currently flagged as low stock.
     * Delegates directly to the repository.
     *
     * @return A List of products with stock levels below their threshold.
     */
    @Override
    public List<Product> findLowStock() {
        return productRepository.findLowStock();
    }

    /**
     * Retrieves a list of all products that have passed their expiration date.
     * Delegates directly to the repository.
     *
     * @return A List of expired products.
     */
    @Override
    public List<Product> findExpired() {
        return productRepository.findExpired();
    }

    /**
     * Persists the current state of the product inventory data.
     * Delegates the save operation to the repository.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    @Override
    public boolean saveInventory() {
        return productRepository.save();
    }

    /**
     * Loads the product inventory data from storage into memory.
     * Delegates the load operation to the repository.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    @Override
    public boolean loadInventory() {
        return productRepository.load();
    }
}

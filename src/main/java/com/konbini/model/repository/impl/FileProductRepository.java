package com.konbini.model.repository.impl;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.model.repository.ProductRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the ProductRepository interface that uses file serialization
 * for persistence. Product inventory data is stored in memory using a Map for fast access
 * and saved/loaded from a file on the disk using Java's built-in serialization mechanism.
 */
public class FileProductRepository implements ProductRepository {
    /**
     * In-memory storage for product records, mapped by their unique ID.
     */
    private final Map<String, Product> products;
    /**
     * The file path used for saving and loading the serialized product data.
     */
    private final String filePath;

    /**
     * Constructs a new FileProductRepository.
     * Initializes the in-memory map and sets the path for persistent storage.
     *
     * @param filePath The path to the file where product data will be serialized.
     */
    public FileProductRepository(String filePath) {
        this.products = new HashMap<>();
        this.filePath = filePath;
    }

    /**
     * Adds a new product to the in-memory repository.
     *
     * @param product The Product object to be added to inventory.
     */
    @Override
    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    /**
     * Updates an existing product record.
     * If the product ID exists, the old record is replaced with the new Product object.
     *
     * @param product The Product object with updated data.
     * @throws IllegalArgumentException if the product ID does not exist in the repository.
     */
    @Override
    public void updateProduct(Product product) {
        if (products.containsKey(product.getId())) {
            products.put(product.getId(), product);
        } else {
            throw new IllegalArgumentException
            ("Product not found: " + product.getId());
        }
    }

    /**
     * Removes a product record from the repository based on its unique ID.
     *
     * @param productId The ID of the product to remove.
     */
    @Override
    public void removeProduct(String productId) {
        products.remove(productId);
    }

    /**
     * Finds and retrieves a product by its unique identifier.
     *
     * @param productId The ID of the product to find.
     * @return An Optional containing the Product if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Product> findById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }

    /**
     * Retrieves all product records currently stored in the repository.
     *
     * @return A new List containing all Product objects.
     */
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    /**
     * Finds and retrieves all products belonging to a specified primary category.
     * This method filters products where the category display name matches the product's category field.
     *
     * @param category The ProductCategory to filter by.
     * @return A List of products matching the category.
     */
    @Override
    public List<Product> findByCategory(ProductCategory category) {
        return products.values().stream()
                .filter(product -> product.getCategory()
                .equals(category.getDisplayName()))
                .collect(Collectors.toList());
    }

    /**
     * Finds and retrieves all products belonging to a specified subcategory.
     * This method filters products where the variant field matches the subcategory's display name.
     *
     * @param subcategory The ProductSubcategory to filter by.
     * @return A List of products matching the subcategory.
     */
    @Override
    public List<Product> findBySubcategory(ProductSubcategory subcategory) {
        String subcategoryStr = subcategory.getDisplayName();
        return products.values().stream()
                .filter(product -> product.getVariant() != null && product
                .getVariant().equals(subcategoryStr))
                .collect(Collectors.toList());
    }

    /**
     * Finds and retrieves all products whose names contain the specified search string (case-insensitive).
     *
     * @param name The search string to match against product names.
     * @return A List of products matching the search criteria.
     */
    @Override
    public List<Product> findByName(String name) {
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase()
                .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all products whose current stock quantity is below their low stock threshold.
     *
     * @return A List of products flagged as low stock.
     */
    @Override
    public List<Product> findLowStock() {
        return products.values().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all products that have passed their expiration date.
     *
     * @return A List of expired products.
     */
    @Override
    public List<Product> findExpired() {
        return products.values().stream()
                .filter(Product::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * Serializes and persists the current in-memory product data to the configured file path.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    @Override
    public boolean save() {
        try (ObjectOutputStream oos = new ObjectOutputStream
            (new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(products.values()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the product data from the serialized file into the in-memory repository.
     * If the file does not exist, the load operation fails silently, and the repository remains empty.
     *
     * @return True if the load operation was successful, false otherwise (including file not found or deserialization errors).
     */
    @Override
    public boolean load() {
        File file = new File(filePath);

        if (!file.exists()) {
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream
            (new FileInputStream(file))) {
            List<Product> loadedProducts = (List<Product>) ois.readObject();
            products.clear();
            loadedProducts.forEach(product -> products
                .put(product.getId(), product));
            return true;
        } catch (IOException e) {
            System.err.println("Error reading product data from file: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Product class definition not found during deserialization: " + e.getMessage());
            return false;
        }
    }
}

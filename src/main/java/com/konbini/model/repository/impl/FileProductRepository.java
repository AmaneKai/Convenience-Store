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

public class FileProductRepository implements ProductRepository {
    private final Map<String, Product> products;
    private final String filePath;

    public FileProductRepository(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.products = new HashMap<>();
        this.filePath = filePath;
    }

    @Override
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        products.put(product.getId(), product);
    }

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

    @Override
    public void removeProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        products.remove(productId);
    }

    @Override
    public Optional<Product> findById(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public List<Product> findByCategory(ProductCategory category) {
        if (category == null) {
            return new ArrayList<>();
        }
        return products.values().stream()
                .filter(product -> product.getCategory().equals(category.getDisplayName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findBySubcategory(ProductSubcategory subcategory) {
        if (subcategory == null) {
            return new ArrayList<>();
        }
        String subcategoryStr = subcategory.getDisplayName();
        return products.values().stream()
                .filter(product -> product.getVariant() != null && 
                        product.getVariant().equals(subcategoryStr))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase()
                        .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findLowStock() {
        return products.values().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findExpired() {
        return products.values().stream()
                .filter(Product::isExpired)
                .collect(Collectors.toList());
    }

    @Override
    public boolean save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(products.values()));
            return true;
        } catch (IOException e) {
            System.err.println("Error saving product data to file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean load() {
        File file = new File(filePath);

        if (!file.exists()) {
            return false; 
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<Product> loadedProducts = (List<Product>) ois.readObject();
            
            if (loadedProducts == null) {
                products.clear();
                return false;
            }

            products.clear();
            loadedProducts.forEach(product -> {
                if (product != null) {
                    products.put(product.getId(), product);
                }
            });
            return true;
        } catch (IOException e) {
            System.err.println("Error reading product data from file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Product class definition mismatch: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error loading product data: " + filePath);
            System.err.println("Reason: " + e.getMessage());
            return false;
        }
    }
}
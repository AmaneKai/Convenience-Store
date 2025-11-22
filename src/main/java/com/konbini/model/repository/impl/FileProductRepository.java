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
        Optional<Product> temp = Optional.empty();

        if (productId != null && !productId.trim().isEmpty()) {
            temp = Optional.ofNullable(products.get(productId));
        }

        return temp;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

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
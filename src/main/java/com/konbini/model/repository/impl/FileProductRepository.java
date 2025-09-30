package com.konbini.model.repository.impl;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.model.repository.ProductRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FileProductRepository implements ProductRepository {
    private final Map<String, Product> products;
    private final String filePath;
    
    public FileProductRepository(String filePath) {
        this.products = new HashMap<>();
        this.filePath = filePath;
    }
    
    @Override
    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }
    
    @Override
    public void updateProduct(Product product) {
        if (products.containsKey(product.getId())) {
            products.put(product.getId(), product);
        } else {
            throw new IllegalArgumentException
            ("Product not found: " + product.getId());
        }
    }
    
    @Override
    public void removeProduct(String productId) {
        products.remove(productId);
    }
    
    @Override
    public Optional<Product> findById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }
    
    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }
    
    @Override
    public List<Product> findByCategory(ProductCategory category) {
        return products.values().stream()
                .filter(product -> product.getCategory()
                .equals(category.getDisplayName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findBySubcategory(ProductSubcategory subcategory) {
        String subcategoryStr = subcategory.getDisplayName();
        return products.values().stream()
                .filter(product -> product.getVariant() != null && product
                .getVariant().equals(subcategoryStr))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> findByName(String name) {
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
        try (ObjectOutputStream oos = new ObjectOutputStream
            (new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(products.values()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
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
            System.err.println("Product class definition not found: " +
            e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("Product class definiton not found: " +
                e.getMessage());
            return false;
        }
    }
}

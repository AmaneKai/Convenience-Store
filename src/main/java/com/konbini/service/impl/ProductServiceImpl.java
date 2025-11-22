package com.konbini.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.model.repository.ProductRepository;
import com.konbini.service.ProductService;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new IllegalArgumentException("Product repository cannot be null");
        }
        this.productRepository = productRepository;
    }

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

    @Override
    public void removeProduct(String productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        productRepository.removeProduct(productId);
    }

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

    @Override
    public Optional<Product> findById(String productId) {
        Optional<Product> temp = Optional.empty();

        if (productId != null && !productId.trim().isEmpty()) {
            temp = productRepository.findById(productId);
        }

        return temp;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

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

    @Override
    public List<Product> findLowStock() {
        return productRepository.findLowStock();
    }

    @Override
    public List<Product> findExpired() {
        return productRepository.findExpired();
    }

    @Override
    public boolean saveInventory() {
        return productRepository.save();
    }

    @Override
    public boolean loadInventory() {
        return productRepository.load();
    }

    @Override
    public void validatePrice(double price) throws IllegalArgumentException {
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
    }

    @Override
    public void validateQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
    }
}
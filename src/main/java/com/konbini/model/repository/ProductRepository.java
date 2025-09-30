package com.konbini.model.repository;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void addProduct(Product product);
    void updateProduct(Product product);
    void removeProduct(String productId);
    Optional<Product> findById(String productId);
    List<Product> findAll();
    List<Product> findByCategory(ProductCategory category);
    List<Product> findBySubcategory(ProductSubcategory subcategory);
    List<Product> findByName(String name);
    List<Product> findLowStock();
    List<Product> findExpired();
    boolean save();
    boolean load();
}

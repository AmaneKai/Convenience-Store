package com.konbini.service;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ProductService {

    void addProduct(String name, double price, int quantity,
        ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate);

    void updateProduct(String productId, String name, double price,
        int quantity, ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate);

    void removeProduct(String productId);

    void restockProduct(String productId, int quantity);

    Optional<Product> findById(String productId);

    List<Product> findAll();

    List<Product> findByCategory(ProductCategory category);

    List<Product> findBySubcategory(ProductSubcategory subcategory);

    List<Product> findByName(String name);

    List<Product> findLowStock();

    List<Product> findExpired();

    boolean saveInventory();

    boolean loadInventory();

    void validatePrice(double price) throws IllegalArgumentException;

    void validateQuantity(int quantity) throws IllegalArgumentException;
}

package com.konbini.controller;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.service.ProductService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productService.findByCategory(category);
    }

    public List<Product> getProductsBySubcategory
        (ProductSubcategory subcategory) {
        return productService.findBySubcategory(subcategory);
    }

    public List<Product> searchProductsByName(String name) {
        return productService.findByName(name);
    }

    public Optional<Product> getProductById(String productId) {
        return productService.findById(productId);
    }

    public List<Product> getLowStockProducts() {
        return productService.findLowStock();
    }

    public List<Product> getExpiredProducts() {
        return productService.findExpired();
    }

    public void addProduct(String name, double price, int quantity,
        ProductCategory category, String brand, ProductSubcategory subcategory,
        LocalDate expirationDate) {
        productService.addProduct(name, price, quantity, category, brand,
            subcategory, expirationDate);
    }

    public void updateProduct(String productId, String name,
        double price, int quantity, ProductCategory category, String brand,
        ProductSubcategory subcategory, LocalDate expirationDate) {
        productService.updateProduct(productId, name, price, quantity,
            category, brand, subcategory, expirationDate);
    }

    public void removeProduct(String productId) {
        productService.removeProduct(productId);
    }

    public void restockProduct(String productId, int quantity) {
        productService.restockProduct(productId, quantity);
    }

    public boolean saveData() {
        return productService.saveInventory();
    }

    public boolean loadData() {
        return productService.loadInventory();
    }
}

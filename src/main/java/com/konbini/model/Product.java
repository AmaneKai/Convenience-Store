package com.konbini.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.konbini.util.IdGenerator;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    private final String id;
    private String name;
    private double price;
    private int quantity;
    private String category;
    private String brand;
    private String variant;
    private LocalDate expirationDate;
    private int lowStockThreshold = DEFAULT_LOW_STOCK_THRESHOLD;

    public Product(String name, double price, int quantity, String category, 
        String brand, String variant, LocalDate expirationDate) {
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }

        this.id = IdGenerator.getInstance().generateId("product");
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.brand = brand != null ? brand : "";
        this.variant = variant != null ? variant : "";
        this.expirationDate = expirationDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand != null ? brand : "";
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant != null ? variant : "";
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isExpired() {
        boolean temp = false;

        if (expirationDate != null) {
            temp = expirationDate.isBefore(LocalDate.now());
        }

        return temp;
    }
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
        this.lowStockThreshold = threshold;
    }

    public boolean isLowStock() {
        return quantity < lowStockThreshold;
    }

    public void decreaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot decrease by negative amount");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException(
                "Cannot decrease more than available quantity. " +
                "Available: " + quantity + ", Requested: " + amount);
        }
        this.quantity -= amount;
    }

    public void increaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot increase by negative amount");
        }
        this.quantity += amount;
    }
}
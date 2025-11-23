package com.konbini.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.konbini.util.IdGenerator;

/**
 * Represents a product in the store inventory with details, pricing, and inventory management.
 * Tracks product information, stock levels, expiration dates, and low stock alerts.
 * Implements Serializable to support persistence.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Default threshold for considering a product as low stock.
     */
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

    /**
     * Constructs a new Product with the specified details.
     * Automatically generates a unique ID for the product.
     *
     * @param name the product name (cannot be null or empty)
     * @param price the product price (must be greater than 0)
     * @param quantity the initial stock quantity (cannot be negative)
     * @param category the product category (cannot be null or empty)
     * @param brand the product brand (can be null or empty)
     * @param variant the product variant/subcategory (can be null or empty)
     * @param expirationDate the product expiration date (can be null for non-perishable items)
     * @throws IllegalArgumentException if any required parameter is invalid
     */
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

    /**
     * Gets the product's unique identifier.
     * The ID is automatically generated during construction and cannot be changed.
     *
     * @return the product ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     *
     * @param name the new product name
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Gets the product price.
     *
     * @return the product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     *
     * @param price the new product price
     * @throws IllegalArgumentException if price is not greater than 0
     */
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        this.price = price;
    }

    /**
     * Gets the current stock quantity.
     *
     * @return the quantity in stock
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the stock quantity.
     *
     * @param quantity the new quantity
     * @throws IllegalArgumentException if quantity is negative
     */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    /**
     * Gets the product category.
     *
     * @return the product category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the product category.
     *
     * @param category the new category
     * @throws IllegalArgumentException if category is null or empty
     */
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category;
    }

    /**
     * Gets the product brand.
     *
     * @return the product brand (empty string if no brand specified)
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the product brand.
     *
     * @param brand the new brand (null will be converted to empty string)
     */
    public void setBrand(String brand) {
        this.brand = brand != null ? brand : "";
    }

    /**
     * Gets the product variant or subcategory.
     *
     * @return the product variant (empty string if no variant specified)
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the product variant or subcategory.
     *
     * @param variant the new variant (null will be converted to empty string)
     */
    public void setVariant(String variant) {
        this.variant = variant != null ? variant : "";
    }

    /**
     * Gets the product expiration date.
     *
     * @return the expiration date, or null if the product doesn't expire
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the product expiration date.
     *
     * @param expirationDate the new expiration date (can be null for non-perishable items)
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Checks if the product has expired.
     * Non-perishable products (null expiration date) never expire.
     *
     * @return true if the product has an expiration date and it is before today, false otherwise
     */
    public boolean isExpired() {
        boolean temp = false;

        if (expirationDate != null) {
            temp = expirationDate.isBefore(LocalDate.now());
        }

        return temp;
    }

    /**
     * Gets the low stock threshold for this product.
     * When quantity falls below this threshold, the product is considered low stock.
     *
     * @return the low stock threshold
     */
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    /**
     * Sets the low stock threshold for this product.
     *
     * @param threshold the new low stock threshold
     * @throws IllegalArgumentException if threshold is negative
     */
    public void setLowStockThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
        this.lowStockThreshold = threshold;
    }

    /**
     * Checks if the product is in low stock.
     *
     * @return true if current quantity is below the low stock threshold, false otherwise
     */
    public boolean isLowStock() {
        return quantity < lowStockThreshold;
    }

    /**
     * Decreases the product quantity by the specified amount.
     *
     * @param amount the amount to decrease (must be non-negative)
     * @throws IllegalArgumentException if amount is negative or exceeds available quantity
     */
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

    /**
     * Increases the product quantity by the specified amount.
     *
     * @param amount the amount to increase (must be non-negative)
     * @throws IllegalArgumentException if amount is negative
     */
    public void increaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot increase by negative amount");
        }
        this.quantity += amount;
    }
}
package com.konbini.domain.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Aggregate root representing a product in the store inventory.
 * Encapsulates pricing, stock levels, expiration tracking and low-stock alerts.
 * The category and variant are stored as user-facing display names to preserve
 * legacy data fidelity; the {@link ProductCategory} and {@link ProductSubcategory}
 * enumerations act as reference dictionaries for the UI and application layers.
 */
@Getter
public class Product {

    /**
     * Default threshold below which a product is considered low stock.
     */
    public static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    /**
     * Regular expression used to validate product names.
     */
    public static final String NAME_PATTERN = "^[\\p{L}\\p{N}][\\p{L}\\p{N} .,'&-]*$";

    private final String id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String category;
    private String brand;
    private String variant;
    private LocalDate expirationDate;
    private int lowStockThreshold;

    /**
     * Constructs a fully specified Product.
     *
     * @param id the product identifier
     * @param name the product name
     * @param price the unit price
     * @param quantity the current stock quantity
     * @param category the product category display name
     * @param brand the product brand (may be null or empty)
     * @param variant the product subcategory display name (may be null or empty)
     * @param expirationDate the expiration date (may be null for non-perishables)
     * @param lowStockThreshold the threshold below which stock is considered low
     * @throws IllegalArgumentException if any required parameter is invalid
     */
    @Builder
    public Product(String id, String name, BigDecimal price, int quantity, String category,
                   String brand, String variant, LocalDate expirationDate,
                   Integer lowStockThreshold) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.brand = brand != null ? brand : "";
        this.variant = variant != null ? variant : "";
        this.expirationDate = expirationDate;
        this.lowStockThreshold = lowStockThreshold != null
                ? lowStockThreshold : DEFAULT_LOW_STOCK_THRESHOLD;
    }

    /**
     * Changes the product name.
     *
     * @param newName the new name
     * @throws IllegalArgumentException if the new name is null or empty
     */
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.name = newName;
    }

    /**
     * Changes the unit price.
     *
     * @param newPrice the new price
     * @throws IllegalArgumentException if the new price is not greater than zero
     */
    public void updatePrice(BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        this.price = newPrice;
    }

    /**
     * Changes the product category.
     *
     * @param newCategory the new category display name
     * @throws IllegalArgumentException if the new category is null or empty
     */
    public void updateCategory(String newCategory) {
        if (newCategory == null || newCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
        this.category = newCategory;
    }

    /**
     * Changes the product brand; null values are normalized to an empty string.
     *
     * @param newBrand the new brand
     */
    public void updateBrand(String newBrand) {
        this.brand = newBrand != null ? newBrand : "";
    }

    /**
     * Changes the product variant; null values are normalized to an empty string.
     *
     * @param newVariant the new variant
     */
    public void updateVariant(String newVariant) {
        this.variant = newVariant != null ? newVariant : "";
    }

    /**
     * Replaces the expiration date.
     *
     * @param newExpirationDate the new expiration date (may be null)
     */
    public void updateExpirationDate(LocalDate newExpirationDate) {
        this.expirationDate = newExpirationDate;
    }

    /**
     * Sets the low stock threshold.
     *
     * @param threshold the new threshold
     * @throws IllegalArgumentException if the threshold is negative
     */
    public void updateLowStockThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
        this.lowStockThreshold = threshold;
    }

    /**
     * Determines whether this product has expired.
     * Non-perishable products never expire.
     *
     * @return true if the product has an expiration date before today
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    /**
     * Determines whether current stock is below the configured threshold.
     *
     * @return true if the product is low on stock
     */
    public boolean isLowStock() {
        return quantity < lowStockThreshold;
    }

    /**
     * Reduces stock by the specified amount.
     *
     * @param amount the amount to remove
     * @throws IllegalArgumentException if the amount is negative or exceeds stock
     */
    public void decreaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot decrease by negative amount");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException(
                    "Cannot decrease more than available quantity. "
                            + "Available: " + quantity + ", Requested: " + amount);
        }
        this.quantity -= amount;
    }

    /**
     * Sets the stock to an absolute quantity (full-field update operation).
     *
     * @param newQuantity the new quantity
     * @throws IllegalArgumentException if the quantity is negative
     */
    public void setQuantity(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
        this.quantity = newQuantity;
    }

    /**
     * Increases stock by the specified amount (restock operation).
     *
     * @param amount the amount to add
     * @throws IllegalArgumentException if the amount is negative
     */
    public void increaseQuantity(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot increase by negative amount");
        }
        this.quantity += amount;
    }
}

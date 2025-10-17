package com.konbini.model;

import com.konbini.util.IdGenerator;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents an individual product item in the convenience store's inventory.
 * This class tracks product attributes, pricing, current stock, and inventory
 * status (expiration, low stock). The product ID is generated automatically upon creation.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique, auto-generated identifier for the product. This field is immutable.
     */
    private final String id;
    /**
     * The name of the product.
     */
    private String name;
    /**
     * The selling price of the product.
     */
    private double price;
    /**
     * The current quantity of the product in stock.
     */
    private int quantity;
    /**
     * The main category of the product (e.g., "Food", "Beverage").
     */
    private String category;
    /**
     * The brand name of the product.
     */
    private String brand;
    /**
     * The specific variant or subcategory of the product.
     */
    private String variant;
    /**
     * The expiration date of the product. Null if the product is non-perishable.
     */
    private LocalDate expirationDate;
    /**
     * The default stock level below which a product is considered 'low stock'.
     */
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;
    /**
     * The current low stock threshold setting for this specific product.
     */
    private int lowStockThreshold = DEFAULT_LOW_STOCK_THRESHOLD;

    /**
     * Constructs a new Product instance, automatically generating a unique ID.
     *
     * @param name The name of the product.
     * @param price The selling price.
     * @param quantity The initial stock quantity.
     * @param category The product category.
     * @param brand The product brand.
     * @param variant The product variant/subcategory.
     * @param expirationDate The expiration date, or null if non-perishable.
     */
    public Product(String name, double price, int quantity, String category, 
        String brand, String variant, LocalDate expirationDate) {
        this.id = IdGenerator.getInstance().generateId("product");
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.brand = brand;
        this.variant = variant;
        this.expirationDate = expirationDate;
    }

    /**
     * Retrieves the unique identifier of the product.
     *
     * @return The product ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the product.
     *
     * @return The product name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product.
     *
     * @param name The new name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the selling price of the product.
     *
     * @return The product price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the selling price of the product.
     *
     * @param price The new price to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Retrieves the current stock quantity.
     *
     * @return The current quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the current stock quantity.
     *
     * @param quantity The new quantity to set.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Retrieves the product category.
     *
     * @return The category string.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the product category.
     *
     * @param category The new category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Retrieves the product brand.
     *
     * @return The brand string.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the product brand.
     *
     * @param brand The new brand to set.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Retrieves the product variant/subcategory.
     *
     * @return The variant string.
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the product variant/subcategory.
     *
     * @param variant The new variant to set.
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Retrieves the expiration date.
     *
     * @return The expiration date, or null if none is set.
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expiration date.
     *
     * @param expirationDate The new expiration date to set.
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Checks if the product has expired based on the current date.
     * Non-perishable items (with a null expiration date) are considered not expired.
     *
     * @return True if the expiration date is before the current date, false otherwise.
     */
    public boolean isExpired() {
        if (expirationDate == null) {
            return false;
        }

        return expirationDate.isBefore(LocalDate.now());
    }

    /**
     * Retrieves the threshold quantity for being marked as low stock.
     *
     * @return The low stock threshold.
     */
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    /**
     * Sets a new threshold quantity for determining low stock status.
     *
     * @param threshold The new low stock threshold to set.
     * @throws IllegalArgumentException if the threshold is negative.
     */
    public void setLowStockThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException
            ("Low stock threshold cannot be negative");
        }

        this.lowStockThreshold = threshold;
    }

    /**
     * Checks if the current stock quantity is below the defined low stock threshold.
     *
     * @return True if quantity is less than the lowStockThreshold, false otherwise.
     */
    public boolean isLowStock() {
        return quantity < lowStockThreshold;
    }

    /**
     * Decreases the current stock quantity by a specified amount (e.g., after a sale).
     *
     * @param amount The amount to decrease the quantity by.
     * @throws IllegalArgumentException if the decrease amount exceeds the available quantity.
     */
    public void decreaseQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException
            ("Cannot decrease more than available quantity");
        }

        this.quantity -= amount;
    }

    /**
     * Increases the current stock quantity by a specified amount (e.g., after a restock).
     *
     * @param amount The amount to increase the quantity by.
     */
    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }
}

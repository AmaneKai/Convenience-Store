package com.konbini.dto;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Product.
 * This class is used to serialize or transfer product data between application
 * layers (e.g., to the View, API, or persistence layer) without
 * exposing the full domain model complexity. It includes flags for inventory
 * status (low stock, expired) which are derived properties.
 */
public class ProductDTO {
    /**
     * The unique identifier of the product.
     */
    private String id;
    /**
     * The name of the product.
     */
    private String name;
    /**
     * The selling price of the product.
     */
    private double price;
    /**
     * The current stock quantity.
     */
    private int quantity;
    /**
     * The primary category of the product (e.g., FOOD, BEVERAGE).
     */
    private String category;
    /**
     * The brand name of the product.
     */
    private String brand;
    /**
     * The subcategory or variant of the product (e.g., COLD, SNACK).
     */
    private String variant;
    /**
     * The expiration date of the product, null if not applicable.
     */
    private LocalDate expirationDate;
    /**
     * Derived property: true if the product stock is below the low-stock threshold.
     */
    private boolean lowStock;
    /**
     * Derived property: true if the product's expiration date has passed.
     */
    private boolean expired;

    /**
     * Empty constructor for serialization purposes (e.g., JSON mappers).
     */
    public ProductDTO() {
    }

    /**
     * Constructor that creates a ProductDTO by copying data from the domain Product model.
     *
     * @param product The domain model Product object to convert.
     */
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        // Convert enum categories to String for DTO simplicity
        this.category = product.getCategory();
        this.brand = product.getBrand();
        this.variant = product.getVariant();
        this.expirationDate = product.getExpirationDate();
        this.lowStock = product.isLowStock();
        this.expired = product.isExpired();
    }

    /**
     * Static factory method to convert a domain Product object to a ProductDTO.
     *
     * @param product The domain model Product object.
     * @return A new ProductDTO instance.
     */
    public static ProductDTO fromModel(Product product) {
        return new ProductDTO(product);
    }

    /**
     * Static utility method to convert a list of domain Product objects to a
     * list of ProductDTOs.
     *
     * @param products A List of domain model Product objects.
     * @return A List of ProductDTO instances.
     */
    public static List<ProductDTO> fromModelList(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(fromModel(product));
        }
        return dtos;
    }

    /**
     * Converts the DTO back into a domain Product model.
     * Note: This method does not set the generated ID of the product. It must
     * be done by the service/persistence layer upon saving.
     *
     * @return A new Product domain object.
     */
    public Product toModel() {
        // Since the Product constructor uses String representations of category/variant,
        // we assume the DTO strings match the enum values (or can be mapped).
        // For simplicity, we pass the DTO strings directly.
        Product product = new Product(
                name,
                price,
                quantity,
                category,
                brand,
                variant,
                expirationDate
        );
        return product;
    }

    // Getters and Setters

    /**
     * Gets the unique ID of the product.
     * @return The product ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique ID of the product.
     * @param id The product ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the product name.
     * @return The product name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     * @param name The product name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the product price.
     * @return The product price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     * @param price The product price to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the current stock quantity.
     * @return The stock quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the current stock quantity.
     * @param quantity The stock quantity to set.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the product category (as a String).
     * @return The category.
     */
    public String getCategory() {

        return category;
    }

    /**
     * Sets the product category (as a String).
     * @param category The category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the product brand.
     * @return The brand name.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the product brand.
     * @param brand The brand name to set.
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Gets the product subcategory or variant (as a String).
     * @return The variant/subcategory.
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the product subcategory or variant (as a String).
     * @param variant The variant/subcategory to set.
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Gets the product expiration date.
     * @return The expiration date.
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the product expiration date.
     * @param expirationDate The expiration date to set.
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Checks if the product is marked as low stock.
     * @return True if stock is low, false otherwise.
     */
    public boolean isLowStock() {
        return lowStock;
    }

    /**
     * Sets the low stock status.
     * @param lowStock The low stock status to set.
     */
    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    /**
     * Checks if the product is expired.
     * @return True if the product is expired, false otherwise.
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * Sets the expired status.
     * @param expired The expired status to set.
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * Provides a string representation of the ProductDTO for logging and debugging.
     *
     * @return A string containing key product details.
     */
    @Override
    public String toString() {
        return "ProductDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", variant='" + variant + '\'' +
                ", expirationDate=" + expirationDate +
                ", lowStock=" + lowStock +
                ", expired=" + expired +
                '}';
    }
}

package com.konbini.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.konbini.model.Product;

/**
 * Data Transfer Object (DTO) for representing product information.
 * Used to transfer product data between layers without exposing the domain model.
 * Contains product details, inventory information, and expiration status.
 */
public class ProductDTO {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private String category;
    private String brand;
    private String variant;
    private LocalDate expirationDate;
    private boolean lowStock;
    private boolean expired;

    /**
     * Default constructor for creating an empty ProductDTO.
     */
    public ProductDTO() {}

    /**
     * Constructs a ProductDTO from a Product domain model object.
     * Extracts all product information including inventory status and expiration.
     *
     * @param product the Product domain model to convert to DTO
     */
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.category = product.getCategory();
        this.brand = product.getBrand();
        this.variant = product.getVariant();
        this.expirationDate = product.getExpirationDate();
        this.lowStock = product.isLowStock();
        this.expired = product.isExpired();
    }

    /**
     * Static factory method to create a ProductDTO from a Product domain model.
     *
     * @param product the Product domain model to convert
     * @return a new ProductDTO instance representing the product
     */
    public static ProductDTO fromModel(Product product) {
        return new ProductDTO(product);
    }

    /**
     * Converts a list of Product domain models to a list of ProductDTOs.
     *
     * @param products the list of Product domain models to convert
     * @return a list of ProductDTO objects
     */
    public static List<ProductDTO> fromModelList(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(fromModel(product));
        }
        return dtos;
    }

    /**
     * Converts this DTO back to a Product domain model.
     * Creates a new Product instance with the DTO's data.
     * Note: The generated product will have a new ID if not set in the DTO.
     *
     * @return a new Product domain model with the DTO's data
     */
    public Product toModel() {
        Product product = new Product(
                name,
                price,
                quantity,
                category,
                brand,
                variant,
                expirationDate);
        return product;
    }

    /**
     * Gets the product's unique identifier.
     *
     * @return the product ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the product's unique identifier.
     *
     * @param id the product ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the product's name.
     *
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product's name.
     *
     * @param name the product name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the product's price.
     *
     * @return the product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product's price.
     *
     * @param price the product price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the current stock quantity of the product.
     *
     * @return the product quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the stock quantity of the product.
     *
     * @param quantity the product quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the product's category.
     *
     * @return the product category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the product's category.
     *
     * @param category the product category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the product's brand.
     *
     * @return the product brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the product's brand.
     *
     * @param brand the product brand to set
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Gets the product's variant or subcategory.
     *
     * @return the product variant
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Sets the product's variant or subcategory.
     *
     * @param variant the product variant to set
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }

    /**
     * Gets the product's expiration date.
     *
     * @return the expiration date, or null if the product doesn't expire
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the product's expiration date.
     *
     * @param expirationDate the expiration date to set
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Checks if the product is in low stock.
     * Typically indicates quantity below a certain threshold.
     *
     * @return true if the product is in low stock, false otherwise
     */
    public boolean isLowStock() {
        return lowStock;
    }

    /**
     * Sets whether the product is in low stock.
     *
     * @param lowStock true if low stock, false otherwise
     */
    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    /**
     * Checks if the product has expired.
     * Based on comparison of expiration date with current date.
     *
     * @return true if the product has expired, false otherwise
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * Sets whether the product has expired.
     *
     * @param expired true if expired, false otherwise
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * Returns a string representation of the ProductDTO.
     *
     * @return a string containing all product information
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
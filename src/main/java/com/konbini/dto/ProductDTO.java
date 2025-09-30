package com.konbini.dto;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Product
 * Used to transfer data between layers without exposing the domain model
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
    
    // Empty constructor for serialization
    public ProductDTO() {
    }
    
    // Constructor from domain model
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
    
    // Convert domain model to DTO
    public static ProductDTO fromModel(Product product) {
        return new ProductDTO(product);
    }
    
    // Convert list of domain models to list of DTOs
    public static List<ProductDTO> fromModelList(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(fromModel(product));
        }
        return dtos;
    }
    
    // Convert DTO to domain model
    public Product toModel() {
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
    
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

package com.konbini.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.konbini.model.Product;

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

    public ProductDTO() {}

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

    public static ProductDTO fromModel(Product product) {
        return new ProductDTO(product);
    }

    public static List<ProductDTO> fromModelList(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products) {
            dtos.add(fromModel(product));
        }
        return dtos;
    }

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

    public String getCategory() {return category; }

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
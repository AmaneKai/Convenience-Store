package com.konbini.dto;

import com.konbini.model.CartItem;
import com.konbini.model.Product;

public class TransactionItemDTO {
    private String productId;
    private String productName;
    private double productPrice;
    private String productCategory;
    private String productBrand;
    private String productVariant;
    private int quantity;
    private double subtotal;

    public TransactionItemDTO() {
    }

    public TransactionItemDTO(CartItem item) {
        Product product = item.getProduct();

        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.productCategory = product.getCategory();
        this.productBrand = product.getBrand();
        this.productVariant = product.getVariant();
        this.quantity = item.getQuantity();
        this.subtotal = item.getSubtotal();
    }

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public String getProductCategory() {
        return productCategory;
    }
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    public String getProductBrand() {
        return productBrand;
    }
    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }
    public String getProductVariant() {
        return productVariant;
    }
    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    public double getUnitPrice() {
        return productPrice;
    }
    public double getTotalPrice() {
        return subtotal;
    }
    @Override
    public String toString() {
        return "TransactionItemDTO{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}

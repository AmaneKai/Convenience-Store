package com.konbini.dto;

import com.konbini.model.CartItem;
import com.konbini.model.Product;

/**
 * Data Transfer Object for CartItem in a Transaction.
 * This DTO represents a single line item of a transaction. It flattens all
 * necessary immutable product details (ID, name, price, attributes) along with
 * the transaction-specific quantity and subtotal from the CartItem.
 */
public class TransactionItemDTO {
    /**
     * The unique identifier of the purchased product.
     */
    private String productId;
    /**
     * The name of the purchased product.
     */
    private String productName;
    /**
     * The unit price of the product at the time of transaction.
     */
    private double productPrice;
    /**
     * The category of the product.
     */
    private String productCategory;
    /**
     * The brand of the product.
     */
    private String productBrand;
    /**
     * The variant or subcategory of the product.
     */
    private String productVariant;
    /**
     * The quantity of the product purchased.
     */
    private int quantity;
    /**
     * The total price for this line item (unit price * quantity).
     */
    private double subtotal;

    /**
     * Empty constructor for serialization purposes (e.g., JSON mappers).
     */
    public TransactionItemDTO() {
    }

    /**
     * Constructor that creates a TransactionItemDTO by copying data from the domain CartItem model.
     * It extracts product details and line item quantities/totals.
     *
     * @param item The domain model CartItem object to convert.
     */
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

    // Getters and Setters

    /**
     * Gets the product ID.
     * @return The unique product ID.
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     * @param productId The product ID to set.
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Gets the product name.
     * @return The name of the product.
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name.
     * @param productName The product name to set.
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the unit price of the product.
     * @return The unit price.
     */
    public double getProductPrice() {
        return productPrice;
    }

    /**
     * Sets the unit price of the product.
     * @param productPrice The unit price to set.
     */
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * Gets the product category.
     * @return The product category.
     */
    public String getProductCategory() {
        return productCategory;
    }

    /**
     * Sets the product category.
     * @param productCategory The product category to set.
     */
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    /**
     * Gets the product brand.
     * @return The product brand.
     */
    public String getProductBrand() {
        return productBrand;
    }

    /**
     * Sets the product brand.
     * @param productBrand The product brand to set.
     */
    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    /**
     * Gets the product variant or subcategory.
     * @return The product variant.
     */
    public String getProductVariant() {
        return productVariant;
    }

    /**
     * Sets the product variant or subcategory.
     * @param productVariant The product variant to set.
     */
    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }

    /**
     * Gets the quantity purchased.
     * @return The quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity purchased.
     * @param quantity The quantity to set.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the line item subtotal (price * quantity).
     * @return The line item subtotal.

     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the line item subtotal.
     * @param subtotal The subtotal to set.
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getUnitPrice() {
        return productPrice;
    }
    public double getTotalPrice() {
        return subtotal;
    }

    /**
     * Provides a string representation of the TransactionItemDTO for logging and debugging.
     *
     * @return A string containing key item details.
     */
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

package com.konbini.dto;

import com.konbini.model.CartItem;
import com.konbini.model.Product;

/**
 * Data Transfer Object (DTO) for representing individual items within a transaction.
 * Used to transfer item-level transaction data between layers without exposing the domain model.
 * Contains product details, quantity, and pricing information for each transaction item.
 */
public class TransactionItemDTO {
    private String productId;
    private String productName;
    private double productPrice;
    private String productCategory;
    private String productBrand;
    private String productVariant;
    private int quantity;
    private double subtotal;

    /**
     * Default constructor for creating an empty TransactionItemDTO.
     */
    public TransactionItemDTO() {
    }

    /**
     * Constructs a TransactionItemDTO from a CartItem domain model object.
     * Extracts product information, quantity, and calculated subtotal.
     *
     * @param item the CartItem domain model to convert to DTO
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

    /**
     * Gets the unique identifier of the product.
     *
     * @return the product ID
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the unique identifier of the product.
     *
     * @param productId the product ID to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Gets the name of the product.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the name of the product.
     *
     * @param productName the product name to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the unit price of the product at the time of transaction.
     *
     * @return the product unit price
     */
    public double getProductPrice() {
        return productPrice;
    }

    /**
     * Sets the unit price of the product.
     *
     * @param productPrice the product unit price to set
     */
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * Gets the category of the product.
     *
     * @return the product category
     */
    public String getProductCategory() {
        return productCategory;
    }

    /**
     * Sets the category of the product.
     *
     * @param productCategory the product category to set
     */
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    /**
     * Gets the brand of the product.
     *
     * @return the product brand
     */
    public String getProductBrand() {
        return productBrand;
    }

    /**
     * Sets the brand of the product.
     *
     * @param productBrand the product brand to set
     */
    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    /**
     * Gets the variant or subcategory of the product.
     *
     * @return the product variant
     */
    public String getProductVariant() {
        return productVariant;
    }

    /**
     * Sets the variant or subcategory of the product.
     *
     * @param productVariant the product variant to set
     */
    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }

    /**
     * Gets the quantity of the product purchased in this transaction.
     *
     * @return the quantity purchased
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product purchased.
     *
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the subtotal for this item (unit price × quantity).
     *
     * @return the item subtotal
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal for this item.
     *
     * @param subtotal the item subtotal to set
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Gets the unit price of the product (alias for getProductPrice).
     *
     * @return the unit price
     */
    public double getUnitPrice() {
        return productPrice;
    }

    /**
     * Gets the total price for this item (alias for getSubtotal).
     *
     * @return the total price
     */
    public double getTotalPrice() {
        return subtotal;
    }

    /**
     * Returns a string representation of the TransactionItemDTO.
     *
     * @return a string containing item summary information
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
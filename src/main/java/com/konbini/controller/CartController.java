package com.konbini.controller;

import com.konbini.model.*;
import com.konbini.service.*;

import java.util.Optional;

/**
 * Controller for managing shopping cart operations including adding, removing,
 * and updating items in the cart.
 */
public class CartController {
    private final ProductService productService;

    /**
     * Constructs a CartController with the specified product service.
     *
     * @param productService the service for product lookup operations
     */
    public CartController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a new shopping cart for the specified customer.
     *
     * @param customer the customer who owns the cart
     * @return a new Cart instance for the customer
     */
    public Cart createCart(Customer customer) {
        return new Cart(customer);
    }

    /**
     * Adds a product to the cart with the specified quantity.
     *
     * @param cart the cart to add the product to
     * @param productId the ID of the product to add
     * @param quantity the quantity to add
     * @throws IllegalArgumentException if the product is not found
     */
    public void addToCart(Cart cart, String productId, int quantity) {
        Optional<Product> optionalProduct = productService.findById(productId);

        if (!optionalProduct.isPresent()) {
            throw new IllegalArgumentException
            ("Product not found: " + productId);
        }

        Product product = optionalProduct.get();
        cart.addItem(product, quantity);
    }

    /**
     * Removes a product from the cart.
     *
     * @param cart the cart to remove the product from
     * @param productId the ID of the product to remove
     */
    public void removeFromCart(Cart cart, String productId) {
        cart.removeItem(productId);
    }

    /**
     * Updates the quantity of a product in the cart.
     *
     * @param cart the cart containing the product
     * @param productId the ID of the product to update
     * @param newQuantity the new quantity for the product
     */
    public void updateCartItemQuantity(Cart cart,
        String productId, int newQuantity) {
        cart.updateItemQuantity(productId, newQuantity);
    }

    /**
     * Removes all items from the cart.
     *
     * @param cart the cart to clear
     */
    public void clearCart(Cart cart) {
        cart.clear();
    }
}
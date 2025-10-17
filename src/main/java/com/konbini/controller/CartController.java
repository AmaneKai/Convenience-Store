package com.konbini.controller;

import com.konbini.model.Cart;
import com.konbini.model.Customer;
import com.konbini.model.Product;
import com.konbini.service.ProductService;

import java.util.Optional;

/**
 * Controller class responsible for managing all shopping cart operations.
 * It acts as an intermediary, handling requests to add, remove, and update
 * items in a Cart, utilizing the ProductService for product validation and lookup.
 */
public class CartController {
    /**
     * The service dependency used to retrieve and validate Product data.
     */
    private final ProductService productService;

    /**
     * Constructs a new CartController, injecting the required product service.
     *
     * @param productService The service providing product look-up capabilities.
     */
    public CartController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a new, empty shopping cart associated with a specific customer.
     *
     * @param customer The Customer who will own the new cart.
     * @return A newly initialized Cart object.
     */
    public Cart createCart(Customer customer) {
        return new Cart(customer);
    }

    /**
     * Adds a specified quantity of a product to the cart. It first validates
     * the product's existence using the ProductService.
     *
     * @param cart The Cart to modify.
     * @param productId The unique identifier of the product to add.
     * @param quantity The amount of the product to add.
     * @throws IllegalArgumentException if no product is found for the given ID.
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
     * Removes all items corresponding to the given product ID from the cart.
     *
     * @param cart The Cart to modify.
     * @param productId The unique identifier of the product to remove.
     */
    public void removeFromCart(Cart cart, String productId) {
        cart.removeItem(productId);
    }

    /**
     * Updates the quantity of an existing item in the cart.
     *
     * @param cart The Cart to modify.
     * @param productId The unique identifier of the product whose quantity should be updated.
     * @param newQuantity The new, total quantity for the item.
     */
    public void updateCartItemQuantity(Cart cart,
        String productId, int newQuantity) {
        cart.updateItemQuantity(productId, newQuantity);
    }

    /**
     * Empties the cart by removing all contained items.
     *
     * @param cart The Cart to clear.
     */
    public void clearCart(Cart cart) {
        cart.clear();
    }
}

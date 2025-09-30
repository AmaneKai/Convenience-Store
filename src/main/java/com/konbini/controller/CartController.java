package com.konbini.controller;

import com.konbini.model.Cart;
import com.konbini.model.Customer;
import com.konbini.model.Product;
import com.konbini.service.ProductService;

import java.util.Optional;

public class CartController {
    private final ProductService productService;
    
    public CartController(ProductService productService) {
        this.productService = productService;
    }
    
    public Cart createCart(Customer customer) {
        return new Cart(customer);
    }
    
    public void addToCart(Cart cart, String productId, int quantity) {
        Optional<Product> optionalProduct = productService.findById(productId);
        
        if (!optionalProduct.isPresent()) {
            throw new IllegalArgumentException
            ("Product not found: " + productId);
        }
        
        Product product = optionalProduct.get();
        cart.addItem(product, quantity);
    }
    
    public void removeFromCart(Cart cart, String productId) {
        cart.removeItem(productId);
    }
    
    public void updateCartItemQuantity(Cart cart, 
        String productId, int newQuantity) {
        cart.updateItemQuantity(productId, newQuantity);
    }
    
    public void clearCart(Cart cart) {
        cart.clear();
    }
}

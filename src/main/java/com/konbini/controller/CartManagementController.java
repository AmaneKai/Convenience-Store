package com.konbini.controller;

import java.util.Optional;

import com.konbini.model.Cart;
import com.konbini.model.Customer;
import com.konbini.model.Transaction;
import com.konbini.util.FileUtil;
import com.konbini.view.StoreView;

/**
 * Cart Management - GUI Event-Driven.
 * Each method is a single action called directly by GUI buttons.
 */
public class CartManagementController {
    private final StoreView view;
    private final ProductController productController;
    private final CustomerController customerController;
    private final CartController cartController;
    private final TransactionController transactionController;
    
    private Cart currentCart;
    
    public CartManagementController(
            StoreView view,
            ProductController productController,
            CustomerController customerController,
            CartController cartController,
            TransactionController transactionController) {
        this.view = view;
        this.productController = productController;
        this.customerController = customerController;
        this.cartController = cartController;
        this.transactionController = transactionController;
    }
    
    public void handleCreateCart() {
        try {
            view.displayCustomers(customerController.getAllCustomers());
            String customerId = view.getStringInput("Enter customer ID: ");
            if (customerId == null || customerId.trim().isEmpty()) return;
            
            Optional<Customer> customer = customerController.getCustomerById(customerId);
            if (customer.isPresent()) {
                currentCart = cartController.createCart(customer.get());
                view.displaySuccessMessage("Cart created for " + customer.get().getName());
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to create cart: " + e.getMessage());
        }
    }
    
    public void handleViewCart() {
        try {
            if (currentCart == null) {
                view.displayErrorMessage("No active cart. Create one first.");
                return;
            }
            view.displayCart(currentCart);
        } catch (Exception e) {
            view.displayErrorMessage("Error: " + e.getMessage());
        }
    }
    
    public void handleAddItem() {
        try {
            if (currentCart == null) {
                view.displayErrorMessage("No active cart. Create one first.");
                return;
            }
            
            view.displayProducts(productController.getAllProducts());
            String productId = view.getStringInput("Enter product ID: ");
            if (productId == null || productId.trim().isEmpty()) return;
            
            int quantity = view.getIntInput("Enter quantity: ");
            if (quantity <= 0) return;
            
            cartController.addToCart(currentCart, productId, quantity);
            view.displaySuccessMessage("Item added.");
            view.displayCart(currentCart);
        } catch (Exception e) {
            view.displayErrorMessage("Failed to add item: " + e.getMessage());
        }
    }
    
    public void handleRemoveItem() {
        try {
            if (currentCart == null || currentCart.isEmpty()) {
                view.displayErrorMessage("Cart is empty.");
                return;
            }
            
            view.displayCart(currentCart);
            String productId = view.getStringInput("Enter product ID to remove: ");
            if (productId == null || productId.trim().isEmpty()) return;
            
            cartController.removeFromCart(currentCart, productId);
            view.displaySuccessMessage("Item removed.");
            view.displayCart(currentCart);
        } catch (Exception e) {
            view.displayErrorMessage("Failed to remove: " + e.getMessage());
        }
    }
    
    public void handleUpdateQuantity() {
        try {
            if (currentCart == null || currentCart.isEmpty()) {
                view.displayErrorMessage("Cart is empty.");
                return;
            }
            
            view.displayCart(currentCart);
            String productId = view.getStringInput("Enter product ID: ");
            if (productId == null || productId.trim().isEmpty()) return;
            
            int newQuantity = view.getIntInput("Enter new quantity: ");
            if (newQuantity <= 0) return;
            
            cartController.updateCartItemQuantity(currentCart, productId, newQuantity);
            view.displaySuccessMessage("Quantity updated.");
            view.displayCart(currentCart);
        } catch (Exception e) {
            view.displayErrorMessage("Failed to update: " + e.getMessage());
        }
    }
    
    public void handleClearCart() {
        try {
            if (currentCart == null) {
                view.displayErrorMessage("No cart to clear.");
                return;
            }
            
            cartController.clearCart(currentCart);
            view.displaySuccessMessage("Cart cleared.");
        } catch (Exception e) {
            view.displayErrorMessage("Error: " + e.getMessage());
        }
    }
    
    public void handleCheckout() {
        try {
            if (currentCart == null || currentCart.isEmpty()) {
                view.displayErrorMessage("Cart is empty.");
                return;
            }
            
            view.displayCart(currentCart);
            if (!view.getBooleanInput("Proceed with checkout?")) {
                return;
            }
            
            Customer customer = currentCart.getCustomer();
            int pointsToRedeem = 0;
            
            if (customer.hasMembershipCard() && customer.getMembershipCard().getPoints() > 0) {
                int available = customer.getMembershipCard().getPoints();
                view.displayInfoMessage("Available points: " + available);
                pointsToRedeem = view.getIntInput("Points to redeem (0 for none): ");
                if (pointsToRedeem < 0 || pointsToRedeem > available) {
                    pointsToRedeem = 0;
                }
            }
            
            double total = currentCart.getSubtotal() * 1.12; // VAT
            if (customer.isSeniorCitizen()) {
                total *= 0.8; // 20% senior discount
            }
            total -= pointsToRedeem;
            
            double payment = view.getDoubleInput("Enter payment amount: ");
            if (payment < total) {
                view.displayErrorMessage("Insufficient payment.");
                return;
            }
            
            Transaction transaction = transactionController.processTransaction(currentCart, payment, pointsToRedeem);
            String receipt = transactionController.generateReceipt(transaction);
            view.displayReceipt(receipt);
            
            String receiptPath = FileUtil.ensureReceiptsDirectory() + "/receipt_" + transaction.getId() + ".txt";
            transactionController.saveReceiptToFile(transaction, receiptPath);
            view.displaySuccessMessage("Receipt saved. Transaction complete!");
            
            currentCart = null;
        } catch (Exception e) {
            view.displayErrorMessage("Checkout failed: " + e.getMessage());
        }
    }
}
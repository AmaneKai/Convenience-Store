package com.konbini.controller;

import com.konbini.model.Cart;
import com.konbini.model.Customer;
import com.konbini.model.Transaction;
import com.konbini.util.FileUtil;
import com.konbini.view.StoreView;
import java.util.Optional;

public class CartManagementController {
    private static final double VAT_RATE = 0.12;
    private static final double SENIOR_DISCOUNT_RATE = 0.20;
    private static final int POINTS_TO_PESO_RATIO = 1;
    private static final String RECEIPT_FILENAME_PREFIX = "receipt_";
    private static final String RECEIPT_FILE_EXTENSION = ".txt";
    
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
    
    public void handleCartManagement() {
        boolean backToMain = false;
        
        while (!backToMain) {
            view.displayCartMenu();
            int choice = view.getCartMenuChoice();
            
            switch (choice) {
                case 1: createCart(); break;
                case 2: viewCart(); break;
                case 3: addItemToCart(); break;
                case 4: removeItemFromCart(); break;
                case 5: updateCartItemQuantity(); break;
                case 6: clearCart(); break;
                case 7: checkout(); break;
                case 0: backToMain = true; break;
                default: view.displayErrorMessage
                    ("Invalid choice. Please try again.");
            }
        }
    }
    
    private void createCart() {
        try {
            view.displayCustomers(customerController.getAllCustomers());
            String customerId = view.getStringInput
                ("Enter customer ID for the cart: ");
            Optional<Customer> optionalCustomer = customerController
                .getCustomerById(customerId);
            
            if (optionalCustomer.isPresent()) {
                currentCart = cartController
                    .createCart(optionalCustomer.get());
                view.displaySuccessMessage("Cart created successfully.");
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to create cart: "  
                + e.getMessage());
        }
    }
    
    private void viewCart() {
        if (currentCart != null) {
            view.displayCart(currentCart);
        } else {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
        }
    }
    
    private void addItemToCart() {
        if (currentCart == null) {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
            return;
        }
        
        try {
            view.displayProducts(productController.getAllProducts());
            String productId = view.getStringInput
                ("Enter product ID to add to cart: ");
            int quantity = view.getIntInput("Enter quantity: ");
            
            cartController.addToCart(currentCart, productId, quantity);
            view.displaySuccessMessage("Item added to cart successfully.");
            view.displayCart(currentCart);
        } catch (Exception e) {
            view.displayErrorMessage("Failed to add item to cart: " 
                + e.getMessage());
        }
    }
    
    private void removeItemFromCart() {
        if (currentCart == null) {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
            return;
        }
        
        if (currentCart.isEmpty()) {
            view.displayErrorMessage("Cart is empty.");
            return;
        }
        
        try {
            view.displayCart(currentCart);
            String productId = view.getStringInput
                ("Enter product ID to remove from cart: ");
            
            cartController.removeFromCart(currentCart, productId);
            view.displaySuccessMessage("Item removed from cart successfully.");
            view.displayCart(currentCart);
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to remove item from cart: " + e.getMessage());
        }
    }
    
    private void updateCartItemQuantity() {
        if (currentCart == null) {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
            return;
        }
        
        if (currentCart.isEmpty()) {
            view.displayErrorMessage("Cart is empty.");
            return;
        }
        
        try {
            view.displayCart(currentCart);
            String productId = view.getStringInput
                ("Enter product ID to update quantity: ");
            int newQuantity = view.getIntInput("Enter new quantity: ");
            
            cartController.updateCartItemQuantity
                (currentCart, productId, newQuantity);
            view.displaySuccessMessage("Item quantity updated successfully.");
            view.displayCart(currentCart);
        } catch (Exception e) {
            view.displayErrorMessage("Failed to update item quantity: " 
                + e.getMessage());
        }
    }
    
    private void clearCart() {
        if (currentCart != null) {
            cartController.clearCart(currentCart);
            view.displaySuccessMessage("Cart cleared successfully.");
        } else {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
        }
    }
    
    private void checkout() {
        if (currentCart == null) {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
            return;
        }
        
        if (currentCart.isEmpty()) {
            view.displayErrorMessage
                ("Cart is empty. Cannot proceed with checkout.");
            return;
        }
        
        try {
            view.displayCart(currentCart);
            
            if (!view.getBooleanInput("Proceed with checkout?")) {
                view.displayInfoMessage("Checkout cancelled.");
                return;
            }
            
            Customer customer = currentCart.getCustomer();
            int pointsToRedeem = getPointsToRedeem(customer);
            
            double totalAmount = calculateTotalAmount
                (customer, pointsToRedeem);
            view.displayInfoMessage
                ("Total amount due: ₱" + String.format("%.2f", totalAmount));
            
            double paymentAmount = view
                .getDoubleInput("Enter payment amount: ");
            
            if (paymentAmount < totalAmount) {
                view.displayErrorMessage("Payment amount is insufficient.");
                return;
            }
            
            Transaction transaction = transactionController.processTransaction
                (currentCart, paymentAmount, pointsToRedeem);
            
            String receipt = transactionController
                .generateReceipt(transaction);
            view.displayReceipt(receipt);
            
            String receiptFilePath = buildReceiptFilePath(transaction.getId());
            transactionController.saveReceiptToFile
                (transaction, receiptFilePath);
            view.displaySuccessMessage("Receipt saved to " + receiptFilePath);
            
            currentCart = null;
        } catch (Exception e) {
            view.displayErrorMessage("Checkout failed: " + e.getMessage());
        }
    }
    
    private int getPointsToRedeem(Customer customer) {
        if (!customer.hasMembershipCard() || 
            customer.getMembershipCard().getPoints() <= 0) {
            return 0;
        }
        
        int availablePoints = customer.getMembershipCard().getPoints();
        view.displayInfoMessage("Customer has " + availablePoints 
            + " points available for redemption.");
        
        int pointsToRedeem = view.getIntInput
            ("Enter points to redeem (0 for none): ");
        
        if (pointsToRedeem > availablePoints) {
            view.displayErrorMessage
                ("Cannot redeem more points than available.");
            return view.getIntInput
                ("Enter points to redeem (0-" + availablePoints + "): ");
        }
        
        return pointsToRedeem;
    }
    
    private double calculateTotalAmount(Customer customer, 
            int pointsToRedeem) {
        double subtotal = currentCart.getSubtotal();
        double totalWithTax = subtotal * (1 + VAT_RATE);
        
        if (customer.isSeniorCitizen()) {
            totalWithTax *= (1 - SENIOR_DISCOUNT_RATE);
        }
        
        return totalWithTax - (pointsToRedeem * POINTS_TO_PESO_RATIO);
    }
    
    private String buildReceiptFilePath(String transactionId) {
        String receiptsDir = FileUtil.ensureReceiptsDirectory();
        return receiptsDir + "/" + RECEIPT_FILENAME_PREFIX 
            + transactionId + RECEIPT_FILE_EXTENSION;
    }
}

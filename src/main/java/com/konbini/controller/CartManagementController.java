package com.konbini.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.dto.*;
import com.konbini.model.*;
import com.konbini.service.*;
import com.konbini.util.FileUtil;
import com.konbini.view.StoreView;

public class CartManagementController {
    private final StoreView view;
    private final ProductController productController;
    private final CustomerController customerController;
    private final CartController cartController;
    private final TransactionController transactionController;
    private final CartService cartService;

    private Cart currentCart;

    public CartManagementController(
            StoreView view,
            ProductController productController,
            CustomerController customerController,
            CartController cartController,
            TransactionController transactionController,
            CartService cartService) {
        if (view == null || productController == null || customerController == null ||
                cartController == null || transactionController == null || cartService == null) {
            throw new IllegalArgumentException("All dependencies must be provided");
        }
        this.view = view;
        this.productController = productController;
        this.customerController = customerController;
        this.cartController = cartController;
        this.transactionController = transactionController;
        this.cartService = cartService;
    }

    // ==================== PUBLIC HANDLERS ====================

    public void handleCreateCart() {
        try {
            List<CustomerDTO> customers = customerController.getAllCustomers().stream()
                    .map(CustomerDTO::fromModel).collect(Collectors.toList());

            view.displayCustomers(customers);
            String customerId = view.getStringInput("Enter customer ID: ");

            if (customerId != null && !customerId.trim().isEmpty()) {
                createCartForCustomer(customerId);
            }

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "creating cart");
        } catch (Exception e) {
            handleGenericException(e, "creating cart", "Failed to create cart. Please try again.");
        }
    }

    public void handleViewCart() {
        try {
            if (ensureCartExists()) {
                view.displayCart(CartDTO.fromModel(currentCart));
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing cart");
        } catch (Exception e) {
            handleGenericException(e, "viewing cart", "Error displaying cart. Please try again.");
        }
    }

    public void handleAddItem() {
        try {
            if (ensureCartExists()) {
                view.displayProducts(ProductDTO.fromModelList(productController.getAllProducts()));
                String productId = view.getStringInput("Enter product ID: ");

                if (productId != null && !productId.trim().isEmpty()) {
                    addItemToCart(productId);
                }
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "adding item");
        } catch (Exception e) {
            handleGenericException(e, "adding item to cart", "Failed to add item. Please try again.");
        }
    }

    public void handleRemoveItem() {
        try {
            if (ensureCartNotEmpty()) {
                view.displayCart(CartDTO.fromModel(currentCart));
                String productId = view.getStringInput("Enter product ID to remove: ");

                if (productId != null && !productId.trim().isEmpty()) {
                    removeItemFromCart(productId);
                }
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "removing item");
        } catch (Exception e) {
            handleGenericException(e, "removing item from cart", "Failed to remove item. Please try again.");
        }
    }

    public void handleUpdateQuantity() {
        try {
            if (ensureCartNotEmpty()) {
                view.displayCart(CartDTO.fromModel(currentCart));
                String productId = view.getStringInput("Enter product ID: ");

                if (productId != null && !productId.trim().isEmpty()) {
                    updateItemQuantity(productId);
                }
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "updating quantity");
        } catch (Exception e) {
            handleGenericException(e, "updating quantity", "Failed to update quantity. Please try again.");
        }
    }

    public void handleClearCart() {
        try {
            if (ensureCartExists()) {
                cartController.clearCart(currentCart);
                view.displaySuccessMessage("Cart cleared.");
            }
        } catch (Exception e) {
            handleGenericException(e, "clearing cart", "Failed to clear cart. Please try again.");
        }
    }

    public boolean handleCheckout() {
        try {
            if (ensureCartNotEmpty()) {
                processCheckout();
                return currentCart == null;
            }

            return false;
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "during checkout");
            return false;
        } catch (Exception e) {
            System.err.println("Fatal error during checkout: " + e.getMessage());
            view.displayErrorMessage("Critical error during checkout. Transaction aborted.");
            return false;
        }
    }

    public void handleAddItem(ProductDTO product, int quantity) {
        try {
            if (ensureCartExists()) {
                addProductToCart(product, quantity);
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "adding item");
        } catch (Exception e) {
            handleGenericException(e, "adding item to cart", "Failed to add item. Please try again.");
        }
    }

    public void loadAvailableProducts() {
        try {
            List<Product> allProducts = productController.getAllProducts();

            if (allProducts != null && !allProducts.isEmpty()) {
                displayProductList(allProducts);
            } else {
                view.displayProducts(null);
            }
        } catch (Exception e) {
            handleGenericException(e, "loading products", "Failed to load products. Please try again.");
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void createCartForCustomer(String customerId) {
        Optional<Customer> customer = customerController.getCustomerById(customerId);

        if (customer.isPresent()) {
            currentCart = cartController.createCart(customer.get());
            view.displaySuccessMessage("Cart created for " + customer.get().getName());
        } else {
            view.displayErrorMessage("Customer not found.");
        }
    }

    private void addItemToCart(String productId) {
        int quantity = view.getIntInput("Enter quantity: ");

        if (quantity > 0) {
            cartController.addToCart(currentCart, productId, quantity);
            view.displaySuccessMessage("Item added.");
            view.displayCart(CartDTO.fromModel(currentCart));
        }
    }

    private void removeItemFromCart(String productId) {
        cartController.removeFromCart(currentCart, productId);
        view.displaySuccessMessage("Item removed.");
        view.displayCart(CartDTO.fromModel(currentCart));
    }

    private void updateItemQuantity(String productId) {
        int newQuantity = view.getIntInput("Enter new quantity: ");

        if (newQuantity > 0) {
            cartController.updateCartItemQuantity(currentCart, productId, newQuantity);
            view.displaySuccessMessage("Quantity updated.");
            view.displayCart(CartDTO.fromModel(currentCart));
        }
    }

    private void processCheckout() {
        boolean inventoryValid = true;

        try {
            cartService.validateInventoryAvailable(currentCart);
        } catch (IllegalArgumentException e) {
            System.err.println("Stock validation failed: " + e.getMessage());
            view.displayErrorMessage("Stock validation failed: " + e.getMessage());
            inventoryValid = false;
        }

        if (inventoryValid) {
            view.displayCart(CartDTO.fromModel(currentCart));

            if (view.getBooleanInput("Proceed with checkout?")) {
                completeCheckout();
            }
        }
    }

    private void completeCheckout() {
        Customer customer = currentCart.getCustomer();
        boolean pointsValid = true;
        int pointsToRedeem = 0;

        try {
            pointsToRedeem = handlePointsRedemption(customer);
        } catch (IllegalArgumentException e) {
            System.err.println("Points error: " + e.getMessage());
            view.displayErrorMessage("Points error: " + e.getMessage());
            pointsValid = false;
        }

        if (pointsValid) {
            processPaymentAndFinalize(customer, pointsToRedeem);
        }
    }

    private void processPaymentAndFinalize(Customer customer, int pointsToRedeem) {
        try {
            double total = cartService.calculateTotal(currentCart, customer);
            double payment = view.getDoubleInput("Enter payment amount: ");

            if (payment >= total) {
                finalizeTransaction(payment, pointsToRedeem);
            } else {
                view.displayErrorMessage("Insufficient payment.");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Calculation error: " + e.getMessage());
            view.displayErrorMessage("Calculation error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during checkout: " + e.getMessage());
            view.displayErrorMessage("Checkout failed. Please try again.");
        }
    }

    private void finalizeTransaction(double payment, int pointsToRedeem) {
        Transaction transaction = transactionController.processTransaction(currentCart, payment, pointsToRedeem);
        String receipt = transactionController.generateReceipt(transaction);
        view.displayReceipt(receipt);

        saveReceiptFile(transaction);
        refreshProductDisplay();

        currentCart = null;
    }

    private void saveReceiptFile(Transaction transaction) {
        try {
            String receiptPath = FileUtil.ensureReceiptsDirectory() + "/receipt_" + transaction.getId() + ".txt";
            transactionController.saveReceiptToFile(transaction, receiptPath);
            view.displaySuccessMessage("Receipt saved. Transaction complete!");
        } catch (Exception receiptError) {
            System.err.println("Warning: Failed to save receipt file: " + receiptError.getMessage());
            view.displaySuccessMessage("Transaction complete! (Receipt file save failed)");
        }
    }

    private void refreshProductDisplay() {
        List<ProductDTO> updatedProducts = productController.getAllProducts().stream()
                .map(ProductDTO::fromModel).collect(Collectors.toList());
        view.displayProducts(updatedProducts);
    }

    private void addProductToCart(ProductDTO product, int quantity) {
        String errorMessage = validateProductAndQuantity(product, quantity);

        if (errorMessage == null) {
            Optional<Product> optionalProduct = productController.getProductById(product.getId());

            if (optionalProduct.isPresent()) {
                addValidatedProduct(optionalProduct.get(), quantity);
            } else {
                view.displayErrorMessage("Product not found in inventory.");
            }
        } else {
            view.displayErrorMessage(errorMessage);
        }
    }

    private String validateProductAndQuantity(ProductDTO product, int quantity) {
        String temp;

        if (product == null) {
            temp = "Invalid product selected.";
        }
        else if (quantity <= 0) {
            temp = "Quantity must be greater than 0.";
        }
        else {
           temp = null;
        }

        return temp;
    }

    private void addValidatedProduct(Product product, int quantity) {
        cartController.addToCart(currentCart, product.getId(), quantity);
        view.displaySuccessMessage("Item added to cart!");
        view.displayCart(CartDTO.fromModel(currentCart));
    }

    private void displayProductList(List<Product> products) {
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromModel).collect(Collectors.toList());
        view.displayProducts(productDTOs);
    }

    // ==================== VALIDATION & UTILITY METHODS ====================

    private int handlePointsRedemption(Customer customer) {
        int temp = 0;

        if (customer.hasMembershipCard() && customer.getMembershipCard().getPoints() > 0) {
            MembershipCard card = customer.getMembershipCard();
            int availablePoints = card.getPoints();

            view.displayInfoMessage("Available points: " + availablePoints + "\n(1 point = ₱1 discount)");

            if (view.getBooleanInput("Do you want to redeem points for discount?")) {
                int pointsToRedeem = view.getIntInput("Enter points to redeem (0 to skip, max " + availablePoints + "): ");

                if (pointsToRedeem >= 0 && pointsToRedeem <= availablePoints) {
                    temp = pointsToRedeem;
                } else {
                    throw new IllegalArgumentException("Invalid points amount: " + pointsToRedeem);
                }
            }
        }
        return temp;
    }

    private boolean ensureCartExists() {
        boolean temp;

        if (currentCart == null) {
            view.displayErrorMessage("No active cart. Create one first.");
            temp = false;
        } else {
            temp = true;
        }

        return temp;
    }

    private boolean ensureCartNotEmpty() {
        boolean temp;

        if (currentCart == null || currentCart.isEmpty()) {
            view.displayErrorMessage("Cart is empty.");
            temp = false;
        } else {
            temp = true;
        }

        return temp;
    }

    // ==================== ERROR HANDLING HELPERS ====================

    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Unknown error"));
    }

    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}
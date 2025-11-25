package com.konbini.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.dto.*;
import com.konbini.model.*;
import com.konbini.service.*;
import com.konbini.util.FileUtil;
import com.konbini.view.swing.*;

/**
 * Controller for managing shopping cart operations including cart creation, item management,
 * and checkout process. Coordinates between view, cart, product, and transaction controllers.
 */
public class CartManagementController {
    private final SwingStoreView view;
    private final ProductController productController;
    private final CustomerController customerController;
    private final CartController cartController;
    private final TransactionController transactionController;
    private final CartService cartService;

    private Cart currentCart;

    /**
     * Constructs a CartManagementController with all required dependencies.
     *
     * @param view the store view for user interface interactions
     * @param productController controller for product operations
     * @param customerController controller for customer operations
     * @param cartController controller for cart operations
     * @param transactionController controller for transaction operations
     * @param cartService service for cart calculations and validations
     * @throws IllegalArgumentException if any dependency is null
     */
    public CartManagementController(
            SwingStoreView view,
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

    /**
     * Handles the creation of a new shopping cart for a customer.
     * Displays available customers and prompts for customer selection.
     */
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

    /**
     * Handles displaying the current cart contents.
     * Requires an active cart to be present.
     */
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

    /**
     * Handles adding an item to the current cart.
     * Displays available products and prompts for product selection and quantity.
     */
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

    /**
     * Handles removing an item from the current cart.
     * Requires the cart to not be empty.
     */
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

    /**
     * Handles updating the quantity of an item in the current cart.
     * Requires the cart to not be empty.
     */
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

    /**
     * Handles clearing all items from the current cart.
     * Requires an active cart to be present.
     */
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

    /**
     * Handles the checkout process including payment and transaction finalization.
     *
     * @return true if checkout was successful and cart was cleared, false otherwise
     */
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

    /**
     * Handles adding a specific product to the cart with a given quantity.
     *
     * @param product the product to add to the cart
     * @param quantity the quantity of the product to add
     */
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

    /**
     * Loads and displays all available products.
     */
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

    /**
     * Creates a cart for the specified customer.
     *
     * @param customerId the ID of the customer to create the cart for
     */
    private void createCartForCustomer(String customerId) {
        Optional<Customer> customer = customerController.getCustomerById(customerId);

        if (customer.isPresent()) {
            currentCart = cartController.createCart(customer.get());
            view.displaySuccessMessage("Cart created for " + customer.get().getName());
        } else {
            view.displayErrorMessage("Customer not found.");
        }
    }

    /**
     * Adds an item to the cart with the specified quantity.
     *
     * @param productId the ID of the product to add
     */
    private void addItemToCart(String productId) {
        int quantity = view.getIntInput("Enter quantity: ");

        if (quantity > 0) {
            cartController.addToCart(currentCart, productId, quantity);
            view.displaySuccessMessage("Item added.");
            view.displayCart(CartDTO.fromModel(currentCart));
        }
    }

    /**
     * Removes an item from the cart.
     *
     * @param productId the ID of the product to remove
     */
    private void removeItemFromCart(String productId) {
        cartController.removeFromCart(currentCart, productId);
        view.displaySuccessMessage("Item removed.");
        view.displayCart(CartDTO.fromModel(currentCart));
    }

    /**
     * Updates the quantity of an item in the cart.
     *
     * @param productId the ID of the product to update
     */
    private void updateItemQuantity(String productId) {
        int newQuantity = view.getIntInput("Enter new quantity: ");

        if (newQuantity > 0) {
            cartController.updateCartItemQuantity(currentCart, productId, newQuantity);
            view.displaySuccessMessage("Quantity updated.");
            view.displayCart(CartDTO.fromModel(currentCart));
        }
    }

    /**
     * Processes the checkout workflow including validation and user confirmation.
     */
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

    /**
     * Completes the checkout process including points redemption and payment.
     */
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

    /**
     * Processes payment and finalizes the transaction.
     *
     * @param customer the customer making the purchase
     * @param pointsToRedeem the number of points being redeemed
     */
    private void processPaymentAndFinalize(Customer customer, int pointsToRedeem) {
        try {
            // Calculate billing breakdown
            double subtotal = currentCart.getSubtotal();
            double vat = subtotal * 0.12; // 12% VAT on original subtotal
            double discountAmount = 0;
            if (customer.isSeniorCitizen()) {
                discountAmount = subtotal * 0.20; // 20% senior discount
            }
            double total = cartService.calculateTotal(currentCart, customer);

            // Build payment prompt with billing summary using HTML for better formatting
            StringBuilder prompt = new StringBuilder();
            prompt.append("<html><body style='font-family: monospace;'>");
            prompt.append("<b style='font-size: 11px;'>BILLING SUMMARY</b><br>");
            prompt.append("─────────────────────<br>");
            prompt.append(String.format("Subtotal: ₱%.2f<br>", subtotal));
            prompt.append(String.format("VAT (12%%): ₱%.2f<br>", vat));
            if (discountAmount > 0) {
                prompt.append(String.format("Discount (Senior): -₱%.2f<br>", discountAmount));
            }
            prompt.append("─────────────────────<br>");
            prompt.append(String.format("<b style='font-size: 12px; color: #0066cc;'>Total: ₱%.2f</b><br>", total));
            prompt.append("─────────────────────<br><br>");
            prompt.append("Enter payment amount:");
            prompt.append("</body></html>");

            double payment = view.getDoubleInput(prompt.toString());

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

    /**
     * Finalizes the transaction, generates receipt, and clears the cart.
     *
     * @param payment the payment amount received
     * @param pointsToRedeem the number of points redeemed
     */
    private void finalizeTransaction(double payment, int pointsToRedeem) {
        Transaction transaction = transactionController.processTransaction(currentCart, payment, pointsToRedeem);
        String receipt = transactionController.generateReceipt(transaction);
        view.displayReceipt(receipt);

        saveReceiptFile(transaction);
        refreshProductDisplay();

        currentCart = null;
    }

    /**
     * Saves the transaction receipt to a file.
     *
     * @param transaction the transaction to save
     */
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

    /**
     * Refreshes the product display with updated inventory.
     */
    private void refreshProductDisplay() {
        List<ProductDTO> updatedProducts = productController.getAllProducts().stream()
                .map(ProductDTO::fromModel).collect(Collectors.toList());
        view.displayProducts(updatedProducts);
    }

    /**
     * Adds a validated product to the cart.
     *
     * @param product the product to add
     * @param quantity the quantity to add
     */
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

    /**
     * Validates that a product and quantity are acceptable for adding to cart.
     *
     * @param product the product to validate
     * @param quantity the quantity to validate
     * @return error message if validation fails, null if validation passes
     */
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

    /**
     * Adds a validated product to the cart and updates the display.
     *
     * @param product the product to add
     * @param quantity the quantity to add
     */
    private void addValidatedProduct(Product product, int quantity) {
        cartController.addToCart(currentCart, product.getId(), quantity);
        view.displaySuccessMessage("Item added to cart!");
        view.displayCart(CartDTO.fromModel(currentCart));
    }

    /**
     * Converts and displays a list of products.
     *
     * @param products the list of products to display
     */
    private void displayProductList(List<Product> products) {
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromModel).collect(Collectors.toList());
        view.displayProducts(productDTOs);
    }

    // ==================== VALIDATION & UTILITY METHODS ====================

    /**
     * Handles points redemption for membership customers.
     *
     * @param customer the customer attempting to redeem points
     * @return the number of points to redeem
     * @throws IllegalArgumentException if points amount is invalid
     */
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

    /**
     * Ensures that a cart exists for operations that require one.
     *
     * @return true if cart exists, false otherwise
     */
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

    /**
     * Ensures that the cart is not empty for operations that require items.
     *
     * @return true if cart is not empty, false otherwise
     */
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

    /**
     * Handles IllegalArgumentException by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     */
    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Unknown error"));
    }

    /**
     * Handles generic exceptions by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     * @param userMessage the message to display to the user
     */
    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}
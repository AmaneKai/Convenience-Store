package com.konbini.controller;

import com.konbini.model.Cart;
import com.konbini.model.Customer;
import com.konbini.model.Transaction;
import com.konbini.util.FileUtil;
import com.konbini.view.StoreView;
import java.util.Optional;

/**
 * Main controller for managing the lifecycle of a shopping cart, from creation
 * and item manipulation to the final checkout process.
 * It coordinates interaction between the user interface (StoreView) and the
 * various specialized controllers (ProductController, CartController, etc.)
 * to handle complex business logic like discounts, points redemption, and receipt generation.
 */
public class CartManagementController {
    /**
     * The Value Added Tax (VAT) rate applied to all purchases. (12%)
     */
    private static final double VAT_RATE = 0.12;
    /**
     * The discount rate applied to senior citizen purchases. (20%)
     */
    private static final double SENIOR_DISCOUNT_RATE = 0.20;
    /**
     * The conversion ratio from loyalty points to Philippine Pesos (e.g., 1 point = 1 peso).
     */
    private static final int POINTS_TO_PESO_RATIO = 1;
    /**
     * The prefix used for naming transaction receipt files.
     */
    private static final String RECEIPT_FILENAME_PREFIX = "receipt_";
    /**
     * The file extension for saved receipts.
     */
    private static final String RECEIPT_FILE_EXTENSION = ".txt";

    /**
     * The view component for user interaction and display.
     */
    private final StoreView view;
    /**
     * Controller for handling product-related business logic.
     */
    private final ProductController productController;
    /**
     * Controller for handling customer-related business logic.
     */
    private final CustomerController customerController;
    /**
     * Controller for low-level cart manipulation logic.
     */
    private final CartController cartController;
    /**
     * Controller for processing and managing transactions.
     */
    private final TransactionController transactionController;
    /**
     * The currently active shopping cart being managed.
     */
    private Cart currentCart;

    /**
     * Constructs the CartManagementController with all necessary dependencies.
     *
     * @param view The user interface component.
     * @param productController The controller for product operations.
     * @param customerController The controller for customer operations.
     * @param cartController The controller for core cart operations.

     * @param transactionController The controller for transaction and checkout logic.
     */
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

    /**
     * Starts the cart management loop, displaying the cart menu and handling
     * user input until the user chooses to exit.
     */
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

    /**
     * Initiates the creation of a new cart, prompts the user for a customer ID,
     * and associates the cart with the retrieved Customer.
     */
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

    /**
     * Displays the contents of the currently active cart to the user.
     */
    private void viewCart() {
        if (currentCart != null) {
            view.displayCart(currentCart);
        } else {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
        }
    }

    /**
     * Prompts the user for a product ID and quantity, then adds the item to the
     * active cart via the CartController.
     */
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

    /**
     * Prompts the user for a product ID and removes that item from the active cart.
     */
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

    /**
     * Prompts the user for a product ID and a new quantity, then updates the item
     * in the active cart.
     */
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

    /**
     * Clears all items from the active cart.
     */
    private void clearCart() {
        if (currentCart != null) {
            cartController.clearCart(currentCart);
            view.displaySuccessMessage("Cart cleared successfully.");
        } else {
            view.displayErrorMessage
                ("No active cart. Please create a cart first.");
        }
    }

    /**
     * Handles the complete checkout process: calculating the total, applying
     * discounts/points, processing payment, and generating/saving the receipt.
     */
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

            // Once checkout is complete, the cart is disposed of.
            currentCart = null;
        } catch (Exception e) {
            view.displayErrorMessage("Checkout failed: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to input the number of loyalty points to redeem,
     * validating against the customer's available points.
     *
     * @param customer The Customer whose points are being redeemed.
     * @return The number of points the user successfully chose to redeem.
     */
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
            // Re-prompt user until a valid input is given (or 0 is chosen)
            return view.getIntInput
                ("Enter points to redeem (0-" + availablePoints + "): ");
        }

        return pointsToRedeem;
    }

    /**
     * Calculates the final total amount due, applying VAT, Senior Citizen
     * discount if applicable, and deducting the value of redeemed points.
     *
     * @param customer The Customer for discount consideration.
     * @param pointsToRedeem The number of loyalty points to deduct from the total.
     * @return The final calculated amount due.
     */
    private double calculateTotalAmount(Customer customer,
            int pointsToRedeem) {
        double subtotal = currentCart.getSubtotal();
        // Apply VAT
        double totalWithTax = subtotal * (1 + VAT_RATE);

        // Apply Senior Discount
        if (customer.isSeniorCitizen()) {
            totalWithTax *= (1 - SENIOR_DISCOUNT_RATE);
        }

        // Apply point redemption
        return totalWithTax - (pointsToRedeem * POINTS_TO_PESO_RATIO);
    }

    /**
     * Generates the full file path where the receipt should be saved,
     * ensuring the receipts directory exists.
     *
     * @param transactionId The unique ID of the completed transaction.
     * @return The full path string for the receipt file.
     */
    private String buildReceiptFilePath(String transactionId) {
        // FileUtil.ensureReceiptsDirectory is assumed to return the path and create the directory if it doesn't exist.
        String receiptsDir = FileUtil.ensureReceiptsDirectory();
        return receiptsDir + "/" + RECEIPT_FILENAME_PREFIX
            + transactionId + RECEIPT_FILE_EXTENSION;
    }
}

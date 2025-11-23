package com.konbini.controller;

import com.konbini.view.BaseView;
import java.time.LocalDate;

/**
 * Controller responsible for managing data operations including saving, loading,
 * and initializing sample data for products, customers, and transactions.
 */
public class DataManagementController {
    private final BaseView view;
    private final ProductController productController;
    private final CustomerController customerController;
    private final TransactionController transactionController;
    private final ProductManagementController productManagementController;

    /**
     * Constructs a DataManagementController with all required dependencies.
     *
     * @param view the store view for user interface interactions
     * @param productController controller for product operations
     * @param customerController controller for customer operations
     * @param transactionController controller for transaction operations
     * @param productManagementController controller for product management operations
     * @throws IllegalArgumentException if any dependency is null
     */
    public DataManagementController(
            BaseView view,
            ProductController productController,
            CustomerController customerController,
            TransactionController transactionController,
            ProductManagementController productManagementController) {
        if (view == null || productController == null || customerController == null ||
                transactionController == null || productManagementController == null) {
            throw new IllegalArgumentException("All dependencies must be provided");
        }
        this.view = view;
        this.productController = productController;
        this.customerController = customerController;
        this.transactionController = transactionController;
        this.productManagementController = productManagementController;
    }

    /**
     * Saves all data (products, customers, transactions) to persistent storage.
     * Displays appropriate success/error messages based on the operation results.
     */
    public void handleSaveData() {
        try {
            int successCount = 0;
            int totalOperations = 3;

            boolean productsSaved = saveProducts();
            boolean customersSaved = saveCustomers();
            boolean transactionsSaved = saveTransactions();

            if (productsSaved) successCount++;
            if (customersSaved) successCount++;
            if (transactionsSaved) successCount++;

            if (successCount == totalOperations) {
                view.displaySuccessMessage("All data saved successfully.");
            } else if (successCount > 0) {
                view.displayInfoMessage("Partially saved: " + successCount + "/" + totalOperations + " data types saved.");
            } else {
                view.displayErrorMessage("Failed to save all data types.");
            }

        } catch (Exception e) {
            handleGenericException(e, "saving data", "Unexpected error during data save operation.");
        }
    }

    /**
     * Loads all data (products, customers, transactions) from persistent storage.
     * Provides user-friendly messages for first-time use when no data exists.
     */
    public void handleLoadData() {
        try {
            int successCount = 0;
            int totalOperations = 3;

            boolean productsLoaded = loadProducts();
            boolean customersLoaded = loadCustomers();
            boolean transactionsLoaded = loadTransactions();

            if (productsLoaded) successCount++;
            if (customersLoaded) successCount++;
            if (transactionsLoaded) successCount++;

            if (successCount == totalOperations) {
                view.displaySuccessMessage("All data loaded successfully.");
            } else if (successCount > 0) {
                view.displayInfoMessage("Partially loaded: " + successCount + "/" + totalOperations + " data types loaded.");
            } else {
                // FRIENDLIER first-time message
                view.displayInfoMessage("No saved data found. This is normal for first-time use. " +
                        "Use 'Initialize Sample Data' to get started, then 'Save Data' to persist.");
            }

        } catch (Exception e) {
            handleGenericException(e, "loading data", "Unexpected error during data load operation.");
        }
    }

    /**
     * Initializes sample data for the application including sample products and customers.
     * Only initializes data if no existing data is found to avoid overwriting.
     * Automatically saves the initialized data to persistent storage.
     */
    public void initializeSampleData() {
        try {
            // Check if we need to initialize
            if (shouldInitializeSampleData()) {
                initializeSampleProducts();
                initializeSampleCustomers();

                // Auto-save the initialized data
                int successCount = 0;
                if (saveProducts()) successCount++;
                if (saveCustomers()) successCount++;
                if (saveTransactions()) successCount++;

                if (successCount >= 2) {
                    view.displaySuccessMessage("Sample data initialized and saved successfully.");
                } else {
                    view.displayInfoMessage("Sample data initialized but may not have saved completely. Please use 'Save Data' to persist.");
                }
            } else {
                view.displayInfoMessage("Sample data already exists. No initialization needed.");
            }

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "initializing sample data");
        } catch (Exception e) {
            handleGenericException(e, "initializing sample data", "Failed to initialize sample data. Some data may be incomplete.");
        }
    }

    /**
     * Checks if sample data should be initialized by verifying if current data is empty.
     *
     * @return true if both products and customers lists are empty, false otherwise
     */
    private boolean shouldInitializeSampleData() {
        return productController.getAllProducts().isEmpty() &&
                customerController.getAllCustomers().isEmpty();
    }

    /**
     * Initializes sample customers with various types including membership customers.
     * Continues initialization even if some customers fail to be created.
     */
    private void initializeSampleCustomers() {
        try {
            customerController.registerCustomer("Juan Dela Cruz", false);
            customerController.registerCustomer("Maria Santos", true);

            try {
                customerController.registerCustomerWithMembershipCard(
                        "Pedro Reyes", false, "MEM-001", LocalDate.now().plusYears(2));
            } catch (IllegalArgumentException e) {
                System.err.println("Note: Pedro Reyes may already exist: " + e.getMessage());
            }

            try {
                customerController.registerCustomerWithMembershipCard(
                        "Ana Gonzales", true, "MEM-002", LocalDate.now().plusYears(1));
            } catch (IllegalArgumentException e) {
                System.err.println("Note: Ana Gonzales may already exist: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error initializing some sample customers: " + e.getMessage());
            // Continue - partial initialization is better than complete failure
        }
    }

    /**
     * Initializes sample products by delegating to the product management controller.
     *
     * @throws Exception if product initialization fails
     */
    private void initializeSampleProducts() {
        try {
            productManagementController.initializeSampleProducts();
        } catch (Exception e) {
            System.err.println("Error initializing sample products: " + e.getMessage());
            throw e; // Re-throw - product initialization is critical
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Saves product data to persistent storage.
     *
     * @return true if save operation was successful, false otherwise
     */
    private boolean saveProducts() {
        boolean temp = false;

        try {
            temp = productController.saveData();
        } catch (Exception e) {
            System.err.println("Failed to save products: " + e.getMessage());
        }

        return temp;
    }

    /**
     * Saves customer data to persistent storage.
     *
     * @return true if save operation was successful, false otherwise
     */
    private boolean saveCustomers() {
        boolean temp = false;
        try {
            temp = customerController.saveData();
        } catch (Exception e) {
            System.err.println("Failed to save customers: " + e.getMessage());
        }
        return temp;
    }

    /**
     * Saves transaction data to persistent storage.
     *
     * @return true if save operation was successful, false otherwise
     */
    private boolean saveTransactions() {
        boolean temp = false;
        try {
            temp = transactionController.saveData();
        } catch (Exception e) {
            System.err.println("Failed to save transactions: " + e.getMessage());
        }
        return temp;
    }

    /**
     * Loads product data from persistent storage.
     *
     * @return true if load operation was successful, false otherwise
     */
    private boolean loadProducts() {
        boolean temp = false;
        try {
            temp = productController.loadData();
        } catch (Exception e) {
            System.err.println("Failed to load products: " + e.getMessage());
        }
        return temp;
    }

    /**
     * Loads customer data from persistent storage.
     *
     * @return true if load operation was successful, false otherwise
     */
    private boolean loadCustomers() {
        boolean temp = false;
        try {
            temp = customerController.loadData();
        } catch (Exception e) {
            System.err.println("Failed to load customers: " + e.getMessage());
        }
        return temp;
    }

    /**
     * Loads transaction data from persistent storage.
     *
     * @return true if load operation was successful, false otherwise
     */
    private boolean loadTransactions() {
        boolean temp = false;
        try {
            temp = transactionController.loadData();
        } catch (Exception e) {
            System.err.println("Failed to load transactions: " + e.getMessage());
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
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
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
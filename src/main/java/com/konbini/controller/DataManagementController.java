package com.konbini.controller;

import com.konbini.view.StoreView;
import java.time.LocalDate;

public class DataManagementController {
    private final StoreView view;
    private final ProductController productController;
    private final CustomerController customerController;
    private final TransactionController transactionController;
    private final ProductManagementController productManagementController;

    public DataManagementController(
            StoreView view,
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

    public void initializeSampleData() {
        try {
            // Check if we need to initialize
            if (shouldInitializeSampleData()) {
                initializeSampleProducts();
                initializeSampleCustomers();
                view.displaySuccessMessage("Sample data initialized successfully.");
            } else {
                view.displayInfoMessage("Sample data already exists. No initialization needed.");
            }

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "initializing sample data");
        } catch (Exception e) {
            handleGenericException(e, "initializing sample data", "Failed to initialize sample data. Some data may be incomplete.");
        }
    }

    private boolean shouldInitializeSampleData() {
        return productController.getAllProducts().isEmpty() &&
                customerController.getAllCustomers().isEmpty();
    }

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

    private void initializeSampleProducts() {
        try {
            productManagementController.initializeSampleProducts();
        } catch (Exception e) {
            System.err.println("Error initializing sample products: " + e.getMessage());
            throw e; // Re-throw - product initialization is critical
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private boolean saveProducts() {
        try {
            return productController.saveData();
        } catch (Exception e) {
            System.err.println("Failed to save products: " + e.getMessage());
            return false;
        }
    }

    private boolean saveCustomers() {
        try {
            return customerController.saveData();
        } catch (Exception e) {
            System.err.println("Failed to save customers: " + e.getMessage());
            return false;
        }
    }

    private boolean saveTransactions() {
        try {
            return transactionController.saveData();
        } catch (Exception e) {
            System.err.println("Failed to save transactions: " + e.getMessage());
            return false;
        }
    }

    private boolean loadProducts() {
        try {
            return productController.loadData();
        } catch (Exception e) {
            System.err.println("Failed to load products: " + e.getMessage());
            return false;
        }
    }

    private boolean loadCustomers() {
        try {
            return customerController.loadData();
        } catch (Exception e) {
            System.err.println("Failed to load customers: " + e.getMessage());
            return false;
        }
    }

    private boolean loadTransactions() {
        try {
            return transactionController.loadData();
        } catch (Exception e) {
            System.err.println("Failed to load transactions: " + e.getMessage());
            return false;
        }
    }

    // ==================== ERROR HANDLING HELPERS ====================

    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}
package com.konbini.controller;

import com.konbini.view.StoreView;
import java.time.LocalDate;

/**
 * Controller class responsible for coordinating system-wide data operations,
 * including saving data to storage, loading data from storage, and initializing
 * sample data for the application.
 * It orchestrates calls to the persistence methods of the main functional controllers.
 */
public class DataManagementController {
    /**
     * The view component for displaying status messages and errors to the user.
     */
    private final StoreView view;
    /**
     * Controller managing product data persistence.
     */
    private final ProductController productController;
    /**
     * Controller managing customer data persistence.
     */
    private final CustomerController customerController;
    /**
     * Controller managing transaction data persistence.
     */
    private final TransactionController transactionController;
    /**
     * Controller used to initialize sample product data.
     */
    private final ProductManagementController productManagementController;

    /**
     * Constructs the DataManagementController, injecting all necessary controllers
     * and the StoreView dependency.
     *
     * @param view The user interface component.
     * @param productController The controller for product data operations.
     * @param customerController The controller for customer data operations.
     * @param transactionController The controller for transaction data operations.
     * @param productManagementController The controller used for product initialization.
     */
    public DataManagementController(
            StoreView view,
            ProductController productController,
            CustomerController customerController,
            TransactionController transactionController,
            ProductManagementController productManagementController) {
        this.view = view;
        this.productController = productController;
        this.customerController = customerController;
        this.transactionController = transactionController;
        this.productManagementController = productManagementController;
    }

    /**
     * Attempts to save all application data (products, customers, and transactions)
     * by delegating the save operation to their respective controllers.
     * Displays a success or failure message based on the overall outcome.
     */
    public void handleSaveData() {
        boolean productsSaved = productController.saveData();
        boolean customersSaved = customerController.saveData();
        boolean transactionsSaved = transactionController.saveData();

        if (productsSaved && customersSaved && transactionsSaved) {
            view.displaySuccessMessage("All data saved successfully.");
        } else {
            view.displayErrorMessage("Failed to save some data.");
        }
    }

    /**
     * Attempts to load all application data (products, customers, and transactions)
     * by delegating the load operation to their respective controllers.
     * Displays a success or failure message based on the overall outcome.
     */
    public void handleLoadData() {
        boolean productsLoaded = productController.loadData();
        boolean customersLoaded = customerController.loadData();
        boolean transactionsLoaded = transactionController.loadData();

        if (productsLoaded && customersLoaded && transactionsLoaded) {
            view.displaySuccessMessage("All data loaded successfully.");
        } else {
            view.displayErrorMessage("Failed to load some data.");
        }
    }

    /**
     * Initializes the system with a set of predefined sample data, including
     * products and customers (some with membership cards and senior citizen status).
     * This is useful for first-time application setup or demonstration purposes.
     */
    public void initializeSampleData() {
        try {
            // Initialize sample products via the ProductManagementController
            productManagementController.initializeSampleProducts();

            // Initialize sample customers
            customerController.registerCustomer("Juan Dela Cruz", false);
            customerController.registerCustomer("Maria Santos", true);
            customerController.registerCustomerWithMembershipCard(
                "Pedro Reyes", false, "MEM-001", LocalDate.now().plusYears(2));
            customerController.registerCustomerWithMembershipCard(
                "Ana Gonzales", true, "MEM-002", LocalDate.now().plusYears(1));

            view.displaySuccessMessage
                ("Sample data initialized successfully.");
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to initialize sample data: " + e.getMessage());
        }
    }
}

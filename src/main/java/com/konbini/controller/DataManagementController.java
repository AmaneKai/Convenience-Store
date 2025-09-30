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
        this.view = view;
        this.productController = productController;
        this.customerController = customerController;
        this.transactionController = transactionController;
        this.productManagementController = productManagementController;
    }
    
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
    
    public void initializeSampleData() {
        try {
            productManagementController.initializeSampleProducts();
            
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

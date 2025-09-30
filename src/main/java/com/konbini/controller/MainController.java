package com.konbini.controller;

import com.konbini.view.StoreView;

public class MainController {
    private final StoreView view;
    private final CustomerManagementController customerManagementController;
    private final CartManagementController cartManagementController;
    private final TransactionManagementController 
        transactionManagementController;
    private final DataManagementController dataManagementController;
    private final ProductManagementController productManagementController;
    
    public MainController(
            StoreView view,
            CustomerManagementController customerManagementController,
            CartManagementController cartManagementController,
            TransactionManagementController transactionManagementController,
            DataManagementController dataManagementController,
            ProductManagementController productManagementController) {
        this.view = view;
        this.customerManagementController = customerManagementController;
        this.cartManagementController = cartManagementController;
        this.transactionManagementController = transactionManagementController;
        this.dataManagementController = dataManagementController;
        this.productManagementController = productManagementController;
    }
    
    public void start() {
        view.displayWelcomeMessage();
        boolean running = true;
        
        while (running) {
            view.displayMainMenu();
            int choice = view.getMainMenuChoice();
            
            switch (choice) {
                case 1:
                    productManagementController.handleProductManagement();
                    break;
                case 2:
                    customerManagementController.handleCustomerManagement();
                    break;
                case 3:
                    cartManagementController.handleCartManagement();
                    break;
                case 4:
                    transactionManagementController
                        .handleTransactionManagement();
                    break;
                case 5:
                    dataManagementController.handleSaveData();
                    break;
                case 6:
                    dataManagementController.handleLoadData();
                    break;
                case 0:
                    running = false;
                    view.displayInfoMessage
                        ("Thank you for using Konbini Store. Goodbye!");
                    break;
                default:
                    view.displayErrorMessage
                        ("Invalid choice. Please try again.");
            }
        }
    }
    
    public void initializeSampleData() {
        dataManagementController.initializeSampleData();
    }
}

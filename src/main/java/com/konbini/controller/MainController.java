package com.konbini.controller;

import com.konbini.view.StoreView;

/**
 * The central controller of the Konbini Store application.
 * FULLY EVENT-DRIVEN for Swing GUI - no blocking loops.
 * 
 * This class is responsible for initializing the application and handling
 * navigation between screens. All user interactions are event-driven through
 * button clicks in the GUI.
 */
public class MainController {
    /**
     * The view component responsible for all user input and output.
     */
    private final StoreView view;
    /**
     * Controller for customer administration tasks.
     */
    private final CustomerManagementController customerManagementController;
    /**
     * Controller for shopping cart and checkout processes.
     */
    private final CartManagementController cartManagementController;
    /**
     * Controller for viewing and searching historical transactions.
     */
    private final TransactionManagementController transactionManagementController;
    /**
     * Controller for handling persistence (save/load) and data initialization.
     */
    private final DataManagementController dataManagementController;
    /**
     * Controller for product creation, modification, and inventory management.
     */
    private final ProductManagementController productManagementController;

    /**
     * Constructs the MainController, injecting all necessary specialized controllers
     * and the main StoreView dependency.
     *
     * @param view The user interface component for interaction.
     * @param customerManagementController The controller for customer operations.
     * @param cartManagementController The controller for cart and sales operations.
     * @param transactionManagementController The controller for transaction history operations.
     * @param dataManagementController The controller for data save/load operations.
     * @param productManagementController The controller for product operations.
     */
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

    /**
     * Starts the application by displaying the main menu and welcome message.
     * For Swing GUI, this simply displays the UI - no blocking loops.
     * All navigation is now event-driven through button clicks.
     */
    public void start() {
        view.displayWelcomeMessage();
        view.displayMainMenu();
    }

    /**
     * Delegates the task of initializing sample data (products and customers)
     * to the DataManagementController. This is typically run once when the
     * application starts without existing data.
     */
    public void initializeSampleData() {
        dataManagementController.initializeSampleData();
    }
}
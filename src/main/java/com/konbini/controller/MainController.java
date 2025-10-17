package com.konbini.controller;

import com.konbini.view.StoreView;

/**
 * The central controller of the Konbini Store application.
 * This class is responsible for initializing the application, managing the main
 * application loop, displaying the main menu, and delegating control to
 * specialized management controllers based on user selection.
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
    private final TransactionManagementController
        transactionManagementController;
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
     * and the main StoreView dependency. This follows the Dependency Injection pattern.
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
     * Starts the main application loop. It displays the welcome message and then
     * continuously presents the main menu, delegating control to the
     * appropriate controller based on the user's choice until the application is exited.
     */
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

    /**
     * Delegates the task of initializing sample data (products and customers)
     * to the DataManagementController. This is typically run once when the
     * application starts without existing data.
     */
    public void initializeSampleData() {
        dataManagementController.initializeSampleData();
    }
}

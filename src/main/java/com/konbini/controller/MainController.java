package com.konbini.controller;

import com.konbini.Main;
import com.konbini.view.MainView;

/**
 * Main controller that coordinates the overall application flow.
 * Handles application startup and delegates sample data initialization.
 */
public class MainController {
    private final MainView view;
    private final DataManagementController dataManagementController;

    /**
     * Constructs a MainController with the specified view and data management controller.
     *
     * @param view the store view for user interface interactions
     * @param dataManagementController the controller for managing sample data initialization
     */
    public MainController(
            MainView view,
            DataManagementController dataManagementController) {
        this.view = view;
        this.dataManagementController = dataManagementController;
    }

    /**
     * Starts the application by displaying welcome message and main menu.
     */
    public void start() {
        view.displayWelcomeMessage();
        view.displayMainMenu();
    }

    /**
     * Initializes sample data for the application by delegating to the data management controller.
     */
    public void initializeSampleData() {
        dataManagementController.initializeSampleData();
    }
}
package com.konbini.controller;

import com.konbini.view.StoreView;

public class MainController {
    private final StoreView view;
    private final DataManagementController dataManagementController;

    public MainController(
            StoreView view,
            DataManagementController dataManagementController) {
        this.view = view;
        this.dataManagementController = dataManagementController;
    }

    public void start() {
        view.displayWelcomeMessage();
        view.displayMainMenu();
    }

    public void initializeSampleData() {
        dataManagementController.initializeSampleData();
    }
}
package com.konbini.view;

/**
 * Defines the contract for the core view component responsible for the application's
 * main entry point and top-level navigation.
 * It extends BaseView to inherit fundamental display and input capabilities.
 */
public interface MainView extends BaseView {
    /**
     * Displays a greeting or introductory message to the user upon application startup.
     */
    void displayWelcomeMessage();

    /**
     * Displays the primary menu of the application, listing the main functional areas
     * accessible to the user (e.g., Product Management, Shopping Cart, Exit).
     */
    void displayMainMenu();

    /**
     * Prompts the user for input and retrieves the selected option from the main menu.
     * The implementation must ensure the input is a valid menu choice.
     *
     * @return The integer corresponding to the user's selected main menu item.
     */
    int getMainMenuChoice();
}

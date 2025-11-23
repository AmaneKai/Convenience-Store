package com.konbini.view;

/**
 * View interface for main application entry point and navigation.
 * Extends BaseView to provide main application-level user interactions
 * including welcome messages and main menu navigation.
 */
public interface MainView extends BaseView {

    /**
     * Displays the application welcome message to the user.
     * Typically shown at application startup to greet the user
     * and provide initial application information.
     */
    void displayWelcomeMessage();

    /**
     * Displays the main application menu to the user.
     * Provides navigation options to different sections of the application
     * such as product management, customer management, cart operations, etc.
     */
    void displayMainMenu();

    /**
     * Gets the user's selection from the main application menu.
     *
     * @return the user's menu choice as an integer
     */
    int getMainMenuChoice();
}
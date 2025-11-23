package com.konbini.view;

import java.time.LocalDate;

/**
 * Base interface for view components in the application.
 * Defines common user interaction methods for displaying messages
 * and gathering various types of user input.
 *
 * Implementations of this interface provide the user interface
 * layer for the application, handling all user interactions.
 */
public interface BaseView {

    /**
     * Displays an error message to the user.
     * Typically used for validation errors, operation failures, or exceptional conditions.
     *
     * @param message the error message to display
     */
    void displayErrorMessage(String message);

    /**
     * Displays a success message to the user.
     * Typically used to confirm successful operations.
     *
     * @param message the success message to display
     */
    void displaySuccessMessage(String message);

    /**
     * Displays an informational message to the user.
     * Typically used for general information, instructions, or status updates.
     *
     * @param message the informational message to display
     */
    void displayInfoMessage(String message);

    /**
     * Prompts the user for string input.
     *
     * @param prompt the message displayed to prompt the user
     * @return the user's input as a string
     */
    String getStringInput(String prompt);

    /**
     * Prompts the user for integer input.
     *
     * @param prompt the message displayed to prompt the user
     * @return the user's input as an integer
     */
    int getIntInput(String prompt);

    /**
     * Prompts the user for double/decimal input.
     *
     * @param prompt the message displayed to prompt the user
     * @return the user's input as a double
     */
    double getDoubleInput(String prompt);

    /**
     * Prompts the user for boolean input (yes/no, true/false).
     *
     * @param prompt the message displayed to prompt the user
     * @return the user's input as a boolean
     */
    boolean getBooleanInput(String prompt);

    /**
     * Prompts the user for date input.
     *
     * @param prompt the message displayed to prompt the user
     * @return the user's input as a LocalDate
     */
    LocalDate getDateInput(String prompt);
}
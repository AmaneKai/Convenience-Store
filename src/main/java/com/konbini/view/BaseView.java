package com.konbini.view;

import java.time.LocalDate;

/**
 * Defines the fundamental contract for all view (user interface) components
 * in the application. It establishes standard methods for displaying messages
 * to the user and for gathering various types of input.
 */
public interface BaseView {
    /**
     * Displays a critical error message to the user.
     * The implementation should highlight the message to signify a problem.
     *
     * @param message The error text to be displayed.
     */
    void displayErrorMessage(String message);

    /**
     * Displays a positive confirmation message to the user, indicating a successful operation.
     *
     * @param message The success text to be displayed.
     */
    void displaySuccessMessage(String message);

    /**
     * Displays a general informational message to the user.
     *
     * @param message The information text to be displayed.
     */
    void displayInfoMessage(String message);

    //--------------------------------------------------------------------------

    /**
     * Prompts the user for a string input and reads the response.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The string entered by the user.
     */
    String getStringInput(String prompt);

    /**
     * Prompts the user for an integer input and reads the response.
     * The implementation should handle parsing and re-prompting on invalid input.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The integer entered by the user.
     */
    int getIntInput(String prompt);

    /**
     * Prompts the user for a double-precision floating-point number and reads the response.
     * The implementation should handle parsing and re-prompting on invalid input.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The double value entered by the user.
     */
    double getDoubleInput(String prompt);

    /**
     * Prompts the user for a boolean input (typically 'y/n' or 'true/false') and reads the response.
     * The implementation should interpret the user's input into a boolean value.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return True or false based on the user's input.
     */
    boolean getBooleanInput(String prompt);

    /**
     * Prompts the user for a date input and reads the response, typically in a specific format.
     * The implementation should handle parsing and re-prompting on invalid input format.
     *
     * @param prompt The message displayed to the user requesting the input (should include the expected format).
     * @return The LocalDate object representing the date entered by the user.
     */
    LocalDate getDateInput(String prompt);
}

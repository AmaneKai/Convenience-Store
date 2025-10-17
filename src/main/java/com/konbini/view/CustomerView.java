package com.konbini.view;

import com.konbini.model.Customer;

import java.util.List;

/**
 * Defines the user interface contract specifically for managing and displaying customer information.
 * It extends BaseView to inherit fundamental display and input capabilities.
 */
public interface CustomerView extends BaseView {
    /**
     * Displays the primary menu options available within the customer management section
     * (e.g., register new customer, view details, update information).
     */
    void displayCustomerMenu();

    /**
     * Prompts the user for and retrieves the selection from the customer management menu.
     * The implementation must ensure the input is a valid menu option.
     *
     * @return The integer corresponding to the user's selected menu item.
     */
    int getCustomerMenuChoice();

    /**
     * Displays a formatted list of multiple customers.
     * This typically includes key summary details like ID and name.
     *
     * @param customers The list of Customer objects to be displayed.
     */
    void displayCustomers(List<Customer> customers);

    /**
     * Displays the full, detailed information for a single customer, including membership status and points.
     *
     * @param customer The Customer object whose details are to be displayed.
     */
    void displayCustomer(Customer customer);
}

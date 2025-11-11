package com.konbini.view;

import java.util.List;

import com.konbini.dto.CustomerDTO;

/**
 * Defines the user interface contract specifically for managing and displaying customer information.
 * It extends BaseView to inherit fundamental display and input capabilities.
 * 
 * IMPORTANT: This interface uses CustomerDTO exclusively - no model imports.
 * Controllers are responsible for converting Customer models to CustomerDTOs.
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
     * @param customers The list of CustomerDTO objects to be displayed.
     */
    void displayCustomers(List<CustomerDTO> customers);

    /**
     * Displays the full, detailed information for a single customer, including membership status and points.
     *
     * @param customer The CustomerDTO object whose details are to be displayed.
     */
    void displayCustomer(CustomerDTO customer);
}
package com.konbini.view;

import java.util.List;

import com.konbini.dto.CustomerDTO;

/**
 * View interface for customer management operations and display.
 * Extends BaseView to provide customer-specific user interactions
 * including customer menu display and customer information visualization.
 */
public interface CustomerView extends BaseView {

    /**
     * Displays the customer management menu to the user.
     * Typically includes options for viewing, adding, updating,
     * removing customers, and managing membership cards.
     */
    void displayCustomerMenu();

    /**
     * Gets the user's selection from the customer management menu.
     *
     * @return the user's menu choice as an integer
     */
    int getCustomerMenuChoice();

    /**
     * Displays a list of customers to the user.
     * Shows customer information in a list format, typically with
     * summary details for each customer.
     *
     * @param customers the list of CustomerDTO objects to display
     */
    void displayCustomers(List<CustomerDTO> customers);

    /**
     * Displays detailed information for a single customer.
     * Shows comprehensive customer details including personal information,
     * membership status, and loyalty points.
     *
     * @param customer the CustomerDTO containing detailed customer information to display
     */
    void displayCustomer(CustomerDTO customer);
}
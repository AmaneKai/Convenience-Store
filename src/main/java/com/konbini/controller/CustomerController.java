package com.konbini.controller;

import com.konbini.model.Customer;
import com.konbini.service.CustomerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller class managing customer-related operations and business logic.
 * It serves as the layer between the application view/user interaction
 * and the underlying data service (CustomerService).
 */
public class CustomerController {
    /**
     * The service dependency used for all data persistence and core business
     * logic related to Customer entities.
     */
    private final CustomerService customerService;

    /**
     * Constructs the CustomerController, injecting the required customer service.
     *
     * @param customerService The service providing data access and business logic for customers.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Retrieves a list of all registered customers in the system.
     *
     * @return A List of all Customer objects.
     */
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    /**
     * Retrieves a customer by their unique identifier.
     *
     * @param customerId The ID of the customer to find.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    public Optional<Customer> getCustomerById(String customerId) {
        return customerService.findById(customerId);
    }

    /**
     * Retrieves a customer associated with a specific membership card number.
     *
     * @param cardNumber The membership card number.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    public Optional<Customer> getCustomerByMembershipCard(String cardNumber) {
        return customerService.findByMembershipCard(cardNumber);
    }

    /**
     * Registers a new customer without a membership card.
     *
     * @param name The full name of the customer.
     * @param isSeniorCitizen Flag indicating if the customer is a senior citizen.
     */
    public void registerCustomer(String name, boolean isSeniorCitizen) {
        customerService.registerCustomer(name, isSeniorCitizen);
    }

    /**
     * Registers a new customer along with a new membership card.
     *
     * @param name The full name of the customer.
     * @param isSeniorCitizen Flag indicating if the customer is a senior citizen.
     * @param cardNumber The membership card number.
     * @param expiryDate The expiration date of the card.
     */
    public void registerCustomerWithMembershipCard(String name,
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate) {
        customerService.registerCustomerWithMembershipCard(name,
            isSeniorCitizen, cardNumber, expiryDate);
    }

    /**
     * Updates the name and senior citizen status of an existing customer.
     *
     * @param customerId The ID of the customer to update.
     * @param name The new name for the customer.
     * @param isSeniorCitizen The new senior citizen status.
     */
    public void updateCustomer(String customerId, String name,
        boolean isSeniorCitizen) {
        customerService.updateCustomer(customerId, name, isSeniorCitizen);
    }

    /**
     * Removes a customer from the system based on their ID.
     *
     * @param customerId The ID of the customer to remove.
     */
    public void removeCustomer(String customerId) {
        customerService.removeCustomer(customerId);
    }

    /**
     * Adds a new membership card to an existing customer.
     *
     * @param customerId The ID of the customer to add the card to.
     * @param cardNumber The membership card number.
     * @param expiryDate The expiration date of the card.
     */
    public void addMembershipCard(String customerId, String cardNumber,
        LocalDate expiryDate) {
        customerService.addMembershipCard(customerId, cardNumber, expiryDate);
    }

    /**
     * Increases the loyalty points balance for a specified customer.
     *
     * @param customerId The ID of the customer.
     * @param points The number of points to add.
     */
    public void addPointsToCustomer(String customerId, int points) {
        customerService.addPointsToCustomer(customerId, points);
    }

    /**
     * Decreases the loyalty points balance for a specified customer (e.g., during redemption).
     *
     * @param customerId The ID of the customer.
     * @param points The number of points to deduct.
     */
    public void useCustomerPoints(String customerId, int points) {
        customerService.useCustomerPoints(customerId, points);
    }

    /**
     * Persists the current list of customers to permanent storage.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    public boolean saveData() {
        return customerService.saveCustomers();
    }

    /**
     * Loads the customer data from permanent storage into the application memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    public boolean loadData() {
        return customerService.loadCustomers();
    }
}

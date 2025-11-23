package com.konbini.controller;

import com.konbini.model.Customer;
import com.konbini.service.CustomerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing customer operations including registration, updates,
 * membership management, and data persistence.
 */
public class CustomerController {
    private final CustomerService customerService;

    /**
     * Constructs a CustomerController with the specified customer service.
     *
     * @param customerService the service for customer operations
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Retrieves all customers from the system.
     *
     * @return a list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    /**
     * Finds a customer by their unique identifier.
     *
     * @param customerId the ID of the customer to find
     * @return an Optional containing the customer if found, empty otherwise
     */
    public Optional<Customer> getCustomerById(String customerId) {
        return customerService.findById(customerId);
    }

    /**
     * Finds a customer by their membership card number.
     *
     * @param cardNumber the membership card number to search for
     * @return an Optional containing the customer if found, empty otherwise
     */
    public Optional<Customer> getCustomerByMembershipCard(String cardNumber) {
        return customerService.findByMembershipCard(cardNumber);
    }

    /**
     * Registers a new customer with basic information.
     *
     * @param name the customer's name
     * @param isSeniorCitizen whether the customer is a senior citizen
     */
    public void registerCustomer(String name, boolean isSeniorCitizen) {
        customerService.registerCustomer(name, isSeniorCitizen);
    }

    /**
     * Registers a new customer with a membership card.
     *
     * @param name the customer's name
     * @param isSeniorCitizen whether the customer is a senior citizen
     * @param cardNumber the membership card number
     * @param expiryDate the membership card expiry date
     */
    public void registerCustomerWithMembershipCard(String name,
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate) {
        customerService.registerCustomerWithMembershipCard(name,
            isSeniorCitizen, cardNumber, expiryDate);
    }

    /**
     * Updates an existing customer's information.
     *
     * @param customerId the ID of the customer to update
     * @param name the updated name
     * @param isSeniorCitizen the updated senior citizen status
     */
    public void updateCustomer(String customerId, String name,
        boolean isSeniorCitizen) {
        customerService.updateCustomer(customerId, name, isSeniorCitizen);
    }

    /**
     * Removes a customer from the system.
     *
     * @param customerId the ID of the customer to remove
     */
    public void removeCustomer(String customerId) {
        customerService.removeCustomer(customerId);
    }

    /**
     * Adds a membership card to an existing customer.
     *
     * @param customerId the ID of the customer
     * @param cardNumber the membership card number
     * @param expiryDate the membership card expiry date
     */
    public void addMembershipCard(String customerId, String cardNumber,
        LocalDate expiryDate) {
        customerService.addMembershipCard(customerId, cardNumber, expiryDate);
    }

    /**
     * Adds loyalty points to a customer's membership card.
     *
     * @param customerId the ID of the customer
     * @param points the number of points to add
     */
    public void addPointsToCustomer(String customerId, int points) {
        customerService.addPointsToCustomer(customerId, points);
    }

    /**
     * Uses (redeems) loyalty points from a customer's membership card.
     *
     * @param customerId the ID of the customer
     * @param points the number of points to use
     */
    public void useCustomerPoints(String customerId, int points) {
        customerService.useCustomerPoints(customerId, points);
    }

    /**
     * Saves all customer data to persistent storage.
     *
     * @return true if save operation was successful, false otherwise
     */
    public boolean saveData() {
        return customerService.saveCustomers();
    }

    /**
     * Loads all customer data from persistent storage.
     *
     * @return true if load operation was successful, false otherwise
     */
    public boolean loadData() {
        return customerService.loadCustomers();
    }
}
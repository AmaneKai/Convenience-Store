package com.konbini.service;

import com.konbini.model.Customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for customer management operations.
 * Handles customer registration, membership management, loyalty points,
 * and data persistence operations.
 */
public interface CustomerService {

    /**
     * Registers a new customer with basic information.
     *
     * @param name the customer's name
     * @param isSeniorCitizen whether the customer is a senior citizen
     */
    void registerCustomer(String name, boolean isSeniorCitizen);

    /**
     * Registers a new customer with a membership card.
     *
     * @param name the customer's name
     * @param isSeniorCitizen whether the customer is a senior citizen
     * @param cardNumber the membership card number
     * @param expiryDate the membership card expiry date
     */
    void registerCustomerWithMembershipCard(String name,
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate);

    /**
     * Updates an existing customer's information.
     *
     * @param customerId the ID of the customer to update
     * @param name the updated customer name
     * @param isSeniorCitizen the updated senior citizen status
     */
    void updateCustomer(String customerId,
        String name, boolean isSeniorCitizen);

    /**
     * Removes a customer from the system.
     *
     * @param customerId the ID of the customer to remove
     */
    void removeCustomer(String customerId);

    /**
     * Adds a membership card to an existing customer.
     *
     * @param customerId the ID of the customer
     * @param cardNumber the membership card number
     * @param expiryDate the membership card expiry date
     */
    void addMembershipCard(String customerId, String cardNumber,
        LocalDate expiryDate);

    /**
     * Adds loyalty points to a customer's membership card.
     *
     * @param customerId the ID of the customer
     * @param points the number of points to add
     */
    void addPointsToCustomer(String customerId, int points);

    /**
     * Uses (redeems) loyalty points from a customer's membership card.
     *
     * @param customerId the ID of the customer
     * @param points the number of points to use
     */
    void useCustomerPoints(String customerId, int points);

    /**
     * Finds a customer by their unique identifier.
     *
     * @param customerId the customer ID to search for
     * @return an Optional containing the customer if found, empty otherwise
     */
    Optional<Customer> findById(String customerId);

    /**
     * Finds a customer by their membership card number.
     *
     * @param cardNumber the membership card number to search for
     * @return an Optional containing the customer if found, empty otherwise
     */
    Optional<Customer> findByMembershipCard(String cardNumber);

    /**
     * Retrieves all customers in the system.
     *
     * @return a list of all customers, empty list if no customers exist
     */
    List<Customer> findAll();

    /**
     * Saves all customer data to persistent storage.
     *
     * @return true if the save operation was successful, false otherwise
     */
    boolean saveCustomers();

    /**
     * Loads customer data from persistent storage.
     *
     * @return true if the load operation was successful, false otherwise
     */
    boolean loadCustomers();

    /**
     * Validates a customer ID format and existence.
     *
     * @param customerId the customer ID to validate
     * @throws IllegalArgumentException if the customer ID is invalid or not found
     */
    void validateCustomerId(String customerId) throws IllegalArgumentException;

    /**
     * Validates a customer name format.
     *
     * @param name the customer name to validate
     * @throws IllegalArgumentException if the name is invalid
     */
    void validateCustomerName(String name) throws IllegalArgumentException;
}
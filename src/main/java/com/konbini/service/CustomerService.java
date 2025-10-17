package com.konbini.service;

import com.konbini.model.Customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface defining the business logic layer for managing Customer data.
 * This service mediates between the presentation layer and the data repository,
 * handling customer registration, updates, membership card management, and loyalty points.
 */
public interface CustomerService {
    /**
     * Registers a new customer with basic details and assigns them a unique ID.
     *
     * @param name The full name of the customer.
     * @param isSeniorCitizen Flag indicating if the customer is a senior citizen for discount purposes.
     */
    void registerCustomer(String name, boolean isSeniorCitizen);

    /**
     * Registers a new customer and immediately associates them with a new membership card.
     *
     * @param name The full name of the customer.
     * @param isSeniorCitizen Flag indicating if the customer is a senior citizen.
     * @param cardNumber The unique identifier for the new membership card.
     * @param expiryDate The expiration date of the membership card.
     */
    void registerCustomerWithMembershipCard(String name,
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate);

    /**
     * Updates the name and senior citizen status of an existing customer.
     *
     * @param customerId The unique ID of the customer to update.
     * @param name The new full name of the customer.
     * @param isSeniorCitizen The new senior citizen status.
     */
    void updateCustomer(String customerId,
        String name, boolean isSeniorCitizen);

    /**
     * Removes a customer record from the system using their unique ID.
     *
     * @param customerId The unique ID of the customer to remove.
     */
    void removeCustomer(String customerId);

    /**
     * Associates an existing customer with a new membership card.
     *
     * @param customerId The unique ID of the customer to modify.
     * @param cardNumber The unique identifier for the new membership card.
     * @param expiryDate The expiration date of the membership card.
     */
    void addMembershipCard(String customerId, String cardNumber,
        LocalDate expiryDate);

    /**
     * Increments the loyalty points balance of a customer's membership card.
     *
     * @param customerId The unique ID of the customer.
     * @param points The number of points to add.
     */
    void addPointsToCustomer(String customerId, int points);

    /**
     * Decrements the loyalty points balance of a customer's membership card.
     * This method validates that the customer has sufficient points before deduction.
     *
     * @param customerId The unique ID of the customer.
     * @param points The number of points to use/deduct.
     */
    void useCustomerPoints(String customerId, int points);

    /**
     * Retrieves a customer record by their unique ID.
     *
     * @param customerId The ID of the customer to find.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    Optional<Customer> findById(String customerId);

    /**
     * Retrieves a customer record by their membership card number.
     *
     * @param cardNumber The membership card number to search by.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    Optional<Customer> findByMembershipCard(String cardNumber);

    /**
     * Retrieves all customer records in the system.
     *
     * @return A List of all Customer objects.
     */
    List<Customer> findAll();

    /**
     * Persists all customer data to the underlying storage mechanism (e.g., file, database).
     *
     * @return True if the save operation was successful, false otherwise.
     */
    boolean saveCustomers();

    /**
     * Loads all customer data from the underlying storage mechanism into memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    boolean loadCustomers();
}

package com.konbini.model.repository;

import com.konbini.model.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining the contract for data access operations related to the Customer model.
 * Implementations of this interface are responsible for persistent storage and retrieval
 * of Customer data, supporting standard CRUD operations and specific lookup methods.
 */
public interface CustomerRepository {
    /**
     * Persists a new customer record to the repository.
     *
     * @param customer The Customer object to be added.
     */
    void addCustomer(Customer customer);

    /**
     * Updates an existing customer record in the repository.
     * The customer's unique ID is used to locate the record to update.
     *
     * @param customer The Customer object with updated data.
     */
    void updateCustomer(Customer customer);

    /**
     * Removes a customer record from the repository based on their unique ID.
     *
     * @param customerId The ID of the customer to remove.
     */
    void removeCustomer(String customerId);

    /**
     * Finds and retrieves a customer by their unique identifier.
     *
     * @param customerId The ID of the customer to find.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    Optional<Customer> findById(String customerId);

    /**
     * Retrieves all customer records stored in the repository.
     *
     * @return A List of all Customer objects.
     */
    List<Customer> findAll();

    /**
     * Finds and retrieves a customer associated with a specific membership card number.
     *
     * @param cardNumber The membership card number to search by.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    Optional<Customer> findByMembershipCard(String cardNumber);

    /**
     * Persists the current state of the repository data to its storage mechanism (e.g., file, database).
     *
     * @return True if the save operation was successful, false otherwise.
     */
    boolean save();

    /**
     * Loads the repository data from its persistent storage mechanism into memory.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    boolean load();
}

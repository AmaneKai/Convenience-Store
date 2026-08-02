package com.konbini.domain.customer;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for customers.
 */
public interface CustomerRepository {

    /**
     * Persists a new customer.
     *
     * @param customer the customer to add
     */
    void add(Customer customer);

    /**
     * Updates an existing customer.
     *
     * @param customer the customer with updated values
     */
    void update(Customer customer);

    /**
     * Removes a customer by ID.
     *
     * @param customerId the customer ID
     */
    void remove(String customerId);

    /**
     * Finds a customer by ID.
     *
     * @param customerId the customer ID
     * @return an Optional containing the customer if found
     */
    Optional<Customer> findById(String customerId);

    /**
     * Returns all customers.
     *
     * @return all customers
     */
    List<Customer> findAll();

    /**
     * Finds a customer by membership card number.
     *
     * @param cardNumber the card number
     * @return an Optional containing the customer if found
     */
    Optional<Customer> findByMembershipCardNumber(String cardNumber);
}

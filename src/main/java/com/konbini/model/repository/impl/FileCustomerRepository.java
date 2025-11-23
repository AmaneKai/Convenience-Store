package com.konbini.model.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.konbini.model.Customer;
import com.konbini.model.repository.CustomerRepository;

/**
 * FileCustomerRepository provides a file-based implementation of the CustomerRepository interface.
 * This implementation stores customer data in a serialized file format and maintains an in-memory
 * cache of customer objects for fast access. It supports basic CRUD operations and membership card lookup.
 */
public class FileCustomerRepository implements CustomerRepository {
    /** In-memory cache of customers stored by customer ID */
    private final Map<String, Customer> customers;

    /** File path where customer data is persisted */
    private final String filePath;

    /**
     * Constructs a new FileCustomerRepository with the specified file path.
     * Initializes the in-memory customer cache.
     *
     * @param filePath the file path where customer data will be stored and loaded from
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public FileCustomerRepository(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.customers = new HashMap<>();
        this.filePath = filePath;
    }

    /**
     * Adds a new customer to the repository.
     * The customer is added to the in-memory cache but not automatically persisted to disk.
     *
     * @param customer the Customer object to add
     * @throws IllegalArgumentException if customer is null
     */
    @Override
    public void addCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        customers.put(customer.getId(), customer);
    }

    /**
     * Updates an existing customer in the repository.
     * Replaces the customer with the same ID in the in-memory cache.
     *
     * @param customer the Customer object with updated information
     * @throws IllegalArgumentException if customer is null or not found in repository
     */
    @Override
    public void updateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (!customers.containsKey(customer.getId())) {
            throw new IllegalArgumentException("Customer not found: " + customer.getId());
        }
        customers.put(customer.getId(), customer);
    }

    /**
     * Removes a customer from the repository by ID.
     * Removes the customer from the in-memory cache but not automatically from disk.
     *
     * @param customerId the ID of the customer to remove
     * @throws IllegalArgumentException if customerId is null or empty
     */
    @Override
    public void removeCustomer(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        customers.remove(customerId);
    }

    /**
     * Finds a customer by their ID.
     *
     * @param customerId the ID of the customer to find
     * @return an Optional containing the Customer if found, empty Optional otherwise
     */
    @Override
    public Optional<Customer> findById(String customerId) {
        Optional<Customer> temp = Optional.empty();

        if (customerId != null && !customerId.trim().isEmpty()) {
            temp = Optional.ofNullable(customers.get(customerId));
        }

        return temp;
    }

    /**
     * Retrieves all customers from the repository.
     *
     * @return a List containing all Customer objects in the repository
     */
    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Finds a customer by their membership card number.
     * Searches through all customers to find one with a matching membership card.
     *
     * @param cardNumber the membership card number to search for
     * @return an Optional containing the Customer if found with matching card, empty Optional otherwise
     */
    @Override
    public Optional<Customer> findByMembershipCard(String cardNumber) {
        Optional<Customer> temp = Optional.empty();

        if (cardNumber != null && !cardNumber.trim().isEmpty()) {
            temp = customers.values().stream()
                    .filter(customer -> customer.hasMembershipCard() &&
                            customer.getMembershipCard().getCardNumber().equals(cardNumber))
                    .findFirst();
        }

        return temp;
    }

    /**
     * Saves all customer data to the file system.
     * Serializes the current in-memory customer cache to the specified file path.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean save() {
        boolean temp = false;

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(customers.values()));
            temp = true;
        } catch (IOException e) {
            System.err.println("Error saving customer data to file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
        }

        return temp;
    }

    /**
     * Loads customer data from the file system.
     * Deserializes customer data from the specified file path into the in-memory cache.
     * If the file doesn't exist, the operation fails silently and returns false.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    public boolean load() {
        boolean temp = false;
        File file = new File(filePath);

        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                List<Customer> loadedCustomers = (List<Customer>) ois.readObject();

                if (loadedCustomers != null) {
                    customers.clear();
                    loadedCustomers.forEach(customer -> {
                        if (customer != null) {
                            customers.put(customer.getId(), customer);
                        }
                    });
                    temp = true;
                } else {
                    customers.clear();
                }
            } catch (IOException e) {
                System.err.println("Error reading customer data from file: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Customer class definition mismatch: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error loading customer data: " + filePath);
                System.err.println("Reason: " + e.getMessage());
            }
        }

        return temp;
    }
}
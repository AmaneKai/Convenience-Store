package com.konbini.model.repository.impl;

import com.konbini.model.Customer;
import com.konbini.model.repository.CustomerRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the CustomerRepository interface that uses file serialization
 * for persistence. Customer data is stored in memory using a Map for fast access
 * and saved/loaded from a file on the disk using Java's built-in serialization mechanism.
 */
public class FileCustomerRepository implements CustomerRepository {
    /**
     * In-memory storage for customer records, mapped by their unique ID.
     */
    private final Map<String, Customer> customers;
    /**
     * The file path used for saving and loading the serialized customer data.
     */
    private final String filePath;

    /**
     * Constructs a new FileCustomerRepository.
     * Initializes the in-memory map and sets the path for persistent storage.
     *
     * @param filePath The path to the file where customer data will be serialized.
     */
    public FileCustomerRepository(String filePath) {
        this.customers = new HashMap<>();
        this.filePath = filePath;
    }

    /**
     * Adds a new customer to the in-memory repository.
     *
     * @param customer The Customer object to be added.
     */
    @Override
    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    /**
     * Updates an existing customer record.
     * If the customer ID exists, the old record is replaced with the new Customer object.
     *
     * @param customer The Customer object with updated data.
     * @throws IllegalArgumentException if the customer ID does not exist in the repository.
     */
    @Override
    public void updateCustomer(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            customers.put(customer.getId(), customer);
        } else {
            throw new IllegalArgumentException
            ("Customer not found: " + customer.getId());
        }
    }

    /**
     * Removes a customer record from the repository based on their unique ID.
     *
     * @param customerId The ID of the customer to remove.
     */
    @Override
    public void removeCustomer(String customerId) {
        customers.remove(customerId);
    }

    /**
     * Finds and retrieves a customer by their unique identifier.
     *
     * @param customerId The ID of the customer to find.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }

    /**
     * Retrieves all customer records stored in the repository.
     *
     * @return A new List containing all Customer objects.
     */
    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    /**
     * Finds and retrieves a customer associated with a specific membership card number.
     *
     * @param cardNumber The membership card number to search by.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Customer> findByMembershipCard(String cardNumber) {
        return customers.values().stream()
                .filter(customer -> customer.hasMembershipCard() &&
                        customer.getMembershipCard()
                                .getCardNumber().equals(cardNumber))
                .findFirst();
    }

    /**
     * Serializes and persists the current in-memory customer data to the configured file path.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    @Override
    public boolean save() {
        try (ObjectOutputStream oos = new ObjectOutputStream
            (new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(customers.values()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the customer data from the serialized file into the in-memory repository.
     * If the file does not exist, the load operation fails silently, and the repository remains empty.
     *
     * @return True if the load operation was successful, false otherwise (including file not found).
     */

    @Override
    public boolean load() {
        File file = new File(filePath);

        if (!file.exists()) {
            // It's not an error if the data file doesn't exist on first run
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream
            (new FileInputStream(file))) {
            List<Customer> loadedCustomers = (List<Customer>) ois.readObject();
            customers.clear();
            loadedCustomers.forEach(customer -> customers
                .put(customer.getId(), customer));
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}

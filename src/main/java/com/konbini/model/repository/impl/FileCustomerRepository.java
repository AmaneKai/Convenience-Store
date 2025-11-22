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

public class FileCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customers;
    private final String filePath;

    public FileCustomerRepository(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.customers = new HashMap<>();
        this.filePath = filePath;
    }

    @Override
    public void addCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        customers.put(customer.getId(), customer);
    }

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

    @Override
    public void removeCustomer(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        customers.remove(customerId);
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        Optional<Customer> temp = Optional.empty();

        if (customerId != null && !customerId.trim().isEmpty()) {
            temp = Optional.ofNullable(customers.get(customerId));
        }

        return temp;
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

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

    @Override
    public boolean save() {
        boolean temp = false;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(new ArrayList<>(customers.values()));
            temp = true;
        } catch (IOException e) {
            System.err.println("Error saving customer data to file: " + filePath);
            System.err.println("Reason: " + e.getMessage());
        }

        return temp;
    }

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
package com.konbini.model.repository.impl;

import com.konbini.model.Customer;
import com.konbini.model.repository.CustomerRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customers;
    private final String filePath;
    
    public FileCustomerRepository(String filePath) {
        this.customers = new HashMap<>();
        this.filePath = filePath;
    }
    
    @Override
    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }
    
    @Override
    public void updateCustomer(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            customers.put(customer.getId(), customer);
        } else {
            throw new IllegalArgumentException
            ("Customer not found: " + customer.getId());
        }
    }
    
    @Override
    public void removeCustomer(String customerId) {
        customers.remove(customerId);
    }
    
    @Override
    public Optional<Customer> findById(String customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }
    
    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }
    
    @Override
    public Optional<Customer> findByMembershipCard(String cardNumber) {
        return customers.values().stream()
                .filter(customer -> customer.hasMembershipCard() && 
                        customer.getMembershipCard()
                                .getCardNumber().equals(cardNumber))
                .findFirst();
    }
    
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
    
    @Override
    public boolean load() {
        File file = new File(filePath);
        
        if (!file.exists()) {
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

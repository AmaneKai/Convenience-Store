package com.konbini.controller;

import com.konbini.model.Customer;
import com.konbini.service.CustomerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    public Optional<Customer> getCustomerById(String customerId) {
        return customerService.findById(customerId);
    }

    public Optional<Customer> getCustomerByMembershipCard(String cardNumber) {
        return customerService.findByMembershipCard(cardNumber);
    }

    public void registerCustomer(String name, boolean isSeniorCitizen) {
        customerService.registerCustomer(name, isSeniorCitizen);
    }

    public void registerCustomerWithMembershipCard(String name,
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate) {
        customerService.registerCustomerWithMembershipCard(name,
            isSeniorCitizen, cardNumber, expiryDate);
    }

    public void updateCustomer(String customerId, String name,
        boolean isSeniorCitizen) {
        customerService.updateCustomer(customerId, name, isSeniorCitizen);
    }

    public void removeCustomer(String customerId) {
        customerService.removeCustomer(customerId);
    }

    public void addMembershipCard(String customerId, String cardNumber,
        LocalDate expiryDate) {
        customerService.addMembershipCard(customerId, cardNumber, expiryDate);
    }

    public void addPointsToCustomer(String customerId, int points) {
        customerService.addPointsToCustomer(customerId, points);
    }

    public void useCustomerPoints(String customerId, int points) {
        customerService.useCustomerPoints(customerId, points);
    }

    public boolean saveData() {
        return customerService.saveCustomers();
    }

    public boolean loadData() {
        return customerService.loadCustomers();
    }
}

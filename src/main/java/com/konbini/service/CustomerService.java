package com.konbini.service;

import com.konbini.model.Customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    void registerCustomer(String name, boolean isSeniorCitizen);
    void registerCustomerWithMembershipCard(String name, 
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate);
    void updateCustomer(String customerId, 
        String name, boolean isSeniorCitizen);
    void removeCustomer(String customerId);
    void addMembershipCard(String customerId, String cardNumber, 
        LocalDate expiryDate);
    void addPointsToCustomer(String customerId, int points);
    void useCustomerPoints(String customerId, int points);
    Optional<Customer> findById(String customerId);
    Optional<Customer> findByMembershipCard(String cardNumber);
    List<Customer> findAll();
    boolean saveCustomers();
    boolean loadCustomers();
}

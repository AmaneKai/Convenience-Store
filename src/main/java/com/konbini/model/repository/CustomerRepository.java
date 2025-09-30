package com.konbini.model.repository;

import com.konbini.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    void addCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void removeCustomer(String customerId);
    Optional<Customer> findById(String customerId);
    List<Customer> findAll();
    Optional<Customer> findByMembershipCard(String cardNumber);
    boolean save();
    boolean load();
}

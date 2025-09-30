package com.konbini.service.impl;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;
import com.konbini.model.repository.CustomerRepository;
import com.konbini.service.CustomerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public void registerCustomer(String name, boolean isSeniorCitizen) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException
            ("Customer name cannot be empty");
        }
        
        Customer customer = new Customer(name, isSeniorCitizen);
        customerRepository.addCustomer(customer);
    }
    
    @Override
    public void registerCustomerWithMembershipCard(String name, 
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException
            ("Customer name cannot be empty");
        }
        
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException
            ("Card number cannot be empty");
        }
        
        if (expiryDate == null || expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException
            ("Expiry date cannot be null or in the past");
        }
        
        Optional<Customer> existingCustomer = customerRepository
            .findByMembershipCard(cardNumber);
        if (existingCustomer.isPresent()) {
            throw new IllegalArgumentException
            ("Membership card already exists");
        }
        
        Customer customer = new Customer(name, isSeniorCitizen);
        MembershipCard membershipCard = new MembershipCard
            (cardNumber, expiryDate);
        customer.setMembershipCard(membershipCard);
        
        customerRepository.addCustomer(customer);
    }
    
    @Override
    public void updateCustomer(String customerId, 
        String name, boolean isSeniorCitizen) {
        Optional<Customer> optionalCustomer = customerRepository
            .findById(customerId);
        
        if (!optionalCustomer.isPresent()) {
            throw new IllegalArgumentException
            ("Customer not found: " + customerId);
        }
        
        Customer customer = optionalCustomer.get();
        
        if (name != null && !name.isEmpty()) {
            customer.setName(name);
        }
        
        customer.setSeniorCitizen(isSeniorCitizen);
        
        customerRepository.updateCustomer(customer);
    }
    
    @Override
    public void removeCustomer(String customerId) {
        Optional<Customer> optionalCustomer = customerRepository
            .findById(customerId);
        
        if (!optionalCustomer.isPresent()) {
            throw new IllegalArgumentException
                ("Customer not found: " + customerId);
        }
        
        customerRepository.removeCustomer(customerId);
    }
    
    @Override
    public void addMembershipCard(String customerId, String cardNumber, 
            LocalDate expiryDate) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be empty");
        }
        
        if (expiryDate == null || expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException
            ("Expiry date cannot be null or in the past");
        }
        
        Optional<Customer> existingCustomer = customerRepository
            .findByMembershipCard(cardNumber);
        if (existingCustomer.isPresent()) {
            throw new IllegalArgumentException
            ("Membership card already exists");
        }
        
        Optional<Customer> optionalCustomer = customerRepository
            .findById(customerId);
        
        if (!optionalCustomer.isPresent()) {
            throw new IllegalArgumentException
            ("Customer not found: " + customerId);
        }
        
        Customer customer = optionalCustomer.get();
        
        if (customer.hasMembershipCard()) {
            throw new IllegalArgumentException
            ("Customer already has a membership card");
        }
        
        MembershipCard membershipCard = new MembershipCard
            (cardNumber, expiryDate);
        customer.setMembershipCard(membershipCard);
        
        customerRepository.updateCustomer(customer);
    }
    
    @Override
    public void addPointsToCustomer(String customerId, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException
            ("Points to add must be positive");
        }
        
        Optional<Customer> optionalCustomer = customerRepository
            .findById(customerId);
        
        if (!optionalCustomer.isPresent()) {
            throw new IllegalArgumentException
            ("Customer not found: " + customerId);
        }
        
        Customer customer = optionalCustomer.get();
        
        if (!customer.hasMembershipCard()) {
            throw new IllegalArgumentException
            ("Customer does not have a membership card");
        }
        
        MembershipCard membershipCard = customer.getMembershipCard();
        
        if (membershipCard.isExpired()) {
            throw new IllegalArgumentException
            ("Membership card is expired");
        }
        
        membershipCard.addPoints(points);
        customerRepository.updateCustomer(customer);
    }
    
    @Override
    public void useCustomerPoints(String customerId, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException
            ("Points to use must be positive");
        }
        
        Optional<Customer> optionalCustomer = customerRepository
            .findById(customerId);
        
        if (!optionalCustomer.isPresent()) {
            throw new IllegalArgumentException
            ("Customer not found: " + customerId);
        }
        
        Customer customer = optionalCustomer.get();
        
        if (!customer.hasMembershipCard()) {
            throw new IllegalArgumentException
            ("Customer does not have a membership card");
        }
        
        MembershipCard membershipCard = customer.getMembershipCard();
        
        if (membershipCard.isExpired()) {
            throw new IllegalArgumentException
            ("Membership card is expired");
        }
        
        if (membershipCard.getPoints() < points) {
            throw new IllegalArgumentException
            ("Insufficient points");
        }
        
        membershipCard.deductPoints(points);
        customerRepository.updateCustomer(customer);
    }
    
    @Override
    public Optional<Customer> findById(String customerId) {
        return customerRepository.findById(customerId);
    }
    
    @Override
    public Optional<Customer> findByMembershipCard(String cardNumber) {
        return customerRepository.findByMembershipCard(cardNumber);
    }
    
    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    
    @Override
    public boolean saveCustomers() {
        return customerRepository.save();
    }
    
    @Override
    public boolean loadCustomers() {
        return customerRepository.load();
    }
}

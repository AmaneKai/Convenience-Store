package com.konbini.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;
import com.konbini.model.repository.CustomerRepository;
import com.konbini.service.CustomerService;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        if (customerRepository == null) {
            throw new IllegalArgumentException("Customer repository cannot be null");
        }
        this.customerRepository = customerRepository;
    }

    @Override
    public void registerCustomer(String name, boolean isSeniorCitizen) {
        validateName(name);
        Customer customer = new Customer(name, isSeniorCitizen);
        customerRepository.addCustomer(customer);
    }

    @Override
    public void registerCustomerWithMembershipCard(String name,
        boolean isSeniorCitizen, String cardNumber, LocalDate expiryDate) {
        
        validateName(name);
        validateCardNumber(cardNumber);
        validateExpiryDate(expiryDate);

        if (customerRepository.findByMembershipCard(cardNumber).isPresent()) {
            throw new IllegalArgumentException("Membership card already exists");
        }

        Customer customer = new Customer(name, isSeniorCitizen);
        MembershipCard membershipCard = new MembershipCard(cardNumber, expiryDate);
        customer.setMembershipCard(membershipCard);

        customerRepository.addCustomer(customer);
    }

    @Override
    public void updateCustomer(String customerId, String name, boolean isSeniorCitizen) {
        Customer customer = getCustomerOrThrow(customerId);

        if (name != null && !name.isEmpty()) {
            validateName(name);
            customer.setName(name);
        }

        customer.setSeniorCitizen(isSeniorCitizen);
        customerRepository.updateCustomer(customer);
    }

    @Override
    public void removeCustomer(String customerId) {
        getCustomerOrThrow(customerId); // Validate exists
        customerRepository.removeCustomer(customerId);
    }

    @Override
    public void addMembershipCard(String customerId, String cardNumber, LocalDate expiryDate) {
        validateCardNumber(cardNumber);
        validateExpiryDate(expiryDate);

        if (customerRepository.findByMembershipCard(cardNumber).isPresent()) {
            throw new IllegalArgumentException("Membership card already exists");
        }

        Customer customer = getCustomerOrThrow(customerId);

        if (customer.hasMembershipCard()) {
            throw new IllegalArgumentException("Customer already has a membership card");
        }

        MembershipCard membershipCard = new MembershipCard(cardNumber, expiryDate);
        customer.setMembershipCard(membershipCard);

        customerRepository.updateCustomer(customer);
    }

    @Override
    public void addPointsToCustomer(String customerId, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        }

        Customer customer = getCustomerOrThrow(customerId);

        if (!customer.hasMembershipCard()) {
            throw new IllegalArgumentException("Customer does not have a membership card");
        }

        MembershipCard card = customer.getMembershipCard();
        card.addPoints(points); // Will validate expiry
        customerRepository.updateCustomer(customer);
    }

    @Override
    public void useCustomerPoints(String customerId, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to use must be positive");
        }

        Customer customer = getCustomerOrThrow(customerId);

        if (!customer.hasMembershipCard()) {
            throw new IllegalArgumentException("Customer does not have a membership card");
        }

        MembershipCard card = customer.getMembershipCard();
        card.deductPoints(points); 
        customerRepository.updateCustomer(customer);
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return Optional.empty();
        }
        return customerRepository.findById(customerId);
    }

    @Override
    public Optional<Customer> findByMembershipCard(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return Optional.empty();
        }
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

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be empty");
        }
    }

    private void validateExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null || expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be null or in the past");
        }
    }

    private Customer getCustomerOrThrow(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
    }

    @Override
    public void validateCustomerId(String customerId) throws IllegalArgumentException {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
    }

    @Override
    public void validateCustomerName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
    }
}
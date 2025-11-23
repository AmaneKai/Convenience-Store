package com.konbini.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;
import com.konbini.model.repository.CustomerRepository;
import com.konbini.service.CustomerService;

/**
 * CustomerServiceImpl provides business logic implementation for customer management operations.
 * This service handles customer registration, membership card management, points system,
 * and validation of customer data according to business rules.
 */
public class CustomerServiceImpl implements CustomerService {
    /** Repository for customer data persistence operations */
    private final CustomerRepository customerRepository;

    /**
     * Constructs a new CustomerServiceImpl with the specified customer repository.
     *
     * @param customerRepository the CustomerRepository for data access operations
     * @throws IllegalArgumentException if customerRepository is null
     */
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        if (customerRepository == null) {
            throw new IllegalArgumentException("Customer repository cannot be null");
        }
        this.customerRepository = customerRepository;
    }

    /**
     * Registers a new customer without a membership card.
     *
     * @param name the name of the customer to register
     * @param isSeniorCitizen true if the customer is a senior citizen, false otherwise
     * @throws IllegalArgumentException if name is null or empty
     */
    @Override
    public void registerCustomer(String name, boolean isSeniorCitizen) {
        validateName(name);
        Customer customer = new Customer(name, isSeniorCitizen);
        customerRepository.addCustomer(customer);
    }

    /**
     * Registers a new customer with a membership card.
     * Validates that the card number is unique across all customers.
     *
     * @param name the name of the customer to register
     * @param isSeniorCitizen true if the customer is a senior citizen, false otherwise
     * @param cardNumber the membership card number
     * @param expiryDate the expiration date of the membership card
     * @throws IllegalArgumentException if any parameter is invalid or card number already exists
     */
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

    /**
     * Updates an existing customer's information.
     * Only updates the name if a non-null, non-empty value is provided.
     *
     * @param customerId the ID of the customer to update
     * @param name the new name for the customer (optional, can be null to keep existing)
     * @param isSeniorCitizen the new senior citizen status
     * @throws IllegalArgumentException if customerId is invalid or customer not found
     */
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

    /**
     * Removes a customer from the system.
     *
     * @param customerId the ID of the customer to remove
     * @throws IllegalArgumentException if customerId is invalid or customer not found
     */
    @Override
    public void removeCustomer(String customerId) {
        getCustomerOrThrow(customerId); // Validate exists
        customerRepository.removeCustomer(customerId);
    }

    /**
     * Adds a membership card to an existing customer.
     * Validates that the card number is unique and the customer doesn't already have a card.
     *
     * @param customerId the ID of the customer to add the card to
     * @param cardNumber the membership card number
     * @param expiryDate the expiration date of the membership card
     * @throws IllegalArgumentException if any parameter is invalid, card exists, or customer already has card
     */
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

    /**
     * Adds points to a customer's membership card.
     * Validates that the customer has a membership card and points are positive.
     *
     * @param customerId the ID of the customer to add points to
     * @param points the number of points to add (must be positive)
     * @throws IllegalArgumentException if points are not positive, customer not found, or no membership card
     */
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

    /**
     * Uses points from a customer's membership card.
     * Validates that the customer has sufficient points and a valid membership card.
     *
     * @param customerId the ID of the customer to use points from
     * @param points the number of points to use (must be positive)
     * @throws IllegalArgumentException if points are not positive, insufficient points, or no membership card
     */
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
            temp = customerRepository.findById(customerId);
        }

        return temp;
    }

    /**
     * Finds a customer by their membership card number.
     *
     * @param cardNumber the membership card number to search for
     * @return an Optional containing the Customer if found with matching card, empty Optional otherwise
     */
    @Override
    public Optional<Customer> findByMembershipCard(String cardNumber) {
        Optional<Customer> temp = Optional.empty();

        if (cardNumber != null && !cardNumber.trim().isEmpty()) {
            temp = customerRepository.findByMembershipCard(cardNumber);
        }

        return temp;
    }

    /**
     * Retrieves all customers from the system.
     *
     * @return a List containing all Customer objects
     */
    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * Saves all customer data to persistent storage.
     *
     * @return true if the save operation was successful, false otherwise
     */
    @Override
    public boolean saveCustomers() {
        return customerRepository.save();
    }

    /**
     * Loads all customer data from persistent storage.
     *
     * @return true if the load operation was successful, false otherwise
     */
    @Override
    public boolean loadCustomers() {
        return customerRepository.load();
    }

    /**
     * Validates that a customer ID is not null or empty.
     *
     * @param customerId the customer ID to validate
     * @throws IllegalArgumentException if customerId is null or empty
     */
    @Override
    public void validateCustomerId(String customerId) throws IllegalArgumentException {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
    }

    /**
     * Validates that a customer name is not null or empty.
     *
     * @param name the customer name to validate
     * @throws IllegalArgumentException if name is null or empty
     */
    @Override
    public void validateCustomerName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
    }

    /**
     * Validates that a customer name is not null or empty.
     *
     * @param name the name to validate
     * @throws IllegalArgumentException if name is null or empty
     */
    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
    }

    /**
     * Validates that a card number is not null or empty.
     *
     * @param cardNumber the card number to validate
     * @throws IllegalArgumentException if cardNumber is null or empty
     */
    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be empty");
        }
    }

    /**
     * Validates that an expiry date is not null and not in the past.
     *
     * @param expiryDate the expiry date to validate
     * @throws IllegalArgumentException if expiryDate is null or in the past
     */
    private void validateExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null || expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be null or in the past");
        }
    }

    /**
     * Retrieves a customer by ID or throws an exception if not found.
     *
     * @param customerId the ID of the customer to retrieve
     * @return the Customer object if found
     * @throws IllegalArgumentException if customerId is invalid or customer not found
     */
    private Customer getCustomerOrThrow(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
    }
}
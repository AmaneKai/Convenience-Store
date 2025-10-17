package com.konbini.service.impl;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;
import com.konbini.model.repository.CustomerRepository;
import com.konbini.service.CustomerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of the CustomerService interface.
 * This class provides the business logic for managing customer records,
 * including registration, updates, and loyalty membership card operations,
 * primarily delegating data persistence to the CustomerRepository.
 */
public class CustomerServiceImpl implements CustomerService {
    /**
     * The data access object responsible for persistent storage and retrieval of Customer data.
     */
    private final CustomerRepository customerRepository;

    /**
     * Constructs a CustomerServiceImpl with a dependency on a CustomerRepository.
     *
     * @param customerRepository The repository used for data persistence.
     */
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Registers a new customer with basic details.
     *
     * @param name The full name of the customer.
     * @param isSeniorCitizen Flag indicating if the customer is a senior citizen.
     * @throws IllegalArgumentException if the customer name is empty or null.
     */
    @Override
    public void registerCustomer(String name, boolean isSeniorCitizen) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException
            ("Customer name cannot be empty");
        }

        Customer customer = new Customer(name, isSeniorCitizen);
        customerRepository.addCustomer(customer);
    }

    /**
     * Registers a new customer and immediately associates them with a new membership card.
     * Performs validation on the provided data and checks for existing card numbers.
     *
     * @param name The full name of the customer.
     * @param isSeniorCitizen Flag indicating if the customer is a senior citizen.
     * @param cardNumber The unique identifier for the new membership card.
     * @param expiryDate The expiration date of the membership card.
     * @throws IllegalArgumentException if any required field is invalid or the card number is already in use.
     */
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

    /**
     * Updates the name and senior citizen status of an existing customer.
     *
     * @param customerId The unique ID of the customer to update.
     * @param name The new full name of the customer (can be null or empty to keep current name).
     * @param isSeniorCitizen The new senior citizen status.
     * @throws IllegalArgumentException if the customer ID is not found.
     */
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

    /**
     * Removes a customer record from the system.
     *
     * @param customerId The unique ID of the customer to remove.
     * @throws IllegalArgumentException if the customer ID is not found.
     */
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

    /**
     * Associates an existing customer with a new membership card.
     * Performs validation to ensure the customer does not already have a card and the new card number is unique.
     *
     * @param customerId The unique ID of the customer to modify.
     * @param cardNumber The unique identifier for the new membership card.
     * @param expiryDate The expiration date of the membership card.
     * @throws IllegalArgumentException if the customer is not found, already has a card, or the new card number is invalid or in use.
     */
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

    /**
     * Increments the loyalty points balance of a customer's membership card.
     *
     * @param customerId The unique ID of the customer.
     * @param points The number of points to add.
     * @throws IllegalArgumentException if the customer is not found, does not have a card, the card is expired, or points are non-positive.
     */
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

    /**
     * Decrements the loyalty points balance of a customer's membership card.
     *
     * @param customerId The unique ID of the customer.
     * @param points The number of points to use/deduct.
     * @throws IllegalArgumentException if the customer is not found, does not have a card, the card is expired, points are non-positive, or points are insufficient.
     */
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

    /**
     * Retrieves a customer record by their unique ID.
     *
     * @param customerId The ID of the customer to find.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Customer> findById(String customerId) {
        return customerRepository.findById(customerId);
    }

    /**
     * Retrieves a customer record by their membership card number.
     *
     * @param cardNumber The membership card number to search by.
     * @return An Optional containing the Customer if found, or an empty Optional otherwise.
     */
    @Override
    public Optional<Customer> findByMembershipCard(String cardNumber) {
        return customerRepository.findByMembershipCard(cardNumber);
    }

    /**
     * Retrieves all customer records in the system.
     *
     * @return A List of all Customer objects.
     */
    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * Persists all customer data to the underlying storage mechanism.
     * Delegates the save operation to the repository.
     *
     * @return True if the save operation was successful, false otherwise.
     */
    @Override
    public boolean saveCustomers() {
        return customerRepository.save();
    }

    /**
     * Loads all customer data from the underlying storage mechanism into memory.
     * Delegates the load operation to the repository.
     *
     * @return True if the load operation was successful, false otherwise.
     */
    @Override
    public boolean loadCustomers() {
        return customerRepository.load();
    }
}

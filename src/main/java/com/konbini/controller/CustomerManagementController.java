package com.konbini.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.dto.CustomerDTO;
import com.konbini.model.Customer;
import com.konbini.view.StoreView;

/**
 * Controller for customer management operations.
 * FULLY EVENT-DRIVEN - each method is a single action called directly by GUI buttons.
 * No menu loops, no blocking operations.
 */
public class CustomerManagementController {
    private final StoreView view;
    private final CustomerController customerController;

    public CustomerManagementController(StoreView view, CustomerController customerController) {
        this.view = view;
        this.customerController = customerController;
    }

    // ==================== Single-Action Methods ====================
    // Each method is called directly by a button - no loops!

    public void handleViewAllCustomers() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            view.displayCustomers(customers.stream().map(CustomerDTO::fromModel).collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view customers: " + e.getMessage());
        }
    }

    public void handleViewCustomerDetails() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            view.displayCustomers(customers.stream().map(CustomerDTO::fromModel).collect(java.util.stream.Collectors.toList()));
            String customerId = view.getStringInput("Enter customer ID: ");
            if (customerId == null || customerId.trim().isEmpty()) return;
            
            Optional<Customer> customer = customerController.getCustomerById(customerId);
            if (customer.isPresent()) {
                view.displayCustomer(CustomerDTO.fromModel(customer.get()));
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view customer: " + e.getMessage());
        }
    }

    public void handleRegisterCustomer() {
        try {
            String name = view.getStringInput("Enter customer name: ");
            if (name == null || name.trim().isEmpty()) return;
            
            boolean isSeniorCitizen = view.getBooleanInput("Is senior citizen?");
            customerController.registerCustomer(name, isSeniorCitizen);
            view.displaySuccessMessage("Customer registered successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed to register customer: " + e.getMessage());
        }
    }

    public void handleRegisterWithMembership() {
        try {
            String name = view.getStringInput("Enter customer name: ");
            if (name == null || name.trim().isEmpty()) return;
            
            boolean isSeniorCitizen = view.getBooleanInput("Is senior citizen?");
            String cardNumber = view.getStringInput("Enter card number: ");
            if (cardNumber == null || cardNumber.trim().isEmpty()) return;
            
            LocalDate expiryDate = view.getDateInput("Enter expiry date (YYYY-MM-DD): ");
            if (expiryDate == null) return;

            customerController.registerCustomerWithMembershipCard(name, isSeniorCitizen, cardNumber, expiryDate);
            view.displaySuccessMessage("Customer registered with membership card.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }

    public void handleUpdateCustomer() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            view.displayCustomers(customers.stream().map(CustomerDTO::fromModel).collect(java.util.stream.Collectors.toList()));
            String customerId = view.getStringInput("Enter customer ID to update: ");
            if (customerId == null || customerId.trim().isEmpty()) return;
            
            Optional<Customer> customer = customerController.getCustomerById(customerId);
            if (!customer.isPresent()) {
                view.displayErrorMessage("Customer not found.");
                return;
            }

            String name = view.getStringInput("Enter new name (leave empty to keep): ");
            name = (name == null || name.isEmpty()) ? customer.get().getName() : name;
            
            boolean isSeniorCitizen = view.getBooleanInput("Is senior citizen?");
            
            customerController.updateCustomer(customerId, name, isSeniorCitizen);
            view.displaySuccessMessage("Customer updated successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed to update: " + e.getMessage());
        }
    }

    public void handleRemoveCustomer() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            view.displayCustomers(customers.stream().map(CustomerDTO::fromModel).collect(java.util.stream.Collectors.toList()));
            String customerId = view.getStringInput("Enter customer ID to remove: ");
            if (customerId == null || customerId.trim().isEmpty()) return;
            
            if (!view.getBooleanInput("Are you sure?")) {
                view.displayInfoMessage("Cancelled.");
                return;
            }

            customerController.removeCustomer(customerId);
            view.displaySuccessMessage("Customer removed successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed to remove: " + e.getMessage());
        }
    }

    public void handleAddMembershipCard() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            view.displayCustomers(customers.stream().map(CustomerDTO::fromModel).collect(java.util.stream.Collectors.toList()));
            String customerId = view.getStringInput("Enter customer ID: ");
            if (customerId == null || customerId.trim().isEmpty()) return;
            
            Optional<Customer> customer = customerController.getCustomerById(customerId);
            if (!customer.isPresent()) {
                view.displayErrorMessage("Customer not found.");
                return;
            }

            if (customer.get().hasMembershipCard()) {
                view.displayErrorMessage("Customer already has a membership card.");
                return;
            }

            String cardNumber = view.getStringInput("Enter card number: ");
            if (cardNumber == null || cardNumber.trim().isEmpty()) return;
            
            LocalDate expiryDate = view.getDateInput("Enter expiry date (YYYY-MM-DD): ");
            if (expiryDate == null) return;

            customerController.addMembershipCard(customerId, cardNumber, expiryDate);
            view.displaySuccessMessage("Membership card added successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed: " + e.getMessage());
        }
    }
}
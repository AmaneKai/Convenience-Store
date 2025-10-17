package com.konbini.controller;

import com.konbini.model.Customer;
import com.konbini.view.StoreView;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller class responsible for managing the customer data lifecycle within the application.
 * It handles the flow of control for all customer administration tasks, coordinating
 * user input and output through the StoreView, and delegating core logic to the
 * CustomerController.
 */
public class CustomerManagementController {
    /**
     * The view component responsible for displaying menus, receiving user input,
     * and showing results or error messages.
     */
    private final StoreView view;
    /**
     * The controller responsible for direct manipulation and persistence of Customer data.
     */
    private final CustomerController customerController;

    /**
     * Constructs the CustomerManagementController, injecting the necessary dependencies.
     *
     * @param view The user interface component.
     * @param customerController The controller for customer business logic.
     */
    public CustomerManagementController(StoreView view,
            CustomerController customerController) {
        this.view = view;
        this.customerController = customerController;
    }

    /**
     * Runs the main loop for customer management, displaying the menu and
     * executing actions based on user choice until the user selects to exit.
     */
    public void handleCustomerManagement() {
        boolean backToMain = false;

        while (!backToMain) {
            view.displayCustomerMenu();
            int choice = view.getCustomerMenuChoice();

            switch (choice) {
                case 1: viewAllCustomers(); break;
                case 2: viewCustomerDetails(); break;
                case 3: registerCustomer(); break;
                case 4: registerCustomerWithMembership(); break;
                case 5: updateCustomer(); break;
                case 6: removeCustomer(); break;
                case 7: addMembershipCard(); break;
                case 0: backToMain = true; break;
                default:
                    view.displayErrorMessage
                        ("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Retrieves and displays a list of all customers currently registered.
     */
    private void viewAllCustomers() {
        view.displayCustomers(customerController.getAllCustomers());
    }

    /**
     * Prompts the user for a customer ID and displays the detailed information
     * for the corresponding Customer if found.
     */
    private void viewCustomerDetails() {
        try {
            view.displayCustomers(customerController.getAllCustomers());
            String customerId = view
                .getStringInput("Enter customer ID to view details: ");
            Optional<Customer> customer = customerController
                .getCustomerById(customerId);

            if (customer.isPresent()) {
                view.displayCustomer(customer.get());
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view customer details: "
                + e.getMessage());
        }
    }

    /**
     * Prompts the user for a name and senior citizen status to register a new
     * customer without an initial membership card.
     */
    private void registerCustomer() {
        try {
            String name = view.getStringInput("Enter customer name: ");
            boolean isSeniorCitizen = view
                .getBooleanInput("Is the customer a senior citizen?");

            customerController.registerCustomer(name, isSeniorCitizen);
            view.displaySuccessMessage("Customer registered successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed to register customer: "
                + e.getMessage());
        }
    }

    /**
     * Prompts the user for customer details and membership card information
     * to register a new customer with an associated membership card.
     */
    private void registerCustomerWithMembership() {
        try {
            String name = view.getStringInput("Enter customer name: ");
            boolean isSeniorCitizen = view.getBooleanInput
                ("Is the customer a senior citizen?");
            String cardNumber = view.getStringInput
                ("Enter membership card number: ");
            LocalDate expiryDate = view.getDateInput
                ("Enter membership card expiry date: ");

            customerController.registerCustomerWithMembershipCard(name,
                isSeniorCitizen, cardNumber, expiryDate);
            view.displaySuccessMessage
                ("Customer registered with membership card successfully.");
        } catch (Exception e) {
            view.displayErrorMessage
            ("Failed to register customer with membership card: "
                    + e.getMessage());
        }
    }

    /**
     * Prompts the user for a customer ID and allows updating the customer's
     * name and senior citizen status.
     */
    private void updateCustomer() {
        try {
            view.displayCustomers(customerController.getAllCustomers());
            String customerId = view.getStringInput
                ("Enter customer ID to update: ");
            Optional<Customer> optionalCustomer = customerController
                .getCustomerById(customerId);

            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                view.displayCustomer(customer);

                // Allows user to keep the current name by leaving input empty
                String name = view.getStringInput(
                    "Enter new customer name (leave empty to keep current): ");
                name = name.isEmpty() ? customer.getName() : name;

                boolean isSeniorCitizen = view.getBooleanInput(
                    "Is the customer a senior citizen? (current: " +
                    (customer.isSeniorCitizen() ? "Yes" : "No") + ")");

                customerController.updateCustomer
                    (customerId, name, isSeniorCitizen);
                view.displaySuccessMessage("Customer updated successfully.");
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to update customer: "
                + e.getMessage());
        }
    }

    /**
     * Prompts the user for a customer ID and confirms before removing
     * the customer from the system.
     */
    private void removeCustomer() {
        try {
            view.displayCustomers(customerController
                    .getAllCustomers());
            String customerId = view.getStringInput
                ("Enter customer ID to remove: ");

            if (view.getBooleanInput
                ("Are you sure you want to remove this customer?")) {
                customerController.removeCustomer(customerId);
                view.displaySuccessMessage("Customer removed successfully.");
            } else {
                view.displayInfoMessage("Customer removal cancelled.");
            }
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to remove customer: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a customer ID and new card details to add a
     * membership card to an existing customer who doesn't already have one.
     */
    private void addMembershipCard() {
        try {
            view.displayCustomers(customerController.getAllCustomers());
            String customerId = view.getStringInput
                ("Enter customer ID to add membership card: ");
            Optional<Customer> optionalCustomer = customerController
                .getCustomerById(customerId);

            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();

                if (customer.hasMembershipCard()) {
                    view.displayErrorMessage
                        ("Customer already has a membership card.");
                    return;
                }

                String cardNumber = view.getStringInput
                    ("Enter membership card number: ");
                LocalDate expiryDate = view.getDateInput
                    ("Enter membership card expiry date: ");

                customerController.addMembershipCard
                    (customerId, cardNumber, expiryDate);
                view.displaySuccessMessage
                    ("Membership card added successfully.");
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to add membership card: "
                + e.getMessage());
        }
    }
}

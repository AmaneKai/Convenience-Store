package com.konbini.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.konbini.dto.CustomerDTO;
import com.konbini.model.Customer;
import com.konbini.service.CustomerService;
import com.konbini.view.*;

/**
 * Controller for managing customer operations including registration, updates,
 * membership management, and customer data display. Coordinates between the view
 * and customer service layer.
 */
public class CustomerManagementController {
    private final CustomerView view;
    private final CustomerController customerController;
    private final CustomerService customerService;

    /**
     * Constructs a CustomerManagementController with all required dependencies.
     *
     * @param view the store view for user interface interactions
     * @param customerController controller for customer operations
     * @param customerService service for customer validation and business logic
     * @throws IllegalArgumentException if any dependency is null
     */
    public CustomerManagementController(
            CustomerView view,
            CustomerController customerController,
            CustomerService customerService) {
        if (view == null || customerController == null || customerService == null) {
            throw new IllegalArgumentException("All dependencies must be provided");
        }
        this.view = view;
        this.customerController = customerController;
        this.customerService = customerService;
    }

    // ==================== PUBLIC HANDLERS ====================

    /**
     * Handles displaying all customers in the system.
     * Catches and handles any exceptions during the loading process.
     */
    public void handleViewAllCustomers() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            displayCustomerList(customers);
        } catch (Exception e) {
            handleGenericException(e, "loading customers", "Failed to load customers. Please try again.");
        }
    }

    /**
     * Handles viewing detailed information for a specific customer.
     * Prompts for customer ID and displays customer details if found.
     */
    public void handleViewCustomerDetails() {
        try {
            Optional<String> customerId = promptForCustomerId("view details");
            customerId.ifPresent(this::showCustomerDetails);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing customer details");
        } catch (Exception e) {
            handleGenericException(e, "viewing customer details", "Failed to view customer details. Please try again.");
        }
    }

    /**
     * Handles registering a new customer with basic information.
     * Prompts for customer name and senior citizen status.
     */
    public void handleRegisterCustomer() {
        try {
            String name = view.getStringInput("Enter customer name: ");

            if (name != null && !name.trim().isEmpty()) {
                registerNewCustomer(name.trim());
            } else {
                view.displayErrorMessage("Customer name cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "registering customer");
        } catch (Exception e) {
            handleGenericException(e, "registering customer", "Failed to register customer. Please try again.");
        }
    }

    /**
     * Handles registering a new customer with a membership card.
     * Prompts for customer name, senior citizen status, card number, and expiry date.
     */
    public void handleRegisterWithMembership() {
        try {
            String name = view.getStringInput("Enter customer name: ");

            if (name != null && !name.trim().isEmpty()) {
                registerCustomerWithCard(name.trim());
            } else {
                view.displayErrorMessage("Customer name cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "registering customer with membership");
        } catch (Exception e) {
            handleGenericException(e, "registering customer with membership", "Failed to register customer with membership. Please try again.");
        }
    }

    /**
     * Handles updating an existing customer's information.
     * Prompts for customer ID and allows updating name and senior citizen status.
     */
    public void handleUpdateCustomer() {
        try {
            Optional<String> customerId = promptForCustomerId("update");
            customerId.ifPresent(this::updateExistingCustomer);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "updating customer");
        } catch (Exception e) {
            handleGenericException(e, "updating customer", "Failed to update customer. Please try again.");
        }
    }

    /**
     * Handles removing a customer from the system.
     * Prompts for customer ID and requires confirmation before removal.
     */
    public void handleRemoveCustomer() {
        try {
            Optional<String> customerId = promptForCustomerId("remove");
            customerId.ifPresent(this::confirmAndRemoveCustomer);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "removing customer");
        } catch (Exception e) {
            handleGenericException(e, "removing customer", "Failed to remove customer. Please try again.");
        }
    }

    /**
     * Handles adding a membership card to an existing customer.
     * Prompts for customer ID, card number, and expiry date.
     */
    public void handleAddMembershipCard() {
        try {
            Optional<String> customerId = promptForCustomerId("add membership card");
            customerId.ifPresent(this::addMembershipCardToCustomer);
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "adding membership card");
        } catch (Exception e) {
            handleGenericException(e, "adding membership card", "Failed to add membership card. Please try again.");
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Displays a list of customers in the view.
     *
     * @param customers the list of customers to display
     */
    private void displayCustomerList(List<Customer> customers) {
        try {
            List<CustomerDTO> customerDTOs = customers.stream()
                    .map(CustomerDTO::fromModel)
                    .collect(Collectors.toList());
            view.displayCustomers(customerDTOs);
        } catch (Exception e) {
            handleGenericException(e, "displaying customer list", "Error displaying customers.");
        }
    }

    /**
     * Shows detailed information for a specific customer.
     *
     * @param customerId the ID of the customer to display
     */
    private void showCustomerDetails(String customerId) {
        try {
            customerService.validateCustomerId(customerId);
            Optional<Customer> customer = customerController.getCustomerById(customerId);

            if (customer.isPresent()) {
                view.displayCustomer(CustomerDTO.fromModel(customer.get()));
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "showing customer details");
        } catch (Exception e) {
            handleGenericException(e, "showing customer details", "Error displaying customer details.");
        }
    }

    /**
     * Registers a new customer with basic information.
     *
     * @param name the name of the customer to register
     */
    private void registerNewCustomer(String name) {
        try {
            customerService.validateCustomerName(name);
            boolean isSeniorCitizen = view.getBooleanInput("Is senior citizen?");

            customerController.registerCustomer(name, isSeniorCitizen);
            view.displaySuccessMessage("Customer registered successfully.");

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "registering new customer");
        } catch (Exception e) {
            handleGenericException(e, "registering new customer", "Failed to complete customer registration.");
        }
    }

    /**
     * Registers a new customer with a membership card.
     *
     * @param name the name of the customer to register
     */
    private void registerCustomerWithCard(String name) {
        boolean shouldProceed = true;
        String operation = "registering customer with membership card";

        try {
            // Input validation phase
            if (shouldProceed && (name == null || name.trim().isEmpty())) {
                view.displayErrorMessage("Customer name cannot be empty.");
                shouldProceed = false;
            }

            if (shouldProceed) {
                customerService.validateCustomerName(name);
            }

            // Data collection phase
            boolean isSeniorCitizen = false;
            String cardNumber = "";
            LocalDate expiryDate = null;

            if (shouldProceed) {
                isSeniorCitizen = view.getBooleanInput("Is senior citizen?");
                String cardInput = view.getStringInput("Enter card ID: ");

                if (cardInput == null) {
                    shouldProceed = false;
                } else {
                    cardNumber = cardInput.trim();

                    if (cardNumber.isEmpty()) {
                        view.displayErrorMessage("Card number cannot be empty.");
                        shouldProceed = false;
                    }
                }
            }

            if (shouldProceed) {
                expiryDate = view.getDateInput("Enter expiry date (YYYY-MM-DD): ");

                if (expiryDate == null) {
                    shouldProceed = false;
                }
            }

            // Final execution phase
            if (shouldProceed) {
                customerController.registerCustomerWithMembershipCard(name, isSeniorCitizen, cardNumber, expiryDate);
                view.displaySuccessMessage("Customer registered with membership card.");
            }

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, operation);
        } catch (Exception e) {
            handleGenericException(e, operation, "Failed to complete registration with membership card.");
        }
    }

    /**
     * Updates an existing customer's information.
     *
     * @param customerId the ID of the customer to update
     */
    private void updateExistingCustomer(String customerId) {
        try {
            customerService.validateCustomerId(customerId);
            Optional<Customer> customer = customerController.getCustomerById(customerId);

            if (customer.isPresent()) {
                String name = view.getStringInput("Enter new name (leave empty to keep current): ");
                name = (name == null || name.trim().isEmpty()) ? customer.get().getName() : name.trim();

                boolean isSeniorCitizen = view.getBooleanInput("Is senior citizen?");

                customerController.updateCustomer(customerId, name, isSeniorCitizen);
                view.displaySuccessMessage("Customer updated successfully.");
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "updating existing customer");
        } catch (Exception e) {
            handleGenericException(e, "updating existing customer", "Failed to update customer.");
        }
    }

    /**
     * Confirms and removes a customer from the system.
     *
     * @param customerId the ID of the customer to remove
     */
    private void confirmAndRemoveCustomer(String customerId) {
        try {
            customerService.validateCustomerId(customerId);

            if (view.getBooleanInput("Are you sure you want to remove this customer?")) {
                customerController.removeCustomer(customerId);
                view.displaySuccessMessage("Customer removed successfully.");
            } else {
                view.displayInfoMessage("Operation cancelled.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "removing customer");
        } catch (Exception e) {
            handleGenericException(e, "removing customer", "Failed to remove customer.");
        }
    }

    /**
     * Adds a membership card to an existing customer.
     *
     * @param customerId the ID of the customer to add the card to
     */
    private void addMembershipCardToCustomer(String customerId) {
        try {
            customerService.validateCustomerId(customerId);
            Optional<Customer> customer = customerController.getCustomerById(customerId);

            if (customer.isPresent()) {
                if (customer.get().hasMembershipCard()) {
                    view.displayErrorMessage("Customer already has a membership card.");
                } else {
                    promptAndAddMembershipCard(customerId);
                }
            } else {
                view.displayErrorMessage("Customer not found.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "adding membership card to customer");
        } catch (Exception e) {
            handleGenericException(e, "adding membership card to customer", "Failed to add membership card.");
        }
    }

    /**
     * Prompts for and adds membership card details to a customer.
     *
     * @param customerId the ID of the customer to add the card to
     */
    private void promptAndAddMembershipCard(String customerId) {
        boolean shouldProceed = true;
        String operation = "prompting for membership card details";

        try {
            String cardNumber = view.getStringInput("Enter card ID: ");
            LocalDate expiryDate = null;

            // Validate card number
            if (cardNumber == null || cardNumber.trim().isEmpty()) {
                view.displayErrorMessage("Card number cannot be empty.");
                shouldProceed = false;
            }

            String trimmedCardNumber = cardNumber != null ? cardNumber.trim() : "";

            // Only proceed to get expiry date if card number is valid
            if (shouldProceed) {
                expiryDate = view.getDateInput("Enter expiry date (YYYY-MM-DD): ");

                if (expiryDate == null) {
                    shouldProceed = false;
                }
            }

            // Only execute the final operation if all validations passed
            if (shouldProceed) {
                customerController.addMembershipCard(customerId, trimmedCardNumber, expiryDate);
                view.displaySuccessMessage("Membership card added successfully.");
            }

        } catch (IllegalArgumentException e) {
            handleArgumentException(e, operation);
        } catch (Exception e) {
            handleGenericException(e, operation, "Failed to process membership card information.");
        }
    }

    // ==================== VALIDATION & UTILITY METHODS ====================

    /**
     * Prompts the user to select a customer ID for an operation.
     *
     * @param operation the operation being performed (for context in messages)
     * @return an Optional containing the customer ID if provided, empty otherwise
     */
    private Optional<String> promptForCustomerId(String operation) {
        Optional<String> temp = Optional.empty();

        try {
            List<Customer> customers = customerController.getAllCustomers();

            if (customers.isEmpty()) {
                view.displayInfoMessage("No customers available.");
            } else {
                displayCustomerList(customers);
                String customerId = view.getStringInput("Enter customer ID: ");

                if (customerId != null && !customerId.trim().isEmpty()) {
                    temp = Optional.of(customerId.trim());
                } else {
                    view.displayInfoMessage("No customer ID provided for " + operation + ".");
                }
            }
        } catch (Exception e) {
            handleGenericException(e, "prompting for customer ID", "Failed to load customer list.");
        }

        return temp;
    }

    // ==================== ERROR HANDLING HELPERS ====================

    /**
     * Handles IllegalArgumentException by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     */
    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    /**
     * Handles generic exceptions by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     * @param userMessage the message to display to the user
     */
    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}

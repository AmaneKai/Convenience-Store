package com.konbini.controller;

import com.konbini.model.Transaction;
import com.konbini.view.StoreView;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller class responsible for handling the user interface flow for
 * querying historical transactions and viewing sales reports. It presents
 * a menu of options, gathers user input for filters (like dates or customer ID),
 * and delegates the data retrieval and calculation to the TransactionController.
 */
public class TransactionManagementController {
    /**
     * The view component for displaying menus, receiving input, and showing
     * transaction data or sales reports.
     */
    private final StoreView view;
    /**
     * Controller for accessing customer data, used primarily to display a
     * customer list when searching for customer-specific transactions.
     */
    private final CustomerController customerController;
    /**
     * The core controller for transaction business logic, data retrieval,
     * and calculations.
     */
    private final TransactionController transactionController;

    /**
     * Constructs the TransactionManagementController, injecting the necessary
     * view and controller dependencies.
     *
     * @param view The user interface component.
     * @param customerController The controller for customer data access.
     * @param transactionController The controller for transaction data access and logic.
     */
    public TransactionManagementController(
            StoreView view,
            CustomerController customerController,
            TransactionController transactionController) {
        this.view = view;
        this.customerController = customerController;
        this.transactionController = transactionController;
    }

    /**
     * Runs the main loop for transaction and sales management, displaying the
     * menu and executing actions based on user choice until the user selects to exit.
     */
    public void handleTransactionManagement() {
        boolean backToMain = false;

        while (!backToMain) {
            view.displayTransactionMenu();
            int choice = view.getTransactionMenuChoice();

            switch (choice) {
                case 1:
                    viewAllTransactions();
                    break;
                case 2:
                    viewTransactionDetails();
                    break;
                case 3:
                    viewCustomerTransactions();
                    break;
                case 4:
                    viewTransactionsByDate();
                    break;
                case 5:
                    viewTransactionsByDateRange();
                    break;
                case 6:
                    viewTotalSales();
                    break;
                case 7:
                    viewTotalSalesByDate();
                    break;
                case 8:
                    viewTotalSalesByDateRange();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    view.displayErrorMessage("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Retrieves and displays a list of all transactions recorded in the system.
     */
    private void viewAllTransactions() {
        view.displayTransactions(transactionController.getAllTransactions());
    }

    /**
     * Prompts the user for a transaction ID, displays the transaction's details
     * if found, and offers to display the full receipt.
     */
    private void viewTransactionDetails() {
        try {
            view.displayTransactions(transactionController.getAllTransactions());
            String transactionId = view.getStringInput("Enter transaction ID to view details: ");
            Optional<Transaction> optionalTransaction =
                transactionController.getTransactionById(transactionId);

            if (optionalTransaction.isPresent()) {
                view.displayTransaction(optionalTransaction.get());

                if (view.getBooleanInput("View receipt?")) {
                    String receipt = transactionController.generateReceipt(
                        optionalTransaction.get());
                    view.displayReceipt(receipt);
                }
            } else {
                view.displayErrorMessage("Transaction not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view transaction details: "
                + e.getMessage());
        }
    }

    /**
     * Prompts the user for a customer ID and displays all transactions
     * associated with that customer.
     */
    private void viewCustomerTransactions() {
        try {
            view.displayCustomers(customerController.getAllCustomers());
            String customerId = view.getStringInput
                ("Enter customer ID to view transactions: ");
            view.displayTransactions(transactionController
                    .getTransactionsByCustomerId(customerId));
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to view customer transactions: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a specific date and displays all transactions
     * that occurred on that date.
     */
    private void viewTransactionsByDate() {
        try {
            LocalDate date = view
                .getDateInput("Enter date to view transactions: ");
            if (date != null) {
                view.displayTransactions(transactionController
                    .getTransactionsByDate(date));
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view transactions by date: "
                + e.getMessage());
        }
    }

    /**
     * Prompts the user for a start and end date and displays all transactions
     * within that inclusive range.
     */
    private void viewTransactionsByDateRange() {
        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            LocalDate endDate = view.getDateInput("Enter end date: ");

            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    view.displayErrorMessage
                        ("Start date cannot be after end date.");
                    return;
                }
                view.displayTransactions(
                    transactionController.getTransactionsByDateRange
                        (startDate, endDate));
            }
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to view transactions by date range: "
                + e.getMessage());
        }
    }

    /**
     * Retrieves and displays the total sales revenue across all transactions
     * in the system.
     */
    private void viewTotalSales() {
        double totalSales = transactionController.getTotalSales();
        view.displayTotalSales(totalSales);
    }

    /**
     * Prompts the user for a specific date and displays the total sales
     * revenue generated on that date.
     */
    private void viewTotalSalesByDate() {
        try {
            LocalDate date = view.getDateInput
                ("Enter date to view total sales: ");
            if (date != null) {
                double totalSales = transactionController
                    .getTotalSalesByDate(date);
                view.displayTotalSalesByDate(date, totalSales);
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view total sales by date: "
                + e.getMessage());
        }
    }

    /**
     * Prompts the user for a start and end date and displays the total sales
     * revenue generated within that inclusive range.
     */
    private void viewTotalSalesByDateRange() {
        try {
            LocalDate startDate = view.getDateInput("Enter start date: ");
            LocalDate endDate = view.getDateInput("Enter end date: ");

            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    view.displayErrorMessage
                        ("Start date cannot be after end date.");
                    return;
                }
                double totalSales = transactionController
                    .getTotalSalesByDateRange(startDate, endDate);
                view.displayTotalSalesByDateRange(startDate, endDate,
                    totalSales);
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to view total sales by date range: "
                + e.getMessage());
        }
    }
}

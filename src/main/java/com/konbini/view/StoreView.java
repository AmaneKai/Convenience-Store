package com.konbini.view;

import com.konbini.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * The master user interface contract for the entire Konbini Store application.
 * <p>
 * This interface extends all specialized view interfaces (MainView, ProductView,
 * CustomerView, CartView, TransactionView) and consolidates all necessary methods
 * for displaying data, menus, and capturing user input across the application's
 * core functional modules.
 * </p>
 */
public interface StoreView extends MainView, ProductView, CustomerView, CartView, TransactionView {

    // --- MainView methods ---
    
    /**
     * Displays a greeting or introductory message to the user upon application startup.
     */
    void displayWelcomeMessage();
    
    /**
     * Displays the primary menu of the application, listing the main functional areas.
     */
    void displayMainMenu();
    
    /**
     * Prompts for and retrieves the user's selected option from the main menu.
     *
     * @return The selected main menu option as an integer.
     */
    int getMainMenuChoice();
    
    // --- ProductView methods ---
    
    /**
     * Displays the menu options for product inventory management.
     */
    void displayProductMenu();
    
    /**
     * Prompts for and retrieves the user's choice from the product management menu.
     *
     * @return The selected menu option as an integer.
     */
    int getProductMenuChoice();
    
    // --- CustomerView methods ---
    
    /**
     * Displays the primary menu options available within the customer management section.
     */
    void displayCustomerMenu();
    
    /**
     * Prompts for and retrieves the selection from the customer management menu.
     *
     * @return The selected menu option as an integer.
     */
    int getCustomerMenuChoice();
    
    // --- CartView methods ---
    
    /**
     * Displays the primary menu options available within the cart management section.
     */
    void displayCartMenu();
    
    /**
     * Prompts for and retrieves the selection from the cart menu.
     *
     * @return The selected menu option as an integer.
     */
    int getCartMenuChoice();
    
    // --- TransactionView methods ---
    
    /**
     * Displays the menu options for transaction reporting and analysis.
     */
    void displayTransactionMenu();
    
    /**
     * Prompts for and retrieves the user's choice from the transaction management menu.
     *

     * @return The selected menu option as an integer.
     */
    int getTransactionMenuChoice();
    
    // --- Data Display Methods (Product) ---
    
    /**
     * Displays a formatted list of multiple products.
     *
     * @param products The list of Product objects to be displayed.
     */
    void displayProducts(List<Product> products);
    
    /**
     * Displays the full, detailed information for a single product.

     *
     * @param product The Product object whose details are to be displayed.
     */
    void displayProduct(Product product);
    
    /**
     * Displays a formatted list containing only products that are currently flagged as having low stock.
     *
     * @param products The list of low stock Product objects.
     */
    void displayLowStockProducts(List<Product> products);
    
    /**
     * Displays a formatted list containing only products that have passed their expiration date.
     *
     * @param products The list of expired Product objects.
     */
    void displayExpiredProducts(List<Product> products);
    
    // --- Data Display Methods (Customer) ---
    
    /**
     * Displays a formatted list of multiple customers.
     *
     * @param customers The list of Customer objects to be displayed.
     */
    void displayCustomers(List<Customer> customers);
    
    /**
     * Displays the full, detailed information for a single customer.
     *
     * @param customer The Customer object whose details are to be displayed.
     */
    void displayCustomer(Customer customer);
    
    // --- Data Display Methods (Cart) ---
    
    /**
     * Displays the current contents and summary of the shopping cart.
     *
     * @param cart The Cart object to display.
     */
    void displayCart(Cart cart);
    
    // --- Data Display Methods (Transaction) ---
    
    /**
     * Displays a formatted list of multiple transactions.
     *
     * @param transactions The list of Transaction objects to display.
     */
    void displayTransactions(List<Transaction> transactions);
    
    /**
     * Displays the full, detailed summary of a single transaction.
     *
     * @param transaction The Transaction object to display.
     */
    void displayTransaction(Transaction transaction);
    
    /**
     * Displays a pre-formatted receipt string, typically immediately after a successful checkout.
     *
     * @param receipt The generated receipt text.
     */
    void displayReceipt(String receipt);
    
    /**
     * Displays the total accumulated sales across all transactions.
     *
     * @param totalSales The calculated total sales amount.
     */
    void displayTotalSales(double totalSales);
    
    /**
     * Displays the total sales amount for a specific date.
     *
     * @param date The date for which the sales were calculated.
     * @param totalSales The total sales amount for that date.
     */
    void displayTotalSalesByDate(LocalDate date, double totalSales);
    
    /**
     * Displays the total sales amount within a specific date range.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @param totalSales The total sales amount for the date range.
     */
    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);
    
    // --- BaseView Message Methods ---
    
    /**
     * Displays a critical error message to the user.
     *
     * @param message The error text to be displayed.
     */
    void displayErrorMessage(String message);
    
    /**
     * Displays a positive confirmation message to the user, indicating a successful operation.
     *
     * @param message The success text to be displayed.
     */
    void displaySuccessMessage(String message);
    
    /**
     * Displays a general informational message to the user.
     *
     * @param message The information text to be displayed.
     */
    void displayInfoMessage(String message);
    
    // --- BaseView Input Methods ---
    
    /**
     * Prompts the user for a string input and reads the response.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The string entered by the user.
     */
    String getStringInput(String prompt);
    
    /**
     * Prompts the user for an integer input and reads the response.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The integer entered by the user.
     */
    int getIntInput(String prompt);
    
    /**
     * Prompts the user for a double-precision floating-point number and reads the response.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The double value entered by the user.
     */
    double getDoubleInput(String prompt);
    
    /**
     * Prompts the user for a boolean input (typically 'y/n').
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return True or false based on the user's input.
     */
    boolean getBooleanInput(String prompt);
    
    /**
     * Prompts the user for a date input.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The LocalDate object entered by the user.
     */
    LocalDate getDateInput(String prompt);
    
    // --- ProductView Categorization Input Methods ---
    
    /**
     * Prompts the user to select a product category from a presented list.
     *
     * @return The selected ProductCategory enum value.
     */
    ProductCategory getCategoryInput();
    
    /**
     * Prompts the user to select a product subcategory, filtered by a given category.
     *
     * @param category The parent ProductCategory to filter by.
     * @return The selected ProductSubcategory enum value, or null if no subcategory is chosen.
     */
    ProductSubcategory getSubcategoryInput(ProductCategory category);
}

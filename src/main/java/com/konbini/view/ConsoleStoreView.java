package com.konbini.view;

import com.konbini.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Concrete implementation of the StoreView interface that provides a text-based,
 * console-driven user interface for the Konbini store application.
 * It handles all menu displays, data output formatting, and user input validation.
 */
public class ConsoleStoreView implements StoreView {
    /** The scanner used for reading user input from the console. */
    private final Scanner scanner;

    /**
     * Constructs the ConsoleStoreView and initializes the Scanner for console input.
     * Registers a shutdown hook to ensure the Scanner is closed when the application terminates.
     */
    public ConsoleStoreView() {
        this.scanner = new Scanner(System.in);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (scanner != null) {
                    scanner.close();
                }
            }));
    }

    /**
     * Displays the application's welcome message banner.
     */
    @Override
    public void displayWelcomeMessage() {
        System.out.println("=====================================================");
        System.out.println("             WELCOME TO KONBINI STORE              ");
        System.out.println("=====================================================");
        System.out.println();
    }

    /**
     * Displays the main menu options for the application.
     */
    @Override
    public void displayMainMenu() {
        System.out.println("MAIN MENU");
        System.out.println("1. Product Management");
        System.out.println("2. Customer Management");
        System.out.println("3. Shopping Cart");
        System.out.println("4. Transaction Management");
        System.out.println("5. Save Data");
        System.out.println("6. Load Data");
        System.out.println("0. Exit");
        System.out.println();
    }

    /**
     * Prompts for and retrieves the user's choice from the main menu.
     *
     * @return The selected menu option as an integer.
     */
    @Override
    public int getMainMenuChoice() {
        return getIntInput("Enter your choice: ");
    }

    /**
     * Displays the menu options for product inventory management.
     */
    @Override
    public void displayProductMenu() {
        System.out.println("PRODUCT MANAGEMENT");
        System.out.println("1. View All Products");
        System.out.println("2. View Products by Category");
        System.out.println("3. View Products by Subcategory");
        System.out.println("4. Search Products by Name");
        System.out.println("5. View Low Stock Products");
        System.out.println("6. View Expired Products");
        System.out.println("7. Add New Product");
        System.out.println("8. Update Existing Product");
        System.out.println("9. Remove Product");
        System.out.println("10. Restock Product");
        System.out.println("0. Back to Main Menu");
        System.out.println();
    }

    /**
     * Prompts for and retrieves the user's choice from the product management menu.
     *
     * @return The selected menu option as an integer.
     */
    @Override
    public int getProductMenuChoice() {
        return getIntInput("Enter your choice: ");
    }

    /**
     * Displays the menu options for customer account management.
     */
    @Override
    public void displayCustomerMenu() {
        System.out.println("CUSTOMER MANAGEMENT");
        System.out.println("1. View All Customers");
        System.out.println("2. View Customer Details");
        System.out.println("3. Register New Customer");
        System.out.println("4. Register Customer with Membership Card");
        System.out.println("5. Update Customer Information");
        System.out.println("6. Remove Customer");
        System.out.println("7. Add Membership Card to Customer");
        System.out.println("0. Back to Main Menu");
        System.out.println();
    }

    /**
     * Prompts for and retrieves the user's choice from the customer management menu.
     *
     * @return The selected menu option as an integer.
     */
    @Override
    public int getCustomerMenuChoice() {
        return getIntInput("Enter your choice: ");
    }

    /**
     * Displays the menu options for shopping cart operations.
     */
    @Override
    public void displayCartMenu() {
        System.out.println("SHOPPING CART");
        System.out.println("1. Create New Cart");
        System.out.println("2. View Cart");
        System.out.println("3. Add Item to Cart");
        System.out.println("4. Remove Item from Cart");
        System.out.println("5. Update Item Quantity");
        System.out.println("6. Clear Cart");
        System.out.println("7. Checkout");
        System.out.println("0. Back to Main Menu");
        System.out.println();
    }

    /**
     * Prompts for and retrieves the user's choice from the cart menu.
     *
     * @return The selected menu option as an integer.
     */
    @Override
    public int getCartMenuChoice() {
        return getIntInput("Enter your choice: ");
    }

    /**
     * Displays the menu options for transaction reporting and analysis.
     */
    @Override
    public void displayTransactionMenu() {
        System.out.println("TRANSACTION MANAGEMENT");
        System.out.println("1. View All Transactions");
        System.out.println("2. View Transaction Details");
        System.out.println("3. View Customer Transactions");
        System.out.println("4. View Transactions by Date");
        System.out.println("5. View Transactions by Date Range");
        System.out.println("6. View Total Sales");
        System.out.println("7. View Total Sales by Date");
        System.out.println("8. View Total Sales by Date Range");
        System.out.println("0. Back to Main Menu");
        System.out.println();
    }

    /**
     * Prompts for and retrieves the user's choice from the transaction management menu.
     *
     * @return The selected menu option as an integer.
     */
    @Override
    public int getTransactionMenuChoice() {
        return getIntInput("Enter your choice: ");
    }

    /**
     * Displays a formatted list of products, including key details like ID, name, price, and quantity.
     * Prints a "No products found" message if the list is empty.
     *
     * @param products The list of Product objects to display.
     */
    @Override
    public void displayProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.println("PRODUCTS");
        System.out.println("------------------------------------------------------");
        System.out.printf("%-8s %-20s %-10s %-10s %-15s%n", "ID", "Name", "Price", "Quantity", "Category");
        System.out.println("------------------------------------------------------");

        for (Product product : products) {
            System.out.printf("%-8s %-20s ₱%-9.2f %-10d %-15s%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity(),
                    product.getCategory());
        }

        System.out.println("------------------------------------------------------");
        System.out.println("Total products: " + products.size());
        System.out.println();
    }

    /**
     * Displays the detailed information for a single product.
     * Prints "Product not found" if the product object is null.
     *
     * @param product The Product object to display.
     */
    @Override
    public void displayProduct(Product product) {
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("PRODUCT DETAILS");
        System.out.println("ID: " + product.getId());
        System.out.println("Name: " + product.getName());
        System.out.println("Price: ₱" + String.format("%.2f", product.getPrice()));
        System.out.println("Quantity: " + product.getQuantity());
        System.out.println("Category: " + product.getCategory());
        System.out.println("Brand: " + product.getBrand());
        System.out.println("Variant: " + (product.getVariant() != null ? product.getVariant() : "N/A"));
        System.out.println("Expiration Date: " + (product.getExpirationDate() != null ?
                product.getExpirationDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A"));
        System.out.println("Status: " + (product.isLowStock() ? "Low Stock" : "In Stock") +
                (product.isExpired() ? ", Expired" : ""));
        System.out.println();
    }

    /**
     * Displays a list of products flagged as having low stock.
     * Reuses the generic displayProducts method for formatting.
     *
     * @param products The list of low stock Product objects.
     */
    @Override
    public void displayLowStockProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products with low stock found.");
            return;
        }

        System.out.println("LOW STOCK PRODUCTS");
        displayProducts(products);
    }

    /**
     * Displays a list of products that have passed their expiration date.
     * Reuses the generic displayProducts method for formatting.
     *
     * @param products The list of expired Product objects.
     */
    @Override
    public void displayExpiredProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No expired products found.");
            return;
        }

        System.out.println("EXPIRED PRODUCTS");
        displayProducts(products);
    }

    /**
     * Displays a formatted list of customers, including their ID, name, senior citizen status, and membership status.
     * Prints a "No customers found" message if the list is empty.
     *
     * @param customers The list of Customer objects to display.
     */
    @Override
    public void displayCustomers(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.println("CUSTOMERS");
        System.out.println("------------------------------------------------------");
        System.out.printf("%-8s %-20s %-15s %-15s%n", "ID", "Name", "Senior Citizen", "Membership");
        System.out.println("------------------------------------------------------");

        for (Customer customer : customers) {
            System.out.printf("%-8s %-20s %-15s %-15s%n",
                    customer.getId(),
                    customer.getName(),
                    customer.isSeniorCitizen() ? "Yes" : "No",
                    customer.hasMembershipCard() ? "Yes" : "No");
        }

        System.out.println("------------------------------------------------------");
        System.out.println("Total customers: " + customers.size());
        System.out.println();
    }

    /**
     * Displays the detailed information for a single customer, including membership card details if applicable.
     * Prints "Customer not found" if the customer object is null.
     *
     * @param customer The Customer object to display.
     */
    @Override
    public void displayCustomer(Customer customer) {
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("CUSTOMER DETAILS");
        System.out.println("ID: " + customer.getId());
        System.out.println("Name: " + customer.getName());
        System.out.println("Senior Citizen: " + (customer.isSeniorCitizen() ? "Yes" : "No"));

        if (customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();
            System.out.println("Membership Card: Yes");
            System.out.println("Card Number: " + card.getCardNumber());
            System.out.println("Points: " + card.getPoints());
            System.out.println("Expiry Date: " + card.getExpiryDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            System.out.println("Card Status: " + (card.isExpired() ? "Expired" : "Active"));
        } else {
            System.out.println("Membership Card: No");
        }

        System.out.println();
    }

    /**
     * Displays the contents and summary of the current shopping cart.
     * Includes customer name, itemized list with subtotals, total items count, and overall subtotal.
     *
     * @param cart The Cart object to display.
     */
    @Override
    public void displayCart(Cart cart) {
        if (cart == null) {
            System.out.println("Cart not initialized.");
            return;
        }

        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("CART");
        System.out.println("Customer: " + cart.getCustomer().getName());
        System.out.println("------------------------------------------------------");
        System.out.printf("%-8s %-20s %-10s %-10s %-12s%n", "ID", "Product", "Price", "Quantity", "Subtotal");
        System.out.println("------------------------------------------------------");

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            System.out.printf("%-8s %-20s ₱%-9.2f %-10d ₱%-11.2f%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.getQuantity(),
                    item.getSubtotal());
        }

        System.out.println("------------------------------------------------------");
        System.out.println("Total Items: " + cart.getTotalItems());
        System.out.println("Subtotal: ₱" + String.format("%.2f", cart.getSubtotal()));
        System.out.println();
    }

    /**
     * Displays a formatted list of transactions, including ID, customer name, date/time, and total amount.
     * Prints a "No transactions found" message if the list is empty.
     *
     * @param transactions The list of Transaction objects to display.
     */
    @Override
    public void displayTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("TRANSACTIONS");
        System.out.println("------------------------------------------------------");
        System.out.printf("%-8s %-20s %-20s %-12s%n", "ID", "Customer", "Date", "Total");
        System.out.println("------------------------------------------------------");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Transaction transaction : transactions) {
            System.out.printf("%-8s %-20s %-20s ₱%-11.2f%n",
                    transaction.getId(),
                    transaction.getCustomer().getName(),
                    transaction.getTimestamp().format(formatter),
                    transaction.getTotal());
        }

        System.out.println("------------------------------------------------------");
        System.out.println("Total transactions: " + transactions.size());
        System.out.println();
    }

    /**
     * Displays the detailed summary of a single transaction.
     * Includes itemized list, subtotal, tax, discount, total, payment details, and loyalty point information.
     *
     * @param transaction The Transaction object to display.
     */
    @Override
    public void displayTransaction(Transaction transaction) {
        if (transaction == null) {
            System.out.println("Transaction not found.");
            return;
        }

        System.out.println("TRANSACTION DETAILS");
        System.out.println("ID: " + transaction.getId());
        System.out.println("Customer: " + transaction.getCustomer().getName());
        System.out.println("Date: " + transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println();

        System.out.println("Items:");
        System.out.println("------------------------------------------------------");
        System.out.printf("%-20s %-10s %-10s %-12s%n", "Product", "Price", "Quantity", "Subtotal");
        System.out.println("------------------------------------------------------");

        for (CartItem item : transaction.getItems()) {
            Product product = item.getProduct();
            System.out.printf("%-20s ₱%-9.2f %-10d ₱%-11.2f%n",
                    product.getName(),
                    product.getPrice(),
                    item.getQuantity(),
                    item.getSubtotal());
        }

        System.out.println("------------------------------------------------------");
        System.out.println("Subtotal: ₱" + String.format("%.2f", transaction.getSubtotal()));
        System.out.println("Tax (VAT): ₱" + String.format("%.2f", transaction.getTax()));

        if (transaction.getDiscount() > 0) {
            System.out.println("Discount: ₱" + String.format("%.2f", transaction.getDiscount()));
        }

        System.out.println("Total: ₱" + String.format("%.2f", transaction.getTotal()));
        System.out.println();

        System.out.println("Amount Paid: ₱" + String.format("%.2f", transaction.getAmountPaid()));
        System.out.println("Change: ₱" + String.format("%.2f", transaction.getChange()));
        System.out.println();

        if (transaction.getPointsRedeemed() > 0) {
            System.out.println("Points Redeemed: " + transaction.getPointsRedeemed());
        }

        if (transaction.getPointsEarned() > 0) {
            System.out.println("Points Earned: " + transaction.getPointsEarned());
        }

        System.out.println();
    }

    /**
     * Displays a pre-formatted receipt string, typically immediately after a checkout.
     *
     * @param receipt The generated receipt text.
     */
    @Override
    public void displayReceipt(String receipt) {
        System.out.println(receipt);
    }

    /**
     * Displays the total accumulated sales across all transactions.
     *
     * @param totalSales The calculated total sales amount.
     */
    @Override
    public void displayTotalSales(double totalSales) {
        System.out.println("TOTAL SALES: ₱" + String.format("%.2f", totalSales));
        System.out.println();
    }

    /**
     * Displays the total sales amount for a specific date.
     *
     * @param date The date for which the sales were calculated.
     * @param totalSales The total sales amount for that date.
     */
    @Override
    public void displayTotalSalesByDate(LocalDate date, double totalSales) {
        System.out.println("TOTAL SALES FOR " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + ": ₱" +
                String.format("%.2f", totalSales));
        System.out.println();
    }

    /**
     * Displays the total sales amount within a specific date range.
     *
     * @param startDate The start date of the range (inclusive).
     * @param endDate The end date of the range (inclusive).
     * @param totalSales The total sales amount for the date range.
     */
    @Override
    public void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales) {
        System.out.println("TOTAL SALES FROM " + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                " TO " + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + ": ₱" +
                String.format("%.2f", totalSales));
        System.out.println();
    }

    /**
     * Displays a formatted error message.
     *
     * @param message The error text.
     */
    @Override
    public void displayErrorMessage(String message) {
        System.out.println("ERROR: " + message);
        System.out.println();
    }

    /**
     * Displays a formatted success message.
     *
     * @param message The success text.
     */
    @Override
    public void displaySuccessMessage(String message) {
        System.out.println("SUCCESS: " + message);
        System.out.println();
    }

    /**
     * Displays a formatted informational message.
     *
     * @param message The information text.
     */
    @Override
    public void displayInfoMessage(String message) {
        System.out.println("INFO: " + message);
        System.out.println();
    }

    /**
     * Prompts the user for a string input and reads the entire line.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The string entered by the user.
     */
    @Override
    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Prompts the user for an integer input and reads the response.
     * Repeats the prompt until a valid integer is entered.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The integer entered by the user.
     */
    @Override
    public int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                // Use nextLine to consume the whole line and avoid Scanner issues
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    /**
     * Prompts the user for a double input and reads the response.
     * Repeats the prompt until a valid double is entered.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The double value entered by the user.
     */
    @Override
    public double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Prompts the user for a boolean input (accepts 'y', 'yes', 'n', 'no' case-insensitively).
     * Repeats the prompt until a valid response is given.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return True for 'y'/'yes', false for 'n'/'no'.
     */
    @Override
    public boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }
    }

    /**
     * Prompts the user for a date input in the YYYY-MM-DD format.
     * Allows an empty input to return null. Repeats the prompt until a valid format is entered.
     *
     * @param prompt The message displayed to the user requesting the input.
     * @return The LocalDate object or null if the input was empty.
     */
    @Override
    public LocalDate getDateInput(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        while (true) {
            try {
                System.out.print(prompt + " (YYYY-MM-DD): ");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    return null;
                }

                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a valid date in the format YYYY-MM-DD.");
            }
        }
    }

    /**
     * Presents a numbered list of all available product categories and prompts the user to select one.
     * Repeats the prompt until a valid choice is made.
     *
     * @return The selected ProductCategory enum value.
     */
    @Override
    public ProductCategory getCategoryInput() {
        System.out.println("Available Categories:");
        ProductCategory[] categories = ProductCategory.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i].getDisplayName());
        }

        while (true) {
            int choice = getIntInput("Select category (1-" + categories.length + "): ");

            if (choice >= 1 && choice <= categories.length) {
                return categories[choice - 1];
            } else {
                System.out.println("Please enter a valid choice.");
            }
        }
    }

    /**
     * Presents a numbered list of subcategories that belong to a given category and prompts the user to select one.
     * Allows the user to select '0' for no subcategory (returns null).
     *
     * @param category The parent category to filter subcategories by.
     * @return The selected ProductSubcategory enum value, or null if '0' is chosen.
     */
    @Override
    public ProductSubcategory getSubcategoryInput(ProductCategory category) {
        System.out.println("Available Subcategories for " + category.getDisplayName() + ":");
        List<ProductSubcategory> subcategories = Arrays.asList(ProductSubcategory.values());
        List<ProductSubcategory> filteredSubcategories = subcategories.stream()
                .filter(subcat -> subcat.getCategory() == category)
                .collect(Collectors.toList());

        for (int i = 0; i < filteredSubcategories.size(); i++) {
            System.out.println((i + 1) + ". " + filteredSubcategories.get(i).getDisplayName());
        }

        while (true) {
            int choice = getIntInput("Select subcategory (1-" + filteredSubcategories.size() + ") or 0 for none: ");

            if (choice == 0) {
                return null;
            } else if (choice >= 1 && choice <= filteredSubcategories.size()) {
                return filteredSubcategories.get(choice - 1);
            } else {
                System.out.println("Please enter a valid choice.");
            }
        }
    }
}

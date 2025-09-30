package com.konbini.view;

import com.konbini.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleStoreView implements StoreView {
    private final Scanner scanner;
    
    public ConsoleStoreView() {
        this.scanner = new Scanner(System.in);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
               if (scanner != null) {
                   scanner.close();
               }
           }));
    }
    
    @Override
    public void displayWelcomeMessage() {
        System.out.println("=====================================================");
        System.out.println("             WELCOME TO KONBINI STORE               ");
        System.out.println("=====================================================");
        System.out.println();
    }
    
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
    
    @Override
    public int getMainMenuChoice() {
        return getIntInput("Enter your choice: ");
    }
    
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
    
    @Override
    public int getProductMenuChoice() {
        return getIntInput("Enter your choice: ");
    }
    
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
    
    @Override
    public int getCustomerMenuChoice() {
        return getIntInput("Enter your choice: ");
    }
    
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
    
    @Override
    public int getCartMenuChoice() {
        return getIntInput("Enter your choice: ");
    }
    
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
    
    @Override
    public int getTransactionMenuChoice() {
        return getIntInput("Enter your choice: ");
    }
    
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
    
    @Override
    public void displayLowStockProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products with low stock found.");
            return;
        }
        
        System.out.println("LOW STOCK PRODUCTS");
        displayProducts(products);
    }
    
    @Override
    public void displayExpiredProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No expired products found.");
            return;
        }
        
        System.out.println("EXPIRED PRODUCTS");
        displayProducts(products);
    }
    
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
    
    @Override
    public void displayReceipt(String receipt) {
        System.out.println(receipt);
    }
    
    @Override
    public void displayTotalSales(double totalSales) {
        System.out.println("TOTAL SALES: ₱" + String.format("%.2f", totalSales));
        System.out.println();
    }
    
    @Override
    public void displayTotalSalesByDate(LocalDate date, double totalSales) {
        System.out.println("TOTAL SALES FOR " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + ": ₱" + 
                String.format("%.2f", totalSales));
        System.out.println();
    }
    
    @Override
    public void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales) {
        System.out.println("TOTAL SALES FROM " + startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + 
                " TO " + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + ": ₱" + 
                String.format("%.2f", totalSales));
        System.out.println();
    }
    
    @Override
    public void displayErrorMessage(String message) {
        System.out.println("ERROR: " + message);
        System.out.println();
    }
    
    @Override
    public void displaySuccessMessage(String message) {
        System.out.println("SUCCESS: " + message);
        System.out.println();
    }
    
    @Override
    public void displayInfoMessage(String message) {
        System.out.println("INFO: " + message);
        System.out.println();
    }
    
    @Override
    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    @Override
    public int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
    
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

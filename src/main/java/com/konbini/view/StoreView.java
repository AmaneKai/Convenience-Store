package com.konbini.view;

import com.konbini.model.*;

import java.time.LocalDate;
import java.util.List;

public interface StoreView extends MainView, ProductView, CustomerView, CartView, TransactionView {
    void displayWelcomeMessage();
    void displayMainMenu();
    int getMainMenuChoice();
    
    void displayProductMenu();
    int getProductMenuChoice();
    
    void displayCustomerMenu();
    int getCustomerMenuChoice();
    
    void displayCartMenu();
    int getCartMenuChoice();
    
    void displayTransactionMenu();
    int getTransactionMenuChoice();
    
    void displayProducts(List<Product> products);
    void displayProduct(Product product);
    void displayLowStockProducts(List<Product> products);
    void displayExpiredProducts(List<Product> products);
    
    void displayCustomers(List<Customer> customers);
    void displayCustomer(Customer customer);
    
    void displayCart(Cart cart);
    
    void displayTransactions(List<Transaction> transactions);
    void displayTransaction(Transaction transaction);
    void displayReceipt(String receipt);
    void displayTotalSales(double totalSales);
    void displayTotalSalesByDate(LocalDate date, double totalSales);
    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);
    
    void displayErrorMessage(String message);
    void displaySuccessMessage(String message);
    void displayInfoMessage(String message);
    
    // Input methods
    String getStringInput(String prompt);
    int getIntInput(String prompt);
    double getDoubleInput(String prompt);
    boolean getBooleanInput(String prompt);
    LocalDate getDateInput(String prompt);
    
    ProductCategory getCategoryInput();
    ProductSubcategory getSubcategoryInput(ProductCategory category);
}

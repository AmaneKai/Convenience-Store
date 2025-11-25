package com.konbini.view;

import java.time.LocalDate;
import java.util.List;

import com.konbini.dto.CartDTO;
import com.konbini.dto.CustomerDTO;
import com.konbini.dto.EmployeeDTO;
import com.konbini.dto.ProductDTO;
import com.konbini.dto.TransactionDTO;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

/**
 * Comprehensive view interface that combines all application view functionalities.
 * Serves as a unified interface for all user interactions throughout the store management system.
 * Extends all specialized view interfaces to provide complete application coverage.
 */
public interface StoreView extends MainView, ProductView, CustomerView, CartView, TransactionView, EmployeeView {

    // MainView methods
    void displayWelcomeMessage();
    void displayMainMenu();
    int getMainMenuChoice();

    // ProductView methods
    void displayProductMenu();
    int getProductMenuChoice();
    void displayProducts(List<ProductDTO> products);
    void displayProduct(ProductDTO product);
    void displayLowStockProducts(List<ProductDTO> products);
    void displayExpiredProducts(List<ProductDTO> products);

    // CustomerView methods
    void displayCustomerMenu();
    int getCustomerMenuChoice();
    void displayCustomers(List<CustomerDTO> customers);
    void displayCustomer(CustomerDTO customer);

    // CartView methods
    void displayCartMenu();
    int getCartMenuChoice();
    void displayCart(CartDTO cart);

    // TransactionView methods
    void displayTransactionMenu();
    int getTransactionMenuChoice();
    void displayTransactions(List<TransactionDTO> transactions);
    void displayTransaction(TransactionDTO transaction);
    void displayReceipt(String receipt);
    void displayTotalSales(double totalSales);
    void displayTotalSalesByDate(LocalDate date, double totalSales);
    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);

    // EmployeeView methods
    void displayEmployeeMenu();
    int getEmployeeMenuChoice();
    void displayEmployees(List<EmployeeDTO> employees);
    void displayEmployee(EmployeeDTO employee);

    // BaseView methods
    void displayErrorMessage(String message);
    void displaySuccessMessage(String message);
    void displayInfoMessage(String message);
    String getStringInput(String prompt);
    int getIntInput(String prompt);
    double getDoubleInput(String prompt);
    boolean getBooleanInput(String prompt);
    LocalDate getDateInput(String prompt);

    // ProductView input methods
    ProductCategory getCategoryInput();
    ProductSubcategory getSubcategoryInput(ProductCategory category);
}
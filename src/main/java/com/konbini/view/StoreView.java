package com.konbini.view;

import java.time.LocalDate;
import java.util.List;

import com.konbini.dto.CartDTO;
import com.konbini.dto.CustomerDTO;
import com.konbini.dto.ProductDTO;
import com.konbini.dto.TransactionDTO;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

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

    void displayProducts(List<ProductDTO> products);

    void displayProduct(ProductDTO product);

    void displayLowStockProducts(List<ProductDTO> products);

    void displayExpiredProducts(List<ProductDTO> products);

    void displayCustomers(List<CustomerDTO> customers);

    void displayCustomer(CustomerDTO customer);

    void displayCart(CartDTO cart);

    void displayTransactions(List<TransactionDTO> transactions);

    void displayTransaction(TransactionDTO transaction);

    void displayReceipt(String receipt);

    void displayTotalSales(double totalSales);

    void displayTotalSalesByDate(LocalDate date, double totalSales);

    void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales);

    void displayErrorMessage(String message);

    void displaySuccessMessage(String message);

    void displayInfoMessage(String message);

    String getStringInput(String prompt);

    int getIntInput(String prompt);

    double getDoubleInput(String prompt);

    boolean getBooleanInput(String prompt);

    LocalDate getDateInput(String prompt);

    ProductCategory getCategoryInput();

    ProductSubcategory getSubcategoryInput(ProductCategory category);
}
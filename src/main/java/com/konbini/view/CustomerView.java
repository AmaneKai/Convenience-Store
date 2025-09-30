package com.konbini.view;

import com.konbini.model.Customer;

import java.util.List;

public interface CustomerView extends BaseView {
    void displayCustomerMenu();
    int getCustomerMenuChoice();
    void displayCustomers(List<Customer> customers);
    void displayCustomer(Customer customer);
}

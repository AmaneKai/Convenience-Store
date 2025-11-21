package com.konbini.view;

import java.util.List;

import com.konbini.dto.CustomerDTO;

public interface CustomerView extends BaseView {
    void displayCustomerMenu();

    int getCustomerMenuChoice();

    void displayCustomers(List<CustomerDTO> customers);

    void displayCustomer(CustomerDTO customer);
}
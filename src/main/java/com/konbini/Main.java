package com.konbini;

import com.konbini.controller.*;
import com.konbini.model.repository.*;
import com.konbini.model.repository.impl.*;
import com.konbini.service.*;
import com.konbini.service.impl.*;
import com.konbini.view.swing.SwingStoreView;

/**
 * Main entry point for the Store Management System application.
 * Initializes all components including repositories, services, controllers, and the UI.
 */
public class Main {
    public static void main(String[] args) {
        ProductRepository productRepository = new FileProductRepository("products.dat");
        CustomerRepository customerRepository = new FileCustomerRepository("customers.dat");
        TransactionRepository transactionRepository = new FileTransactionRepository("transactions.dat");
        EmployeeRepository employeeRepository = new FileEmployeeRepository("employees.dat");

        ProductService productService = new ProductServiceImpl(productRepository);
        CustomerService customerService = new CustomerServiceImpl(customerRepository);
        CartService cartService = new CartServiceImpl();
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository);
        EmployeeService employeeService = new EmployeeServiceImpl(employeeRepository);

        SwingStoreView view = new SwingStoreView();

        ProductController productController = new ProductController(productService);
        CustomerController customerController = new CustomerController(customerService);
        CartController cartController = new CartController(productService);
        TransactionController transactionController = new TransactionController(transactionService);
        EmployeeController employeeController = new EmployeeController(employeeService);

        ProductManagementController productManagementController = new ProductManagementController(
                view,
                productController,
                productService
        );
        CustomerManagementController customerManagementController = new CustomerManagementController(
                view,
                customerController,
                customerService
        );
        CartManagementController cartManagementController = new CartManagementController(
                view,
                productController,
                customerController,
                cartController,
                transactionController,
                cartService
        );
        TransactionManagementController transactionManagementController = new TransactionManagementController(
                view,
                customerController,
                transactionController
        );
        DataManagementController dataManagementController = new DataManagementController(
                view,
                productController,
                customerController,
                transactionController,
                productManagementController
        );
        EmployeeManagementController employeeManagementController = new EmployeeManagementController(
                view,
                employeeController,
                employeeService
        );

        view.setControllers(
                productManagementController,
                customerManagementController,
                cartManagementController,
                transactionManagementController,
                dataManagementController,
                employeeController,
                employeeManagementController
        );

        MainController mainController = new MainController(
                view,
                dataManagementController
        );

        boolean productsLoaded = productController.loadData();
        boolean customersLoaded = customerController.loadData();
        boolean transactionsLoaded = transactionController.loadData();
        boolean employeesLoaded = employeeController.loadData();

        boolean needSampleData = false;

        if (!productsLoaded) {
            view.displayInfoMessage("No product data found. Will initialize sample products.");
            needSampleData = true;
        }

        if (!customersLoaded) {
            view.displayInfoMessage("No customer data found. Will initialize sample customers.");
            needSampleData = true;
        }

        if (!transactionsLoaded) {
            view.displayInfoMessage("No transaction data found.");
        }

        if (!employeesLoaded) {
            view.displayInfoMessage("No employee data found. Initializing sample employees.");
            employeeController.addEmployee("Manager", "password");
            employeeController.addEmployee("Cashier", "cashier123");
            employeeController.addEmployee("Supervisor", "super456");
            employeeController.saveData();
            employeeController.loadData();
        }

        if (needSampleData) {
            mainController.initializeSampleData();
        }

        mainController.start();
    }
}
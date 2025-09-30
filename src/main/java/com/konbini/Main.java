package com.konbini;

import com.konbini.controller.*;
import com.konbini.model.repository.*;
import com.konbini.model.repository.impl.*;
import com.konbini.service.*;
import com.konbini.service.impl.*;
import com.konbini.view.*;

public class Main {
    public static void main(String[] args) {
        // Initialize repositories
        ProductRepository productRepository = new FileProductRepository
            ("products.dat");
        CustomerRepository customerRepository = new FileCustomerRepository
            ("customers.dat");
        TransactionRepository transactionRepository = 
            new FileTransactionRepository("transactions.dat");
        
        // Initialize services
        ProductService productService = new ProductServiceImpl
            (productRepository);
        CustomerService customerService = new CustomerServiceImpl
            (customerRepository);
        TransactionService transactionService = 
            new TransactionServiceImpl
                (transactionRepository, productRepository);
        
        // Initialize view
        StoreView view = new ConsoleStoreView();
        
        // Initialize base controllers
        ProductController productController = new ProductController
            (productService);
        CustomerController customerController = new CustomerController
            (customerService);
        CartController cartController = new CartController
            (productService);
        TransactionController transactionController = 
            new TransactionController(transactionService);
        
        // Initialize management controllers
        ProductManagementController productManagementController = 
            new ProductManagementController(view, productController);
        
        CustomerManagementController customerManagementController = 
            new CustomerManagementController(view, customerController);
        
        CartManagementController cartManagementController = 
            new CartManagementController(view, productController, 
            customerController, cartController, transactionController);
        
        TransactionManagementController transactionManagementController = 
            new TransactionManagementController(view, customerController, 
                transactionController);
        
        DataManagementController dataManagementController = 
            new DataManagementController(view, productController, 
                customerController, transactionController, 
                productManagementController);
        
        // Initialize main controller
        MainController mainController = new MainController(
                view,
                customerManagementController,
                cartManagementController,
                transactionManagementController,
                dataManagementController,
                productManagementController
        );
        
        // Load or initialize data
        boolean productsLoaded = productController.loadData();
        boolean customersLoaded = customerController.loadData();
        boolean transactionsLoaded = transactionController.loadData();
        
        boolean needSampleData = false;
        
        if (!productsLoaded) {
            view.displayInfoMessage
            ("No product data found. Will initialize sample products.");
            needSampleData = true;
        }
        
        if (!customersLoaded) {
            view.displayInfoMessage
            ("No customer data found. Will initialize sample customers.");
            needSampleData = true;
        }
        
        if (!transactionsLoaded) {
            view.displayInfoMessage("No transaction data found.");
        }
        
        if (needSampleData) {
            mainController.initializeSampleData();
        }
        
        mainController.start();
    }
}

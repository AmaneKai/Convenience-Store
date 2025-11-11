package com.konbini;

import com.konbini.controller.CartController;
import com.konbini.controller.CartManagementController;
import com.konbini.controller.CustomerController;
import com.konbini.controller.CustomerManagementController;
import com.konbini.controller.DataManagementController;
import com.konbini.controller.MainController;
import com.konbini.controller.ProductController;
import com.konbini.controller.ProductManagementController;
import com.konbini.controller.TransactionController;
import com.konbini.controller.TransactionManagementController;
import com.konbini.model.repository.CustomerRepository;
import com.konbini.model.repository.ProductRepository;
import com.konbini.model.repository.TransactionRepository;
import com.konbini.model.repository.impl.FileCustomerRepository;
import com.konbini.model.repository.impl.FileProductRepository;
import com.konbini.model.repository.impl.FileTransactionRepository;
import com.konbini.service.CustomerService;
import com.konbini.service.ProductService;
import com.konbini.service.TransactionService;
import com.konbini.service.impl.CustomerServiceImpl;
import com.konbini.service.impl.ProductServiceImpl;
import com.konbini.service.impl.TransactionServiceImpl;
import com.konbini.view.swing.SwingStoreView;

public class Main {
    public static void main(String[] args) {
        // Initialize repositories
        ProductRepository productRepository = new FileProductRepository("products.dat");
        CustomerRepository customerRepository = new FileCustomerRepository("customers.dat");
        TransactionRepository transactionRepository = new FileTransactionRepository("transactions.dat");
        
        // Initialize services
        ProductService productService = new ProductServiceImpl(productRepository);
        CustomerService customerService = new CustomerServiceImpl(customerRepository);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository, productRepository);
        
        // Initialize view FIRST
        SwingStoreView view = new SwingStoreView();
        
        // Initialize base controllers
        ProductController productController = new ProductController(productService);
        CustomerController customerController = new CustomerController(customerService);
        CartController cartController = new CartController(productService);
        TransactionController transactionController = new TransactionController(transactionService);
        
        // Initialize management controllers
        ProductManagementController productManagementController = new ProductManagementController(view, productController);
        CustomerManagementController customerManagementController = new CustomerManagementController(view, customerController);
        CartManagementController cartManagementController = new CartManagementController(view, productController, customerController, cartController, transactionController);
        TransactionManagementController transactionManagementController = new TransactionManagementController(view, customerController, transactionController);
        DataManagementController dataManagementController = new DataManagementController(view, productController, customerController, transactionController, productManagementController);
        
        // *** CRITICAL: Set controllers on view BEFORE using it ***
        view.setControllers(
            productManagementController,
            customerManagementController,
            cartManagementController,
            transactionManagementController,
            dataManagementController
        );
        
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
        
        if (needSampleData) {
            mainController.initializeSampleData();
        }
        
        // Start the application
        mainController.start();
    }
}
package com.konbini.view.swing;

import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;

import com.konbini.controller.*;
import com.konbini.dto.*;
import com.konbini.model.*;
import com.konbini.util.UserSession;
import com.konbini.view.StoreView;

public class SwingStoreView implements StoreView {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private java.awt.CardLayout cardLayout;

    private CustomerMenuPanel customerMenuPanel;
    private EmployeeMenuPanel employeeMenuPanel;
    private ProductPanel productPanel;
    private CustomerPanel customerPanel;
    private CartPanel cartPanel;
    private TransactionPanel transactionPanel;
    private EmployeePanel employeePanel;

    private ProductManagementController productManagementController;
    private CustomerManagementController customerManagementController;
    private CartManagementController cartManagementController;
    private TransactionManagementController transactionManagementController;
    private DataManagementController dataManagementController;
    private EmployeeController employeeController;
    private EmployeeManagementController employeeManagementController;

    private static final String CUSTOMER_MENU_CARD = "CustomerMenu";
    private static final String EMPLOYEE_MENU_CARD = "EmployeeMenu";
    private static final String PRODUCT_CARD = "Product";
    private static final String CUSTOMER_CARD = "Customer";
    private static final String CART_CARD = "Cart";
    private static final String TRANSACTION_CARD = "Transaction";
    private static final String EMPLOYEE_CARD = "Employee";

    private boolean applicationRunning = true;

    public SwingStoreView() {
        initializeGUI();
    }

    public void setControllers(
            ProductManagementController productManagementController,
            CustomerManagementController customerManagementController,
            CartManagementController cartManagementController,
            TransactionManagementController transactionManagementController,
            DataManagementController dataManagementController,
            EmployeeController employeeController,
            EmployeeManagementController employeeManagementController) {
        validateControllerDependencies(
                productManagementController,
                customerManagementController,
                cartManagementController,
                transactionManagementController,
                dataManagementController
        );

        this.productManagementController = productManagementController;
        this.customerManagementController = customerManagementController;
        this.cartManagementController = cartManagementController;
        this.transactionManagementController = transactionManagementController;
        this.dataManagementController = dataManagementController;
        this.employeeController = employeeController;
        this.employeeManagementController = employeeManagementController;

        createPanelsWithControllers();
    }

    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            handleUIException(e, "setting look and feel", "Warning: Could not set system look and feel.");
        }

        try {
            mainFrame = new JFrame("コンビニ");
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    handleExit();
                }
            });
            mainFrame.setSize(1024, 768);

            cardLayout = new java.awt.CardLayout();
            mainPanel = new JPanel(cardLayout);

            mainFrame.add(mainPanel);
            mainFrame.setLocationRelativeTo(null);

        } catch (Exception e) {
            handleUIException(e, "initializing main window", "Failed to initialize application window.");
            applicationRunning = false;
        }
    }

    private void createPanelsWithControllers() {
        try {
            mainPanel.removeAll();

            customerMenuPanel = new CustomerMenuPanel(
                    this.mainFrame,
                    this::navigateToScreen,
                    this::showLoginScreen
            );

            employeeMenuPanel = new EmployeeMenuPanel(
                    this.mainFrame,
                    this::navigateToScreen,
                    this::showLoginScreen
            );

            productPanel = new ProductPanel(productManagementController, this::navigateToUserMenu);
            customerPanel = new CustomerPanel(customerManagementController, this::navigateToUserMenu);
            cartPanel = new CartPanel(cartManagementController, this::navigateToUserMenu);
            transactionPanel = new TransactionPanel(transactionManagementController, this::navigateToUserMenu);
            employeePanel = new EmployeePanel(employeeManagementController, this::navigateToUserMenu);

            mainPanel.add(customerMenuPanel, CUSTOMER_MENU_CARD);
            mainPanel.add(employeeMenuPanel, EMPLOYEE_MENU_CARD);
            mainPanel.add(productPanel, PRODUCT_CARD);
            mainPanel.add(customerPanel, CUSTOMER_CARD);
            mainPanel.add(cartPanel, CART_CARD);
            mainPanel.add(transactionPanel, TRANSACTION_CARD);
            mainPanel.add(employeePanel, EMPLOYEE_CARD);

            mainPanel.revalidate();
            mainPanel.repaint();

            initializeApplicationData();

        } catch (Exception e) {
            handleUIException(e, "creating application panels", "Failed to initialize application interface.");
        }
    }

    private void initializeApplicationData() {
        try {
            dataManagementController.handleLoadData();

            List<ProductDTO> allProducts = productManagementController.getAllProductsDTO();

            if (allProducts == null || allProducts.isEmpty()) {
                dataManagementController.initializeSampleData();
                allProducts = productManagementController.getAllProductsDTO();
            }

            if (allProducts != null && !allProducts.isEmpty()) {
                displayProducts(allProducts);
            }

        } catch (Exception e) {
            handleUIException(e, "loading initial data", "Failed to load application data on startup.");
        }

        showLoginScreen();
    }

    // ==================== NAVIGATION METHODS ====================

    private void navigateToScreen(String screenName) {
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    cardLayout.show(mainPanel, screenName);
                } catch (Exception e) {
                    handleUIException(e, "navigating to " + screenName, "Navigation failed.");
                }
            });
        } catch (Exception e) {
            handleUIException(e, "scheduling navigation", "Navigation scheduling failed.");
        }
    }

    private void handleExit() {
        try {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                applicationRunning = false;
                mainFrame.dispose();
            }
        } catch (Exception e) {
            handleUIException(e, "exiting application", "Failed to exit properly.");
            applicationRunning = false;
            mainFrame.dispose();
        }
    }

    private void showLoginScreen() {
        SwingUtilities.invokeLater(() -> {
            performLoginProcess();
        });
    }

    private void performLoginProcess() {
        boolean exit = false;

        while (applicationRunning && !exit) {
            mainFrame.setVisible(false);
            UserSession.getInstance().logout();

            String userType = UserTypeSelectionDialog.showDialog(mainFrame);

            if (userType == null) {
                handleExit();
                exit = true;
            } else if (attemptLogin(userType))
                exit = true;
        }
    }

    private boolean attemptLogin(String userType) {
        boolean temp = false;

        if ("CUSTOMER".equals(userType)) {
            UserSession.getInstance().login("CUSTOMER", "CUSTOMER");
            navigateToScreen(CUSTOMER_MENU_CARD);
            mainFrame.setVisible(true);
            temp = true;
        } else if ("EMPLOYEE".equals(userType)) {
            EmployeeLoginDialog.LoginResult result = EmployeeLoginDialog.showLoginDialog(mainFrame, employeeController);
            if (result.isAuthenticated()) {
                UserSession.getInstance().login(result.getEmployeeId(), "EMPLOYEE");
                navigateToScreen(EMPLOYEE_MENU_CARD);
                mainFrame.setVisible(true);
                temp = true;
            }
        } else {
            displayErrorMessage("Invalid user type selected.");
        }

        return temp;
    }
    private void navigateToUserMenu() {
        UserSession session = UserSession.getInstance();
        if (session.isCustomer()) {
            navigateToScreen(CUSTOMER_MENU_CARD);
        } else if (session.isEmployee()) {
            navigateToScreen(EMPLOYEE_MENU_CARD);
        } else {
            showLoginScreen();
        }
    }

    // ==================== DISPLAY METHODS ====================

    @Override
    public void displayWelcomeMessage() {
    }

    @Override
    public void displayMainMenu() {
    }

    @Override
    public int getMainMenuChoice() {
        return -1;
    }

    @Override
    public void displayProductMenu() {
        navigateToScreen(PRODUCT_CARD);
    }

    @Override
    public int getProductMenuChoice() {
        return -1;
    }

    @Override
    public void displayProducts(List<ProductDTO> products) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (productPanel != null) {
                    productPanel.displayProducts(products);
                }
                if (cartPanel != null) {
                    cartPanel.displayProducts(products);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying products", "Failed to display products.");
            }
        });
    }

    @Override
    public void displayProduct(ProductDTO product) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (productPanel != null) {
                    productPanel.displayProduct(product);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying product", "Failed to display product details.");
            }
        });
    }

    @Override
    public void displayLowStockProducts(List<ProductDTO> products) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (productPanel != null) {
                    productPanel.displayLowStockProducts(products);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying low stock products", "Failed to display low stock products.");
            }
        });
    }

    @Override
    public void displayExpiredProducts(List<ProductDTO> products) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (productPanel != null) {
                    productPanel.displayExpiredProducts(products);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying expired products", "Failed to display expired products.");
            }
        });
    }

    @Override
    public void displayCustomerMenu() {
        navigateToScreen(CUSTOMER_CARD);
    }

    @Override
    public int getCustomerMenuChoice() {
        return -1;
    }

    @Override
    public void displayCustomers(List<CustomerDTO> customers) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (customerPanel != null) {
                    customerPanel.displayCustomers(customers);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying customers", "Failed to display customers.");
            }
        });
    }

    @Override
    public void displayCustomer(CustomerDTO customer) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (customerPanel != null) {
                    customerPanel.displayCustomer(customer);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying customer", "Failed to display customer details.");
            }
        });
    }

    @Override
    public void displayCartMenu() {
        navigateToScreen(CART_CARD);
    }

    @Override
    public int getCartMenuChoice() {
        return -1;
    }

    @Override
    public void displayCart(CartDTO cart) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (cartPanel != null) {
                    cartPanel.displayCart(cart);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying cart", "Failed to display cart.");
            }
        });
    }

    @Override
    public void displayTransactionMenu() {
        navigateToScreen(TRANSACTION_CARD);
    }

    @Override
    public int getTransactionMenuChoice() {
        return -1;
    }

    @Override
    public void displayTransactions(List<TransactionDTO> transactions) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (transactionPanel != null) {
                    transactionPanel.displayTransactions(transactions);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying transactions", "Failed to display transactions.");
            }
        });
    }

    @Override
    public void displayTransaction(TransactionDTO transaction) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (transactionPanel != null) {
                    transactionPanel.displayTransaction(transaction);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying transaction", "Failed to display transaction details.");
            }
        });
    }

    @Override
    public void displayReceipt(String receipt) {
        SwingUtilities.invokeLater(() -> {
            try {
                JTextArea textArea = new JTextArea(receipt);
                textArea.setEditable(false);
                textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(500, 600));
                JOptionPane.showMessageDialog(mainFrame, scrollPane, "Receipt", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                handleUIException(e, "displaying receipt", "Failed to display receipt.");
            }
        });
    }

    @Override
    public void displayTotalSales(double totalSales) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (transactionPanel != null) {
                    transactionPanel.displayTotalSales(totalSales);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying total sales", "Failed to display total sales.");
            }
        });
    }

    @Override
    public void displayTotalSalesByDate(LocalDate date, double totalSales) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (transactionPanel != null) {
                    transactionPanel.displayTotalSalesByDate(date, totalSales);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying sales by date", "Failed to display sales by date.");
            }
        });
    }

    @Override
    public void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (transactionPanel != null) {
                    transactionPanel.displayTotalSalesByDateRange(startDate, endDate, totalSales);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying sales by date range", "Failed to display sales by date range.");
            }
        });
    }

    @Override
    public void displayEmployeeMenu() {
        navigateToScreen(EMPLOYEE_CARD);
    }

    @Override
    public int getEmployeeMenuChoice() {
        return -1;
    }

    @Override
    public void displayEmployees(List<EmployeeDTO> employees) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (employeePanel != null) {
                    employeePanel.displayEmployees(employees);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying employees", "Failed to display employees.");
            }
        });
    }

    @Override
    public void displayEmployee(EmployeeDTO employee) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (employeePanel != null) {
                    employeePanel.displayEmployee(employee);
                }
            } catch (Exception e) {
                handleUIException(e, "displaying employee", "Failed to display employee details.");
            }
        });
    }

    @Override
    public void displayErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                System.err.println("Failed to display error message: " + message);
            }
        });
    }

    @Override
    public void displaySuccessMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                System.err.println("Failed to display success message: " + message);
            }
        });
    }

    @Override
    public void displayInfoMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                JOptionPane.showMessageDialog(mainFrame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                System.err.println("Failed to display info message: " + message);
            }
        });
    }

    // ==================== INPUT METHODS ====================

    @Override
    public String getStringInput(String prompt) {
        try {
            return JOptionPane.showInputDialog(mainFrame, prompt);
        } catch (Exception e) {
            handleUIException(e, "getting string input", "Input dialog failed.");
            return null;
        }
    }

    @Override
    public int getIntInput(String prompt) {
        int result = 0;
        boolean validInput = false;
        boolean shouldExitLoop = false;

        while (!validInput && !shouldExitLoop && applicationRunning) {
            String input = getStringInput(prompt);

            if (input == null) {
                shouldExitLoop = true; // User cancelled
            } else {
                try {
                    result = Integer.parseInt(input.trim());
                    validInput = true;
                } catch (NumberFormatException e) {
                    displayErrorMessage("Invalid integer. Please enter a valid number.");
                } catch (Exception e) {
                    handleUIException(e, "parsing integer input", "Input processing failed.");
                    shouldExitLoop = true; // Fatal error
                }
            }
        }

        return result;
    }

    @Override
    public double getDoubleInput(String prompt) {
        double result = 0.0;
        boolean validInput = false;
        boolean userCancelled = false;
        boolean fatalError = false;

        while (!validInput && !userCancelled && !fatalError && applicationRunning) {
            String input = getStringInput(prompt);

            if (input == null) {
                userCancelled = true;
            } else {
                try {
                    result = Double.parseDouble(input.trim());
                    validInput = true;
                } catch (NumberFormatException e) {
                    displayErrorMessage("Invalid number. Please enter a valid decimal number.");
                } catch (Exception e) {
                    handleUIException(e, "parsing double input", "Input processing failed.");
                    fatalError = true;
                }
            }
        }

        return result;
    }

    @Override
    public boolean getBooleanInput(String prompt) {
        try {
            int result = JOptionPane.showConfirmDialog(mainFrame, prompt, "Confirm", JOptionPane.YES_NO_OPTION);
            return result == JOptionPane.YES_OPTION;
        } catch (Exception e) {
            handleUIException(e, "getting boolean input", "Confirmation dialog failed.");
            return false;
        }
    }

    @Override
    public LocalDate getDateInput(String prompt) {
        LocalDate result = null;
        boolean validInput = false;
        boolean userCancelled = false;
        boolean fatalError = false;

        while (!validInput && !userCancelled && !fatalError && applicationRunning) {
            String input = getStringInput(prompt + " (YYYY-MM-DD)");

            if (input == null) {
                userCancelled = true;
            } else if (input.trim().isEmpty()) {
                validInput = true;
            } else {
                try {
                    result = LocalDate.parse(input.trim());
                    validInput = true;
                } catch (DateTimeParseException e) {
                    displayErrorMessage("Invalid date format. Please use YYYY-MM-DD.");
                } catch (Exception e) {
                    handleUIException(e, "parsing date input", "Date input processing failed.");
                    fatalError = true;
                }
            }
        }

        return result;
    }

    @Override
    public ProductCategory getCategoryInput() {
        try {
            ProductCategory[] categories = ProductCategory.values();
            ProductCategory selected = (ProductCategory) JOptionPane.showInputDialog(
                    mainFrame, "Select category:", "Category", JOptionPane.QUESTION_MESSAGE, null, categories, categories[0]
            );
            return selected != null ? selected : categories[0];
        } catch (Exception e) {
            handleUIException(e, "getting category input", "Category selection failed.");
            return ProductCategory.values()[0];
        }
    }

    @Override
    public ProductSubcategory getSubcategoryInput(ProductCategory category) {
        ProductSubcategory temp = null;

        try {
            if (category != null) {
                ProductSubcategory[] subcategories = ProductSubcategory.getSubcategoriesFor(category);
                if (subcategories.length > 0) {
                    temp = (ProductSubcategory) JOptionPane.showInputDialog(
                            mainFrame, "Select subcategory:", "Subcategory", JOptionPane.QUESTION_MESSAGE, null, subcategories, subcategories[0]
                    );
                }
            } else {
                throw new IllegalArgumentException("Category cannot be null");
            }
        } catch (Exception e) {
            handleUIException(e, "getting subcategory input", "Subcategory selection failed.");
        }

        return temp;
    }
    // ==================== VALIDATION METHODS ====================

    private void validateControllerDependencies(
            ProductManagementController productManagementController,
            CustomerManagementController customerManagementController,
            CartManagementController cartManagementController,
            TransactionManagementController transactionManagementController,
            DataManagementController dataManagementController) {
        if (productManagementController == null) {
            throw new IllegalArgumentException("ProductManagementController cannot be null");
        }
        if (customerManagementController == null) {
            throw new IllegalArgumentException("CustomerManagementController cannot be null");
        }
        if (cartManagementController == null) {
            throw new IllegalArgumentException("CartManagementController cannot be null");
        }
        if (transactionManagementController == null) {
            throw new IllegalArgumentException("TransactionManagementController cannot be null");
        }
        if (dataManagementController == null) {
            throw new IllegalArgumentException("DataManagementController cannot be null");
        }
    }

    // ==================== ERROR HANDLING ====================

    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("SwingStoreView Error " + context + ": " + e.getMessage());
        displayErrorMessage(userMessage);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public boolean isApplicationRunning() {
        return applicationRunning;
    }
}
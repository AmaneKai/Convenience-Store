package com.konbini.view.swing;

import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;

import com.konbini.controller.*;
import com.konbini.dto.*;
import com.konbini.model.*;
import com.konbini.util.UserSession;
import com.konbini.view.*;

/**
 * Main Swing-based GUI implementation of the StoreView interface.
 * Provides a comprehensive graphical user interface for the store management system
 * using Java Swing components with card-based navigation.
 *
 * Features include:
 * - Multi-panel card layout navigation
 * - User authentication (customer/employee)
 * - Integrated view for all store operations
 * - Exception handling and user feedback
 * - Responsive UI with proper threading
 */
public class SwingStoreView implements MainView, ProductView, CustomerView, CartView, TransactionView, EmployeeView {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private java.awt.CardLayout cardLayout;

    // Panel components for different application sections
    private CustomerMenuPanel customerMenuPanel;
    private EmployeeMenuPanel employeeMenuPanel;
    private ProductPanel productPanel;
    private CustomerPanel customerPanel;
    private CartPanel cartPanel;
    private TransactionPanel transactionPanel;
    private EmployeePanel employeePanel;

    // Controller dependencies
    private ProductManagementController productManagementController;
    private CustomerManagementController customerManagementController;
    private CartManagementController cartManagementController;
    private TransactionManagementController transactionManagementController;
    private DataManagementController dataManagementController;
    private EmployeeController employeeController;
    private EmployeeManagementController employeeManagementController;

    // Card layout identifiers
    private static final String CUSTOMER_MENU_CARD = "CustomerMenu";
    private static final String EMPLOYEE_MENU_CARD = "EmployeeMenu";
    private static final String PRODUCT_CARD = "Product";
    private static final String CUSTOMER_CARD = "Customer";
    private static final String CART_CARD = "Cart";
    private static final String TRANSACTION_CARD = "Transaction";
    private static final String EMPLOYEE_CARD = "Employee";

    private boolean applicationRunning = true;

    /**
     * Constructs a new SwingStoreView and initializes the GUI components.
     */
    public SwingStoreView() {
        initializeGUI();
    }

    /**
     * Sets all controller dependencies required for the view to function.
     * Validates dependencies and initializes panels with controllers.
     *
     * @param productManagementController controller for product operations
     * @param customerManagementController controller for customer operations
     * @param cartManagementController controller for cart operations
     * @param transactionManagementController controller for transaction operations
     * @param dataManagementController controller for data management operations
     * @param employeeController controller for employee operations
     * @param employeeManagementController controller for employee management operations
     */
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

    /**
     * Initializes the main GUI components including window, layout, and basic styling.
     */
    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            handleUIException(e, "setting look and feel", "Warning: Could not set system look and feel.");
        }

        try {
            mainFrame = new JFrame("コンビニ");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    /**
     * Creates and initializes all specialized panels with their respective controllers.
     */
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

    /**
     * Initializes application data on startup, loading existing data or creating sample data.
     */
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

    /**
     * Navigates to the specified screen using card layout.
     *
     * @param screenName the name of the screen/card to display
     */
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

    /**
     * Handles application exit with confirmation dialog.
     */
    private void handleExit() {
        try {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                if (dataManagementController != null) {
                    dataManagementController.handleSaveData();
                    // Show success message synchronously before exiting
                    JOptionPane.showMessageDialog(mainFrame, "Data saved successfully.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

                applicationRunning = false;

                // Dispatch a WINDOW_CLOSING event to trigger EXIT_ON_CLOSE behavior
                mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
            }
        } catch (Exception e) {
            handleUIException(e, "exiting application", "Failed to exit properly.");
            applicationRunning = false;
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * Shows the login screen and initiates authentication process.
     */
    private void showLoginScreen() {
        SwingUtilities.invokeLater(() -> {
            performLoginProcess();
        });
    }

    /**
     * Performs the complete login process including user type selection and authentication.
     */
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

    /**
     * Attempts to login a user of the specified type.
     *
     * @param userType the type of user ("CUSTOMER" or "EMPLOYEE")
     * @return true if login was successful, false otherwise
     */
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

    /**
     * Navigates to the appropriate user menu based on current session.
     */
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

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by navigation.
     */
    @Override
    public void displayWelcomeMessage() {
    }

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by navigation.
     */
    @Override
    public void displayMainMenu() {
    }

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by navigation.
     */
    @Override
    public int getMainMenuChoice() {
        return -1;
    }

    /**
     * {@inheritDoc}
     * Navigates to the product management screen.
     */
    @Override
    public void displayProductMenu() {
        navigateToScreen(PRODUCT_CARD);
    }

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by panel components.
     */
    @Override
    public int getProductMenuChoice() {
        return -1;
    }

    /**
     * {@inheritDoc}
     * Displays products in both product and cart panels.
     */
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

    /**
     * {@inheritDoc}
     * Displays detailed product information in the product panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays low stock products in the product panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays expired products in the product panel.
     */
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

    /**
     * {@inheritDoc}
     * Navigates to the customer management screen.
     */
    @Override
    public void displayCustomerMenu() {
        navigateToScreen(CUSTOMER_CARD);
    }

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by panel components.
     */
    @Override
    public int getCustomerMenuChoice() {
        return -1;
    }

    /**
     * {@inheritDoc}
     * Displays customers in the customer panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays detailed customer information in the customer panel.
     */
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

    /**
     * {@inheritDoc}
     * Navigates to the cart management screen.
     */
    @Override
    public void displayCartMenu() {
        navigateToScreen(CART_CARD);
    }

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by panel components.
     */
    @Override
    public int getCartMenuChoice() {
        return -1;
    }

    /**
     * {@inheritDoc}
     * Displays cart contents in the cart panel.
     */
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

    /**
     * {@inheritDoc}
     * Navigates to the transaction management screen.
     */
    @Override
    public void displayTransactionMenu() {
        navigateToScreen(TRANSACTION_CARD);
    }

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by panel components.
     */
    @Override
    public int getTransactionMenuChoice() {
        return -1;
    }

    /**
     * {@inheritDoc}
     * Displays transactions in the transaction panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays detailed transaction information in the transaction panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays a receipt in a scrollable dialog with monospaced font.
     */
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

    /**
     * {@inheritDoc}
     * Displays total sales in the transaction panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays sales for a specific date in the transaction panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays sales for a date range in the transaction panel.
     */
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

    /**
     * {@inheritDoc}
     * Navigates to the employee management screen.
     */
    @Override
    public void displayEmployeeMenu() {
        navigateToScreen(EMPLOYEE_CARD);
    }

    /**
     * {@inheritDoc}
     * Not implemented in Swing version - handled by panel components.
     */
    @Override
    public int getEmployeeMenuChoice() {
        return -1;
    }

    /**
     * {@inheritDoc}
     * Displays employees in the employee panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays detailed employee information in the employee panel.
     */
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

    /**
     * {@inheritDoc}
     * Displays an error message in a dialog box.
     */
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

    /**
     * {@inheritDoc}
     * Displays a success message in a dialog box.
     */
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

    /**
     * {@inheritDoc}
     * Displays an informational message in a dialog box.
     */
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

    /**
     * {@inheritDoc}
     * Gets string input from the user via a dialog box.
     */
    @Override
    public String getStringInput(String prompt) {
        try {
            return JOptionPane.showInputDialog(mainFrame, prompt);
        } catch (Exception e) {
            handleUIException(e, "getting string input", "Input dialog failed.");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * Gets integer input from the user with validation and error handling.
     */
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

    /**
     * {@inheritDoc}
     * Gets double input from the user with validation and error handling.
     */
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

    /**
     * {@inheritDoc}
     * Gets boolean input from the user via a confirmation dialog.
     */
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

    /**
     * {@inheritDoc}
     * Gets date input from the user with format validation.
     */
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

    /**
     * {@inheritDoc}
     * Gets product category input from the user via a selection dialog.
     */
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

    /**
     * {@inheritDoc}
     * Gets product subcategory input from the user based on the selected category.
     */
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

    /**
     * Validates that all required controller dependencies are provided.
     *
     * @param productManagementController product management controller
     * @param customerManagementController customer management controller
     * @param cartManagementController cart management controller
     * @param transactionManagementController transaction management controller
     * @param dataManagementController data management controller
     * @throws IllegalArgumentException if any controller is null
     */
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

    /**
     * Handles UI-related exceptions by logging and displaying user-friendly messages.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     * @param userMessage the message to display to the user
     */
    private void handleUIException(Exception e, String context, String userMessage) {
        System.err.println("SwingStoreView Error " + context + ": " + e.getMessage());
        displayErrorMessage(userMessage);
    }

    /**
     * Gets the main application frame.
     *
     * @return the main JFrame instance
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Checks if the application is currently running.
     *
     * @return true if the application is running, false otherwise
     */
    public boolean isApplicationRunning() {
        return applicationRunning;
    }
}
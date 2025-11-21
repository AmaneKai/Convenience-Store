package com.konbini.view.swing;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.konbini.controller.CartManagementController;
import com.konbini.controller.CustomerManagementController;
import com.konbini.controller.DataManagementController;
import com.konbini.controller.ProductManagementController;
import com.konbini.controller.TransactionManagementController;
import com.konbini.dto.CartDTO;
import com.konbini.dto.CustomerDTO;
import com.konbini.dto.ProductDTO;
import com.konbini.dto.TransactionDTO;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.view.StoreView;

public class SwingStoreView implements StoreView {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private java.awt.CardLayout cardLayout;

    private MainMenuPanel mainMenuPanel;
    private ProductPanel productPanel;
    private CustomerPanel customerPanel;
    private CartPanel cartPanel;
    private TransactionPanel transactionPanel;

    private ProductManagementController productManagementController;
    private CustomerManagementController customerManagementController;
    private CartManagementController cartManagementController;
    private TransactionManagementController transactionManagementController;
    private DataManagementController dataManagementController;

    private static final String MAIN_MENU_CARD = "MainMenu";
    private static final String PRODUCT_CARD = "Product";
    private static final String CUSTOMER_CARD = "Customer";
    private static final String CART_CARD = "Cart";
    private static final String TRANSACTION_CARD = "Transaction";

    public SwingStoreView() {
        initializeGUI();
    }

    public void setControllers(
            ProductManagementController productManagementController,
            CustomerManagementController customerManagementController,
            CartManagementController cartManagementController,
            TransactionManagementController transactionManagementController,
            DataManagementController dataManagementController) {
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
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1024, 768);

            cardLayout = new java.awt.CardLayout();
            mainPanel = new JPanel(cardLayout);

            mainFrame.add(mainPanel);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);

        } catch (Exception e) {
            handleUIException(e, "initializing main window", "Failed to initialize application window.");
            System.exit(1);
        }
    }

    private void createPanelsWithControllers() {
        try {
            mainPanel.removeAll();

            mainMenuPanel = new MainMenuPanel(
                    this.mainFrame,
                    this::navigateToScreen,
                    this::handleExit,
                    dataManagementController
            );

            productPanel = new ProductPanel(productManagementController, this::navigateToMainMenu);
            customerPanel = new CustomerPanel(customerManagementController, this::navigateToMainMenu);
            cartPanel = new CartPanel(cartManagementController, this::navigateToMainMenu);
            transactionPanel = new TransactionPanel(transactionManagementController, this::navigateToMainMenu);

            mainPanel.add(mainMenuPanel, MAIN_MENU_CARD);
            mainPanel.add(productPanel, PRODUCT_CARD);
            mainPanel.add(customerPanel, CUSTOMER_CARD);
            mainPanel.add(cartPanel, CART_CARD);
            mainPanel.add(transactionPanel, TRANSACTION_CARD);

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

        navigateToScreen(MAIN_MENU_CARD);
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

    private void navigateToMainMenu() {
        navigateToScreen(MAIN_MENU_CARD);
    }

    private void handleExit() {
        try {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } catch (Exception e) {
            handleUIException(e, "exiting application", "Failed to exit properly.");
        }
    }

    // ==================== DISPLAY METHODS ====================

    @Override
    public void displayWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (mainMenuPanel != null) {
                    mainMenuPanel.showWelcomeMessage();
                }
            } catch (Exception e) {
                handleUIException(e, "displaying welcome message", "Failed to show welcome message.");
            }
        });
    }

    @Override
    public void displayMainMenu() {
        navigateToScreen(MAIN_MENU_CARD);
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
        boolean userCancelled = false;

        while (!validInput && !userCancelled) {
            String input = getStringInput(prompt);

            if (input == null) {
                userCancelled = true;
            } else {
                try {
                    result = Integer.parseInt(input.trim());
                    validInput = true;
                } catch (NumberFormatException e) {
                    displayErrorMessage("Invalid integer. Please enter a valid number.");
                } catch (Exception e) {
                    handleUIException(e, "parsing integer input", "Input processing failed.");
                    break;
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

        while (!validInput && !userCancelled) {
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
                    break;
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
        LocalDate result = LocalDate.now();
        boolean validInput = false;
        boolean userCancelled = false;

        while (!validInput && !userCancelled) {
            String input = getStringInput(prompt + " (YYYY-MM-DD)");

            if (input == null) {
                userCancelled = true;
            } else {
                try {
                    result = LocalDate.parse(input.trim());
                    validInput = true;
                } catch (DateTimeParseException e) {
                    displayErrorMessage("Invalid date format. Please use YYYY-MM-DD.");
                } catch (Exception e) {
                    handleUIException(e, "parsing date input", "Date input processing failed.");
                    break;
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
        try {
            if (category == null) {
                throw new IllegalArgumentException("Category cannot be null");
            }

            ProductSubcategory[] subcategories = ProductSubcategory.getSubcategoriesFor(category);
            if (subcategories.length == 0) return null;

            ProductSubcategory selected = (ProductSubcategory) JOptionPane.showInputDialog(
                    mainFrame, "Select subcategory:", "Subcategory", JOptionPane.QUESTION_MESSAGE, null, subcategories, subcategories[0]
            );
            return selected;
        } catch (Exception e) {
            handleUIException(e, "getting subcategory input", "Subcategory selection failed.");
            return null;
        }
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
}
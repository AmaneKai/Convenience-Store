package com.konbini.view.swing;

import com.konbini.dto.*;
import com.konbini.model.*;
import com.konbini.view.StoreView;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Swing-based implementation of the StoreView interface.
 * This class serves as the main GUI coordinator, delegating to specialized
 * panel components for different functional areas (Products, Customers, Cart, Transactions).
 * 
 * Architecture:
 * - Uses CardLayout to switch between different main screens
 * - Delegates display logic to specialized panel classes
 * - Handles user input through modal dialogs
 * - Thread-safe updates using SwingUtilities.invokeLater()
 */
public class SwingStoreView implements StoreView {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private java.awt.CardLayout cardLayout;
    
    // Specialized panel components
    private MainMenuPanel mainMenuPanel;
    private ProductPanel productPanel;
    private CustomerPanel customerPanel;
    private CartPanel cartPanel;
    private TransactionPanel transactionPanel;
    
    // Card names for CardLayout
    private static final String MAIN_MENU_CARD = "MainMenu";
    private static final String PRODUCT_CARD = "Product";
    private static final String CUSTOMER_CARD = "Customer";
    private static final String CART_CARD = "Cart";
    private static final String TRANSACTION_CARD = "Transaction";
    
    /**
     * Constructs the SwingStoreView and initializes all GUI components.
     */
    public SwingStoreView() {
        initializeGUI();
    }
    
    /**
     * Initializes the main frame and all panel components.
     */
    private void initializeGUI() {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default if system L&F fails
        }
        
        mainFrame = new JFrame("Konbini Store - Point of Sale System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1024, 768);
        
        // Initialize CardLayout for switching between screens
        cardLayout = new java.awt.CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Initialize all specialized panels
        initializePanels();
        
        // Add panels to CardLayout
        mainPanel.add(mainMenuPanel, MAIN_MENU_CARD);
        mainPanel.add(productPanel, PRODUCT_CARD);
        mainPanel.add(customerPanel, CUSTOMER_CARD);
        mainPanel.add(cartPanel, CART_CARD);
        mainPanel.add(transactionPanel, TRANSACTION_CARD);
        
        mainFrame.add(mainPanel);
        mainFrame.setLocationRelativeTo(null); // Center on screen
        mainFrame.setVisible(true);
    }
    
    /**
     * Initializes all specialized panel components with navigation callbacks.
     */
    private void initializePanels() {
        mainMenuPanel = new MainMenuPanel(this::navigateToScreen);
        productPanel = new ProductPanel(this::navigateToMainMenu);
        customerPanel = new CustomerPanel(this::navigateToMainMenu);
        cartPanel = new CartPanel(this::navigateToMainMenu);
        transactionPanel = new TransactionPanel(this::navigateToMainMenu);
    }
    
    /**
     * Navigates to a specific screen by name.
     */
    private void navigateToScreen(String screenName) {
        SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, screenName));
    }
    
    /**
     * Returns to the main menu.
     */
    private void navigateToMainMenu() {
        navigateToScreen(MAIN_MENU_CARD);
    }
    
    // ==================== MainView Methods ====================
    
    @Override
    public void displayWelcomeMessage() {
        SwingUtilities.invokeLater(() -> 
            mainMenuPanel.showWelcomeMessage()
        );
    }
    
    @Override
    public void displayMainMenu() {
        navigateToScreen(MAIN_MENU_CARD);
    }
    
    @Override
    public int getMainMenuChoice() {
        return mainMenuPanel.getMenuChoice();
    }
    
    // ==================== ProductView Methods ====================
    
    @Override
    public void displayProductMenu() {
        navigateToScreen(PRODUCT_CARD);
    }
    
    @Override
    public int getProductMenuChoice() {
        return productPanel.getMenuChoice();
    }
    
    @Override
    public void displayProducts(List<Product> products) {
        List<ProductDTO> dtos = ProductDTO.fromModelList(products);
        SwingUtilities.invokeLater(() -> 
            productPanel.displayProducts(dtos)
        );
    }
    
    @Override
    public void displayProduct(Product product) {
        ProductDTO dto = ProductDTO.fromModel(product);
        SwingUtilities.invokeLater(() -> 
            productPanel.displayProduct(dto)
        );
    }
    
    @Override
    public void displayLowStockProducts(List<Product> products) {
        List<ProductDTO> dtos = ProductDTO.fromModelList(products);
        SwingUtilities.invokeLater(() -> 
            productPanel.displayLowStockProducts(dtos)
        );
    }
    
    @Override
    public void displayExpiredProducts(List<Product> products) {
        List<ProductDTO> dtos = ProductDTO.fromModelList(products);
        SwingUtilities.invokeLater(() -> 
            productPanel.displayExpiredProducts(dtos)
        );
    }
    
    // ==================== CustomerView Methods ====================
    
    @Override
    public void displayCustomerMenu() {
        navigateToScreen(CUSTOMER_CARD);
    }
    
    @Override
    public int getCustomerMenuChoice() {
        return customerPanel.getMenuChoice();
    }
    
    @Override
    public void displayCustomers(List<Customer> customers) {
        List<CustomerDTO> dtos = customers.stream()
            .map(CustomerDTO::fromModel)
            .collect(java.util.stream.Collectors.toList());
        SwingUtilities.invokeLater(() -> 
            customerPanel.displayCustomers(dtos)
        );
    }
    
    @Override
    public void displayCustomer(Customer customer) {
        CustomerDTO dto = CustomerDTO.fromModel(customer);
        SwingUtilities.invokeLater(() -> 
            customerPanel.displayCustomer(dto)
        );
    }
    
    // ==================== CartView Methods ====================
    
    @Override
    public void displayCartMenu() {
        navigateToScreen(CART_CARD);
    }
    
    @Override
    public int getCartMenuChoice() {
        return cartPanel.getMenuChoice();
    }
    
    @Override
    public void displayCart(Cart cart) {
        CartDTO dto = CartDTO.fromModel(cart);
        SwingUtilities.invokeLater(() -> 
            cartPanel.displayCart(dto)
        );
    }
    
    // ==================== TransactionView Methods ====================
    
    @Override
    public void displayTransactionMenu() {
        navigateToScreen(TRANSACTION_CARD);
    }
    
    @Override
    public int getTransactionMenuChoice() {
        return transactionPanel.getMenuChoice();
    }
    
    @Override
    public void displayTransactions(List<Transaction> transactions) {
        List<TransactionDTO> dtos = transactions.stream()
            .map(TransactionDTO::fromModel)
            .collect(java.util.stream.Collectors.toList());
        SwingUtilities.invokeLater(() -> 
            transactionPanel.displayTransactions(dtos)
        );
    }
    
    @Override
    public void displayTransaction(Transaction transaction) {
        TransactionDTO dto = TransactionDTO.fromModel(transaction);
        SwingUtilities.invokeLater(() -> 
            transactionPanel.displayTransaction(dto)
        );
    }
    
    @Override
    public void displayReceipt(String receipt) {
        SwingUtilities.invokeLater(() -> {
            JTextArea textArea = new JTextArea(receipt);
            textArea.setEditable(false);
            textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(500, 600));
            JOptionPane.showMessageDialog(mainFrame, scrollPane, 
                "Transaction Receipt", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    @Override
    public void displayTotalSales(double totalSales) {
        SwingUtilities.invokeLater(() -> 
            transactionPanel.displayTotalSales(totalSales)
        );
    }
    
    @Override
    public void displayTotalSalesByDate(LocalDate date, double totalSales) {
        SwingUtilities.invokeLater(() -> 
            transactionPanel.displayTotalSalesByDate(date, totalSales)
        );
    }
    
    @Override
    public void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales) {
        SwingUtilities.invokeLater(() -> 
            transactionPanel.displayTotalSalesByDateRange(startDate, endDate, totalSales)
        );
    }
    
    // ==================== Message Display Methods ====================
    
    @Override
    public void displayErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(mainFrame, message, 
                "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
    
    @Override
    public void displaySuccessMessage(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(mainFrame, message, 
                "Success", JOptionPane.INFORMATION_MESSAGE)
        );
    }
    
    @Override
    public void displayInfoMessage(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(mainFrame, message, 
                "Information", JOptionPane.INFORMATION_MESSAGE)
        );
    }
    
    // ==================== Input Methods ====================
    
    @Override
    public String getStringInput(String prompt) {
        return JOptionPane.showInputDialog(mainFrame, prompt);
    }
    
    @Override
    public int getIntInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt);
            if (input == null) return 0; // User cancelled
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                displayErrorMessage("Please enter a valid integer.");
            }
        }
    }
    
    @Override
    public double getDoubleInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt);
            if (input == null) return 0.0; // User cancelled
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                displayErrorMessage("Please enter a valid number.");
            }
        }
    }
    
    @Override
    public boolean getBooleanInput(String prompt) {
        int result = JOptionPane.showConfirmDialog(mainFrame, prompt, 
            "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    @Override
    public LocalDate getDateInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt + " (YYYY-MM-DD)");
            if (input == null) return LocalDate.now(); // User cancelled
            try {
                return LocalDate.parse(input.trim());
            } catch (Exception e) {
                displayErrorMessage("Please enter a valid date in YYYY-MM-DD format.");
            }
        }
    }
    
    @Override
    public ProductCategory getCategoryInput() {
        ProductCategory[] categories = ProductCategory.values();
        ProductCategory selected = (ProductCategory) JOptionPane.showInputDialog(
            mainFrame,
            "Select a product category:",
            "Product Category",
            JOptionPane.QUESTION_MESSAGE,
            null,
            categories,
            categories[0]
        );
        return selected != null ? selected : categories[0];
    }
    
    @Override
    public ProductSubcategory getSubcategoryInput(ProductCategory category) {
        ProductSubcategory[] subcategories = ProductSubcategory.getSubcategoriesFor(category);
        if (subcategories.length == 0) return null;
        
        ProductSubcategory selected = (ProductSubcategory) JOptionPane.showInputDialog(
            mainFrame,
            "Select a subcategory:",
            "Product Subcategory",
            JOptionPane.QUESTION_MESSAGE,
            null,
            subcategories,
            subcategories[0]
        );
        return selected;
    }
    
    /**
     * Gets the main JFrame for this view.
     * Useful for creating child dialogs.
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }
}

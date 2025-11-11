package com.konbini.view.swing;

import java.time.LocalDate;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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

/**
 * Swing-based GUI implementation - fully event-driven.
 */
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
    
    /**
     * Must be called BEFORE showing the GUI to inject controllers.
     */
    public void setControllers(
            ProductManagementController productManagementController,
            CustomerManagementController customerManagementController,
            CartManagementController cartManagementController,
            TransactionManagementController transactionManagementController,
            DataManagementController dataManagementController) {
        this.productManagementController = productManagementController;
        this.customerManagementController = customerManagementController;
        this.cartManagementController = cartManagementController;
        this.transactionManagementController = transactionManagementController;
        this.dataManagementController = dataManagementController;
        
        // NOW create panels with actual controllers
        createPanelsWithControllers();
    }
    
    private void initializeGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore
        }
        
        mainFrame = new JFrame("コンビニ");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1024, 768);
        
        cardLayout = new java.awt.CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainFrame.add(mainPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
   
    /**
     * Create all panels with controllers - called AFTER controllers are set.
     */
    private void createPanelsWithControllers() {
        // Remove old panels if they exist
        mainPanel.removeAll();
        
        // Create panels with controllers
        mainMenuPanel = new MainMenuPanel(
            this::navigateToScreen,
            () -> System.exit(0),
            productManagementController,
            customerManagementController,
            cartManagementController,
            transactionManagementController,
            dataManagementController
        );
        
        productPanel = new ProductPanel(productManagementController, this::navigateToMainMenu);
        customerPanel = new CustomerPanel(customerManagementController, this::navigateToMainMenu);
        cartPanel = new CartPanel(cartManagementController, this::navigateToMainMenu);
        transactionPanel = new TransactionPanel(transactionManagementController, this::navigateToMainMenu);
        
        // Add to CardLayout
        mainPanel.add(mainMenuPanel, MAIN_MENU_CARD);
        mainPanel.add(productPanel, PRODUCT_CARD);
        mainPanel.add(customerPanel, CUSTOMER_CARD);
        mainPanel.add(cartPanel, CART_CARD);
        mainPanel.add(transactionPanel, TRANSACTION_CARD);
        
        mainPanel.revalidate();
        mainPanel.repaint();
        
        // Show main menu
        navigateToScreen(MAIN_MENU_CARD);
    }
    
    private void navigateToScreen(String screenName) {
        SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, screenName));
    }
    
    private void navigateToMainMenu() {
        navigateToScreen(MAIN_MENU_CARD);
    }
    
    // ==================== Display Methods ====================
    
    @Override
    public void displayWelcomeMessage() {
        SwingUtilities.invokeLater(() -> mainMenuPanel.showWelcomeMessage());
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
        SwingUtilities.invokeLater(() -> productPanel.displayProducts(products));
    }
    
    @Override
    public void displayProduct(ProductDTO product) {
        SwingUtilities.invokeLater(() -> productPanel.displayProduct(product));
    }
    
    @Override
    public void displayLowStockProducts(List<ProductDTO> products) {
        SwingUtilities.invokeLater(() -> productPanel.displayLowStockProducts(products));
    }
    
    @Override
    public void displayExpiredProducts(List<ProductDTO> products) {
        SwingUtilities.invokeLater(() -> productPanel.displayExpiredProducts(products));
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
        SwingUtilities.invokeLater(() -> customerPanel.displayCustomers(customers));
    }
    
    @Override
    public void displayCustomer(CustomerDTO customer) {
        SwingUtilities.invokeLater(() -> customerPanel.displayCustomer(customer));
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
        SwingUtilities.invokeLater(() -> cartPanel.displayCart(cart));
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
        SwingUtilities.invokeLater(() -> transactionPanel.displayTransactions(transactions));
    }
    
    @Override
    public void displayTransaction(TransactionDTO transaction) {
        SwingUtilities.invokeLater(() -> transactionPanel.displayTransaction(transaction));
    }
    
    @Override
    public void displayReceipt(String receipt) {
        SwingUtilities.invokeLater(() -> {
            JTextArea textArea = new JTextArea(receipt);
            textArea.setEditable(false);
            textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(500, 600));
            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Receipt", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    @Override
    public void displayTotalSales(double totalSales) {
        SwingUtilities.invokeLater(() -> transactionPanel.displayTotalSales(totalSales));
    }
    
    @Override
    public void displayTotalSalesByDate(LocalDate date, double totalSales) {
        SwingUtilities.invokeLater(() -> transactionPanel.displayTotalSalesByDate(date, totalSales));
    }
    
    @Override
    public void displayTotalSalesByDateRange(LocalDate startDate, LocalDate endDate, double totalSales) {
        SwingUtilities.invokeLater(() -> transactionPanel.displayTotalSalesByDateRange(startDate, endDate, totalSales));
    }
    
    @Override
    public void displayErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE));
    }
    
    @Override
    public void displaySuccessMessage(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE));
    }
    
    @Override
    public void displayInfoMessage(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, message, "Info", JOptionPane.INFORMATION_MESSAGE));
    }
    
    @Override
    public String getStringInput(String prompt) {
        return JOptionPane.showInputDialog(mainFrame, prompt);
    }
    
    @Override
    public int getIntInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt);
            if (input == null) return 0;
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
            if (input == null) return 0.0;
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                displayErrorMessage("Please enter a valid number.");
            }
        }
    }
    
    @Override
    public boolean getBooleanInput(String prompt) {
        int result = JOptionPane.showConfirmDialog(mainFrame, prompt, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    @Override
    public LocalDate getDateInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt + " (YYYY-MM-DD)");
            if (input == null) return LocalDate.now();
            try {
                return LocalDate.parse(input.trim());
            } catch (Exception e) {
                displayErrorMessage("Please enter a valid date (YYYY-MM-DD).");
            }
        }
    }
    
    @Override
    public ProductCategory getCategoryInput() {
        ProductCategory[] categories = ProductCategory.values();
        ProductCategory selected = (ProductCategory) JOptionPane.showInputDialog(
            mainFrame, "Select category:", "Category", JOptionPane.QUESTION_MESSAGE, null, categories, categories[0]
        );
        return selected != null ? selected : categories[0];
    }
    
    @Override
    public ProductSubcategory getSubcategoryInput(ProductCategory category) {
        ProductSubcategory[] subcategories = ProductSubcategory.getSubcategoriesFor(category);
        if (subcategories.length == 0) return null;
        ProductSubcategory selected = (ProductSubcategory) JOptionPane.showInputDialog(
            mainFrame, "Select subcategory:", "Subcategory", JOptionPane.QUESTION_MESSAGE, null, subcategories, subcategories[0]
        );
        return selected;
    }
    
    public JFrame getMainFrame() {
        return mainFrame;
    }
}
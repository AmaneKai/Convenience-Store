package com.konbini.controller;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.view.ProductView;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller class dedicated to handling the user interface flow for all
 * product and inventory management tasks. It coordinates user interaction
 * via the ProductView and delegates business logic execution to the
 * underlying ProductController.
 */
public class ProductManagementController {
    /**
     * The view component responsible for displaying product-related menus,
     * lists, and handling user input.
     */
    private final ProductView view;
    /**
     * The controller responsible for the core business logic and persistence
     * of product data.
     */
    private final ProductController productController;

    /**
     * Constructs the ProductManagementController, injecting the required view
     * and product controller dependencies.
     *
     * @param view The user interface component for product management.
     * @param productController The core controller for product data and logic.
     */
    public ProductManagementController(ProductView view,
        ProductController productController) {
        this.view = view;
        this.productController = productController;
    }

    /**
     * Runs the main loop for product management, displaying the inventory menu
     * and executing methods based on user selection until the user chooses to exit.
     */
    public void handleProductManagement() {
        boolean backToMain = false;

        while (!backToMain) {
            view.displayProductMenu();
            int choice = view.getProductMenuChoice();

            switch (choice) {
                case 1: // View all products
                    view.displayProducts(productController.getAllProducts());
                    break;
                case 2: // View products by category
                    try {
                        view.displayInfoMessage("Select a product category:");
                        view.displayProducts(productController
                            .getProductsByCategory(view.getCategoryInput()));
                    } catch (Exception e) {
                        view.displayErrorMessage(e.getMessage());
                    }
                    break;
                case 3: // View products by subcategory
                    try {
                        view.displayInfoMessage
                            ("Select a product category first:");
                        view.displayProducts
                            (productController.getProductsBySubcategory(view
                            .getSubcategoryInput(view.getCategoryInput())));
                    } catch (Exception e) {
                        view.displayErrorMessage(e.getMessage());
                    }
                    break;
                case 4: // Search products by name
                    String searchTerm = view.getStringInput
                        ("Enter product name to search: ");
                    view.displayProducts
                        (productController.searchProductsByName(searchTerm));
                    break;
                case 5: // View low stock products
                    view.displayLowStockProducts
                        (productController.getLowStockProducts());
                    break;
                case 6: // View expired products
                    view.displayExpiredProducts
                        (productController.getExpiredProducts());
                    break;
                case 7: // Add new product
                    handleAddProduct();
                    break;
                case 8: // Update existing product
                    handleUpdateProduct();
                    break;
                case 9: // Remove product
                    handleRemoveProduct();
                    break;
                case 10: // Restock product
                    handleRestockProduct();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    view.displayErrorMessage
                        ("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Guides the user through the process of adding a new product to the inventory.
     * It gathers all necessary product details from the view and calls the
     * product controller to persist the new product.
     */
    public void handleAddProduct() {
        try {
            String name = view.getStringInput("Enter product name: ");
            double price = view.getDoubleInput("Enter product price: ");
            int quantity = view.getIntInput("Enter product quantity: ");
            view.displayInfoMessage("Select a product category:");
            ProductCategory category = view.getCategoryInput();
            String brand = view.getStringInput("Enter product brand: ");
            view.displayInfoMessage("Select a product subcategory:");
            ProductSubcategory subcategory = view
                .getSubcategoryInput(category);
            LocalDate expirationDate = view.getDateInput
            ("Enter product expiration date (leave empty if not applicable): ");

            productController.addProduct(name, price, quantity, category,
                brand, subcategory, expirationDate);
            view.displaySuccessMessage("Product added successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed to add product: "
                + e.getMessage());
        }
    }

    /**
     * Guides the user through selecting and updating an existing product's details.
     * It prompts for the product ID and then for new values for its properties.
     */
    public void handleUpdateProduct() {
        try {
            // Display products for selection
            view.displayProducts(productController.getAllProducts());

            // Get product ID
            String productId = view.getStringInput
                ("Enter product ID to update: ");
            Optional<Product> optionalProduct = productController
                .getProductById(productId);

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                view.displayProduct(product);

                // Get updated values
                String name = view.getStringInput
                    ("Enter new product name (leave empty to keep current): ");
                name = name.isEmpty() ? product.getName() : name;

                double price = view.getDoubleInput
                    ("Enter new product price (current: "
                    + product.getPrice() + "): ");

                int quantity = view.getIntInput
                    ("Enter new product quantity (current: "
                    + product.getQuantity() + "): ");

                view.displayInfoMessage
                    ("Select a new product category (current: "
                        + product.getCategory() + "):");

                ProductCategory category = view.getCategoryInput();

                String brand = view.getStringInput
                    ("Enter new product brand (leave empty to keep current): ");

                brand = brand.isEmpty() ? product.getBrand() : brand;

                view.displayInfoMessage
                ("Select a new product subcategory (current: "
                    + product.getVariant() + "):");

                ProductSubcategory subcategory = view
                .getSubcategoryInput(category);

                LocalDate expirationDate = view.getDateInput
                    ("Enter new expiration date (leave empty to keep current): ");

                productController.updateProduct(productId, name, price,
                    quantity, category, brand, subcategory, expirationDate);

                view.displaySuccessMessage("Product updated successfully.");
            } else {
                view.displayErrorMessage("Product not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to update product: "
                + e.getMessage());
        }
    }

    /**
     * Guides the user through selecting a product ID and confirming its removal
     * from the inventory.
     */
    public void handleRemoveProduct() {
        try {
            // Display products for selection
            view.displayProducts(productController.getAllProducts());

            // Get product ID
            String productId = view.getStringInput
                ("Enter product ID to remove: ");

            // Confirm removal
            if (view.getBooleanInput
                ("Are you sure you want to remove this product?")) {
                productController.removeProduct(productId);
                view.displaySuccessMessage("Product removed successfully.");
            } else {
                view.displayInfoMessage("Product removal cancelled.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to remove product: "
                + e.getMessage());
        }
    }

    /**
     * Guides the user through selecting a product ID and specifying a quantity
     * to add to the existing stock (restock operation).
     */
    public void handleRestockProduct() {
        try {
            // Display products for selection
            view.displayProducts(productController.getAllProducts());

            // Get product ID and quantity
            String productId = view
                .getStringInput("Enter product ID to restock: ");
            int quantity = view.getIntInput("Enter quantity to add: ");

            productController.restockProduct(productId, quantity);
            view.displaySuccessMessage("Product restocked successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed to restock product: "
                + e.getMessage());
        }
    }

    /**
     * Initializes a predefined set of sample products across different categories
     * for demonstration or initial setup purposes.
     */
    public void initializeSampleProducts() {
        try {
            // Initialize sample products
            // Food category
            productController.addProduct("Sandwich", 75.0, 10, ProductCategory.FOOD, "Konbini", ProductSubcategory.READY_TO_EAT, LocalDate.now().plusDays(2));
            productController.addProduct("Potato Chips", 45.0, 20, ProductCategory.FOOD, "Lays", ProductSubcategory.SNACK, LocalDate.now().plusMonths(6));
            productController.addProduct("Chocolate Bar", 35.0, 15, ProductCategory.FOOD, "Hershey's", ProductSubcategory.SNACK, LocalDate.now().plusMonths(8));
            productController.addProduct("Instant Ramen", 25.0, 30, ProductCategory.FOOD, "Nissin", ProductSubcategory.READY_TO_EAT, LocalDate.now().plusYears(1));
            productController.addProduct("Cookies", 40.0, 15, ProductCategory.FOOD, "Oreo", ProductSubcategory.SNACK, LocalDate.now().plusMonths(10));

            // Beverage category
            productController.addProduct("Coffee", 30.0, 10, ProductCategory.BEVERAGE, "Nescafe", ProductSubcategory.HOT, LocalDate.now().plusMonths(12));
            productController.addProduct("Bottled Water", 20.0, 50, ProductCategory.BEVERAGE, "Nature's Spring", ProductSubcategory.COLD, LocalDate.now().plusYears(2));
            productController.addProduct("Soda", 35.0, 25, ProductCategory.BEVERAGE, "Coca-Cola", ProductSubcategory.COLD, LocalDate.now().plusMonths(6));
            productController.addProduct("Beer", 60.0, 15, ProductCategory.BEVERAGE, "San Miguel", ProductSubcategory.ALCOHOLIC, LocalDate.now().plusYears(1));
            productController.addProduct("Tea", 25.0, 20, ProductCategory.BEVERAGE, "Lipton", ProductSubcategory.HOT, LocalDate.now().plusMonths(18));

            // Toiletries category
            productController.addProduct("Bath Soap", 25.0, 30, ProductCategory.TOILETRIES, "Dove", ProductSubcategory.SOAP, LocalDate.now().plusYears(2));
            productController.addProduct("Shampoo", 120.0, 15, ProductCategory.TOILETRIES, "Pantene", ProductSubcategory.SHAMPOO, LocalDate.now().plusYears(3));
            productController.addProduct("Toothpaste", 80.0, 20, ProductCategory.TOILETRIES, "Colgate", ProductSubcategory.BEAUTY, LocalDate.now().plusYears(2));
            productController.addProduct("Facial Wash", 150.0, 10, ProductCategory.TOILETRIES, "Nivea", ProductSubcategory.BEAUTY, LocalDate.now().plusYears(2));
            productController.addProduct("Hand Lotion", 90.0, 12, ProductCategory.TOILETRIES, "Jergens", ProductSubcategory.BEAUTY, LocalDate.now().plusYears(1));

            // Cleaning Products category
            productController.addProduct("Dishwashing Liquid", 50.0, 20, ProductCategory.CLEANING, "Joy", ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));
            productController.addProduct("Bathroom Tissue", 75.0, 30, ProductCategory.CLEANING, "Tissue", ProductSubcategory.TISSUE, LocalDate.now().plusYears(5));
            productController.addProduct("Hand Sanitizer", 45.0, 25, ProductCategory.CLEANING, "Safeguard", ProductSubcategory.SANITIZER, LocalDate.now().plusYears(3));
            productController.addProduct("Laundry Detergent", 120.0, 15, ProductCategory.CLEANING, "Tide", ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));
            productController.addProduct("Floor Cleaner", 100.0, 10, ProductCategory.CLEANING, "Mr. Clean", ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));

            // Medications category
            productController.addProduct("Paracetamol", 50.0, 40, ProductCategory.MEDICATION, "Biogesic", ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(2));
            productController.addProduct("Ibuprofen", 75.0, 30, ProductCategory.MEDICATION, "Advil", ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(3));
            productController.addProduct("Cold Medicine", 120.0, 20, ProductCategory.MEDICATION, "Neozep", ProductSubcategory.COLD_FLU, LocalDate.now().plusYears(1));
            productController.addProduct("Antacid", 60.0, 25, ProductCategory.MEDICATION, "Kremil-S", ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(2));
            productController.addProduct("Antihistamine", 80.0, 15, ProductCategory.MEDICATION, "Claritin", ProductSubcategory.ALLERGY, LocalDate.now().plusYears(2));

            view.displaySuccessMessage
                ("Sample products initialized successfully.");
        } catch (Exception e) {
            view.displayErrorMessage
                ("Failed to initialize sample products: " + e.getMessage());
        }
    }
}

package com.konbini.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.view.ProductView;

/**
 * Controller class dedicated to handling product and inventory management
 * tasks.
 * 
 * EVENT-DRIVEN ARCHITECTURE (for Swing GUI):
 * - Individual action methods called directly by view buttons
 * - No blocking while loops
 * - Passes Model objects to view (view handles DTO conversion internally)
 * 
 * INTERFACE COMPLIANCE:
 * - View receives Model objects (per StoreView interface)
 * - View converts Model → DTO internally before showing to panels
 * - Business logic delegated to ProductController
 */
public class ProductManagementController {
    private final ProductView view;
    private final ProductController productController;

    public ProductManagementController(ProductView view, ProductController productController) {
        this.view = view;
        this.productController = productController;
    }

    // ==================== INDIVIDUAL ACTION METHODS ====================
    // These are called directly by GUI buttons - no loops!

    /**
     * Displays all products in the inventory.
     */
    public void handleViewAllProducts() {
        List<Product> products = productController.getAllProducts();
        view.displayProducts(products);
    }

    /**
     * Displays products filtered by category.
     */
    public void handleViewByCategory() {
        try {
            view.displayInfoMessage("Select a product category:");
            ProductCategory category = view.getCategoryInput();
            List<Product> products = productController.getProductsByCategory(category);
            view.displayProducts(products);
        } catch (Exception e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    /**
     * Displays products filtered by subcategory.
     */
    public void handleViewBySubcategory() {
        try {
            view.displayInfoMessage("Select a product category first:");
            ProductCategory category = view.getCategoryInput();
            ProductSubcategory subcategory = view.getSubcategoryInput(category);
            List<Product> products = productController.getProductsBySubcategory(subcategory);
            view.displayProducts(products);
        } catch (Exception e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    /**
     * Searches products by name.
     */
    public void handleSearchProducts() {
        String searchTerm = view.getStringInput("Enter product name to search: ");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<Product> products = productController.searchProductsByName(searchTerm);
            view.displayProducts(products);
        }
    }

    /**
     * Displays products with low stock levels.
     */
    public void handleViewLowStock() {
        List<Product> products = productController.getLowStockProducts();
        view.displayLowStockProducts(products);
    }

    /**
     * Displays expired products.
     */
    public void handleViewExpired() {
        List<Product> products = productController.getExpiredProducts();
        view.displayExpiredProducts(products);
    }

    /**
     * Handles the product addition process.
     */
    public void handleAddProduct() {
        try {
            String name = view.getStringInput("Enter product name: ");
            if (name == null || name.trim().isEmpty())
                return;

            double price = view.getDoubleInput("Enter product price: ");
            int quantity = view.getIntInput("Enter product quantity: ");

            view.displayInfoMessage("Select a product category:");
            ProductCategory category = view.getCategoryInput();

            String brand = view.getStringInput("Enter product brand: ");
            if (brand == null || brand.trim().isEmpty())
                return;

            view.displayInfoMessage("Select a product subcategory:");
            ProductSubcategory subcategory = view.getSubcategoryInput(category);

            LocalDate expirationDate = view
                    .getDateInput("Enter product expiration date (leave empty if not applicable): ");

            productController.addProduct(name, price, quantity, category, brand, subcategory, expirationDate);
            view.displaySuccessMessage("Product added successfully.");

            // Refresh the display
            handleViewAllProducts();
        } catch (Exception e) {
            view.displayErrorMessage("Failed to add product: " + e.getMessage());
        }
    }

    /**
     * Handles the product update process.
     */
    public void handleUpdateProduct() {
        try {
            // Display products for selection
            handleViewAllProducts();

            // Get product ID
            String productId = view.getStringInput("Enter product ID to update: ");
            if (productId == null || productId.trim().isEmpty())
                return;

            Optional<Product> optionalProduct = productController.getProductById(productId);

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                view.displayProduct(product);

                // Get updated values
                String name = view.getStringInput("Enter new product name (leave empty to keep current): ");
                name = (name == null || name.isEmpty()) ? product.getName() : name;

                double price = view.getDoubleInput("Enter new product price (current: " + product.getPrice() + "): ");
                int quantity = view
                        .getIntInput("Enter new product quantity (current: " + product.getQuantity() + "): ");

                view.displayInfoMessage("Select a new product category (current: " + product.getCategory() + "):");
                ProductCategory category = view.getCategoryInput();

                String brand = view.getStringInput("Enter new product brand (leave empty to keep current): ");
                brand = (brand == null || brand.isEmpty()) ? product.getBrand() : brand;

                view.displayInfoMessage("Select a new product subcategory (current: " + product.getVariant() + "):");
                ProductSubcategory subcategory = view.getSubcategoryInput(category);

                LocalDate expirationDate = view
                        .getDateInput("Enter new expiration date (leave empty to keep current): ");

                productController.updateProduct(productId, name, price, quantity, category, brand, subcategory,
                        expirationDate);

                view.displaySuccessMessage("Product updated successfully.");
                handleViewAllProducts(); // Refresh display
            } else {
                view.displayErrorMessage("Product not found.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to update product: " + e.getMessage());
        }
    }

    /**
     * Handles the product removal process.
     */
    public void handleRemoveProduct() {
        try {
            // Display products for selection
            handleViewAllProducts();

            // Get product ID
            String productId = view.getStringInput("Enter product ID to remove: ");
            if (productId == null || productId.trim().isEmpty())
                return;

            // Confirm removal
            if (view.getBooleanInput("Are you sure you want to remove this product?")) {
                productController.removeProduct(productId);
                view.displaySuccessMessage("Product removed successfully.");
                handleViewAllProducts(); // Refresh display
            } else {
                view.displayInfoMessage("Product removal cancelled.");
            }
        } catch (Exception e) {
            view.displayErrorMessage("Failed to remove product: " + e.getMessage());
        }
    }

    /**
     * Handles the product restocking process.
     */
    public void handleRestockProduct() {
        try {
            // Display products for selection
            handleViewAllProducts();

            // Get product ID and quantity
            String productId = view.getStringInput("Enter product ID to restock: ");
            if (productId == null || productId.trim().isEmpty())
                return;

            int quantity = view.getIntInput("Enter quantity to add: ");

            productController.restockProduct(productId, quantity);
            view.displaySuccessMessage("Product restocked successfully.");
            handleViewAllProducts(); // Refresh display
        } catch (Exception e) {
            view.displayErrorMessage("Failed to restock product: " + e.getMessage());
        }
    }

    /**
     * Runs the main loop for product management, displaying the menu and
     * executing actions based on user choice until the user selects to exit.
     */
    public void handleProductManagement() {
        boolean backToMain = false;

        while (!backToMain) {
            view.displayProductMenu();
            int choice = view.getProductMenuChoice();

            switch (choice) {
                case 1:
                    handleViewAllProducts();
                    break;
                case 2:
                    handleViewByCategory();
                    break;
                case 3:
                    handleViewBySubcategory();
                    break;
                case 4:
                    handleSearchProducts();
                    break;
                case 5:
                    handleViewLowStock();
                    break;
                case 6:
                    handleViewExpired();
                    break;
                case 7:
                    handleAddProduct();
                    break;
                case 8:
                    handleUpdateProduct();
                    break;
                case 9:
                    handleRemoveProduct();
                    break;
                case 10:
                    handleRestockProduct();
                    break;
                case 0:
                    backToMain = true;
                    break;
                default:
                    view.displayErrorMessage("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Initializes sample products for demonstration purposes.
     * This method is typically called once during application setup.
     */
    public void initializeSampleProducts() {
        try {
            // Food category
            productController.addProduct("Sandwich", 75.0, 10, ProductCategory.FOOD, "Konbini",
                    ProductSubcategory.READY_TO_EAT, LocalDate.now().plusDays(2));
            productController.addProduct("Potato Chips", 45.0, 20, ProductCategory.FOOD, "Lays",
                    ProductSubcategory.SNACK, LocalDate.now().plusMonths(6));
            productController.addProduct("Chocolate Bar", 35.0, 15, ProductCategory.FOOD, "Hershey's",
                    ProductSubcategory.SNACK, LocalDate.now().plusMonths(8));
            productController.addProduct("Instant Ramen", 25.0, 30, ProductCategory.FOOD, "Nissin",
                    ProductSubcategory.READY_TO_EAT, LocalDate.now().plusYears(1));
            productController.addProduct("Cookies", 40.0, 15, ProductCategory.FOOD, "Oreo", ProductSubcategory.SNACK,
                    LocalDate.now().plusMonths(10));

            // Beverage category
            productController.addProduct("Coffee", 30.0, 10, ProductCategory.BEVERAGE, "Nescafe",
                    ProductSubcategory.HOT, LocalDate.now().plusMonths(12));
            productController.addProduct("Bottled Water", 20.0, 50, ProductCategory.BEVERAGE, "Nature's Spring",
                    ProductSubcategory.COLD, LocalDate.now().plusYears(2));
            productController.addProduct("Soda", 35.0, 25, ProductCategory.BEVERAGE, "Coca-Cola",
                    ProductSubcategory.COLD, LocalDate.now().plusMonths(6));
            productController.addProduct("Beer", 60.0, 15, ProductCategory.BEVERAGE, "San Miguel",
                    ProductSubcategory.ALCOHOLIC, LocalDate.now().plusYears(1));
            productController.addProduct("Tea", 25.0, 20, ProductCategory.BEVERAGE, "Lipton", ProductSubcategory.HOT,
                    LocalDate.now().plusMonths(18));

            // Toiletries category
            productController.addProduct("Bath Soap", 25.0, 30, ProductCategory.TOILETRIES, "Dove",
                    ProductSubcategory.SOAP, LocalDate.now().plusYears(2));
            productController.addProduct("Shampoo", 120.0, 15, ProductCategory.TOILETRIES, "Pantene",
                    ProductSubcategory.SHAMPOO, LocalDate.now().plusYears(3));
            productController.addProduct("Toothpaste", 80.0, 20, ProductCategory.TOILETRIES, "Colgate",
                    ProductSubcategory.BEAUTY, LocalDate.now().plusYears(2));
            productController.addProduct("Facial Wash", 150.0, 10, ProductCategory.TOILETRIES, "Nivea",
                    ProductSubcategory.BEAUTY, LocalDate.now().plusYears(2));
            productController.addProduct("Hand Lotion", 90.0, 12, ProductCategory.TOILETRIES, "Jergens",
                    ProductSubcategory.BEAUTY, LocalDate.now().plusYears(1));

            // Cleaning Products category
            productController.addProduct("Dishwashing Liquid", 50.0, 20, ProductCategory.CLEANING, "Joy",
                    ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));
            productController.addProduct("Bathroom Tissue", 75.0, 30, ProductCategory.CLEANING, "Tissue",
                    ProductSubcategory.TISSUE, LocalDate.now().plusYears(5));
            productController.addProduct("Hand Sanitizer", 45.0, 25, ProductCategory.CLEANING, "Safeguard",
                    ProductSubcategory.SANITIZER, LocalDate.now().plusYears(3));
            productController.addProduct("Laundry Detergent", 120.0, 15, ProductCategory.CLEANING, "Tide",
                    ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));
            productController.addProduct("Floor Cleaner", 100.0, 10, ProductCategory.CLEANING, "Mr. Clean",
                    ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));

            // Medications category
            productController.addProduct("Paracetamol", 50.0, 40, ProductCategory.MEDICATION, "Biogesic",
                    ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(2));
            productController.addProduct("Ibuprofen", 75.0, 30, ProductCategory.MEDICATION, "Advil",
                    ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(3));
            productController.addProduct("Cold Medicine", 120.0, 20, ProductCategory.MEDICATION, "Neozep",
                    ProductSubcategory.COLD_FLU, LocalDate.now().plusYears(1));
            productController.addProduct("Antacid", 60.0, 25, ProductCategory.MEDICATION, "Kremil-S",
                    ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(2));
            productController.addProduct("Antihistamine", 80.0, 15, ProductCategory.MEDICATION, "Claritin",
                    ProductSubcategory.ALLERGY, LocalDate.now().plusYears(2));

            view.displaySuccessMessage("Sample products initialized successfully.");
        } catch (Exception e) {
            view.displayErrorMessage("Failed to initialize sample products: " + e.getMessage());
        }
    }
}
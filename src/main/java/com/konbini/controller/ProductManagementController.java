package com.konbini.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.konbini.dto.ProductDTO;
import com.konbini.model.Product;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import com.konbini.service.ProductService;
import com.konbini.view.ProductView;

/**
 * Controller for managing product operations including viewing, adding, updating,
 * removing products, and inventory management. Coordinates between the view
 * and product service layer, and handles sample data initialization.
 */
public class ProductManagementController {
    private final ProductView view;
    private final ProductController productController;
    private final ProductService productService;

    /**
     * Constructs a ProductManagementController with all required dependencies.
     *
     * @param view the product view for user interface interactions
     * @param productController controller for product operations
     * @param productService service for product validation and business logic
     * @throws IllegalArgumentException if any dependency is null
     */
    public ProductManagementController(
            ProductView view,
            ProductController productController,
            ProductService productService) {
        if (view == null || productController == null || productService == null) {
            throw new IllegalArgumentException("All dependencies must be provided");
        }
        this.view = view;
        this.productController = productController;
        this.productService = productService;
    }

    // ==================== PUBLIC HANDLERS ====================

    /**
     * Handles displaying all products in the inventory.
     * Catches and handles any exceptions during the loading process.
     */
    public void handleViewAllProducts() {
        try {
            List<Product> products = productController.getAllProducts();
            view.displayProducts(ProductDTO.fromModelList(products));
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing all products");
        } catch (Exception e) {
            handleGenericException(e, "viewing all products", "Failed to load products. Please try again.");
        }
    }

    /**
     * Handles viewing products filtered by category.
     * Prompts the user to select a category and displays matching products.
     */
    public void handleViewByCategory() {
        try {
            view.displayInfoMessage("Select a product category:");
            ProductCategory category = view.getCategoryInput();
            List<Product> products = productController.getProductsByCategory(category);
            view.displayProducts(ProductDTO.fromModelList(products));
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing products by category");
        } catch (Exception e) {
            handleGenericException(e, "viewing products by category", "Failed to load products. Please try again.");
        }
    }

    /**
     * Handles viewing products filtered by subcategory.
     * Prompts the user to select a category first, then a subcategory.
     */
    public void handleViewBySubcategory() {
        try {
            view.displayInfoMessage("Select a product category first:");
            ProductCategory category = view.getCategoryInput();
            ProductSubcategory subcategory = view.getSubcategoryInput(category);
            List<Product> products = productController.getProductsBySubcategory(subcategory);
            view.displayProducts(ProductDTO.fromModelList(products));
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "viewing products by subcategory");
        } catch (Exception e) {
            handleGenericException(e, "viewing products by subcategory", "Failed to load products. Please try again.");
        }
    }

    /**
     * Handles searching for products by name.
     * Prompts the user for a search term and displays matching products.
     */
    public void handleSearchProducts() {
        try {
            String searchTerm = view.getStringInput("Enter product name to search: ");

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                List<Product> products = productController.searchProductsByName(searchTerm.trim());
                view.displayProducts(ProductDTO.fromModelList(products));
            } else {
                view.displayInfoMessage("No search term provided.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "searching products");
        } catch (Exception e) {
            handleGenericException(e, "searching products", "Search failed. Please try again.");
        }
    }

    /**
     * Handles viewing products with low stock levels.
     * Displays products that are below the low stock threshold.
     */
    public void handleViewLowStock() {
        try {
            List<Product> products = productController.getLowStockProducts();
            view.displayLowStockProducts(ProductDTO.fromModelList(products));
        } catch (Exception e) {
            handleGenericException(e, "viewing low stock products", "Failed to load low stock products. Please try again.");
        }
    }

    /**
     * Handles viewing expired or soon-to-expire products.
     * Displays products that have passed or are nearing their expiration dates.
     */
    public void handleViewExpired() {
        try {
            List<Product> products = productController.getExpiredProducts();
            view.displayExpiredProducts(ProductDTO.fromModelList(products));
        } catch (Exception e) {
            handleGenericException(e, "viewing expired products", "Failed to load expired products. Please try again.");
        }
    }

    /**
     * Handles adding a new product to the inventory.
     * Prompts the user for all required product information.
     */
    public void handleAddProduct() {
        try {
            String name = view.getStringInput("Enter product name: ");

            if (name != null && !name.trim().isEmpty()) {
                addNewProduct(name.trim());
            } else {
                view.displayErrorMessage("Product name cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "adding product");
        } catch (Exception e) {
            handleGenericException(e, "adding product", "Failed to add product. Please try again.");
        }
    }

    /**
     * Handles updating an existing product's information.
     * Displays all products first, then prompts for product ID and new information.
     */
    public void handleUpdateProduct() {
        try {
            handleViewAllProducts();
            String productId = view.getStringInput("Enter product ID to update: ");

            if (productId != null && !productId.trim().isEmpty()) {
                updateExistingProduct(productId.trim());
            } else {
                view.displayErrorMessage("Product ID cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "updating product");
        } catch (Exception e) {
            handleGenericException(e, "updating product", "Failed to update product. Please try again.");
        }
    }

    /**
     * Handles removing a product from the inventory.
     * Displays all products first, then prompts for product ID and confirmation.
     */
    public void handleRemoveProduct() {
        try {
            handleViewAllProducts();
            String productId = view.getStringInput("Enter product ID to remove: ");

            if (productId != null && !productId.trim().isEmpty()) {
                removeExistingProduct(productId.trim());
            } else {
                view.displayErrorMessage("Product ID cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "removing product");
        } catch (Exception e) {
            handleGenericException(e, "removing product", "Failed to remove product. Please try again.");
        }
    }

    /**
     * Handles restocking a product by adding to its current quantity.
     * Displays all products first, then prompts for product ID and quantity to add.
     */
    public void handleRestockProduct() {
        try {
            handleViewAllProducts();
            String productId = view.getStringInput("Enter product ID to restock: ");

            if (productId != null && !productId.trim().isEmpty()) {
                restockExistingProduct(productId.trim());
            } else {
                view.displayErrorMessage("Product ID cannot be empty.");
            }
        } catch (IllegalArgumentException e) {
            handleArgumentException(e, "restocking product");
        } catch (Exception e) {
            handleGenericException(e, "restocking product", "Failed to restock product. Please try again.");
        }
    }

    /**
     * Retrieves all products as DTOs for external use.
     *
     * @return a list of ProductDTO objects representing all products
     */
    public List<ProductDTO> getAllProductsDTO() {
        List<ProductDTO> temp = new java.util.ArrayList<>();

        try {
            List<Product> products = productController.getAllProducts();
            if (products != null && !products.isEmpty()) {
                temp = ProductDTO.fromModelList(products);
            }
        } catch (Exception e) {
            handleGenericException(e, "fetching products for DTO", "Error loading product data");
        }

        return temp;
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Adds a new product with the specified name and prompts for additional details.
     *
     * @param name the name of the product to add
     */
    private void addNewProduct(String name) {
        double price = view.getDoubleInput("Enter product price: ");
        int quantity = view.getIntInput("Enter product quantity: ");

        productService.validatePrice(price);
        productService.validateQuantity(quantity);

        view.displayInfoMessage("Select a product category:");
        ProductCategory category = view.getCategoryInput();

        String brand = view.getStringInput("Enter product brand: ");
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Product brand cannot be empty");
        }

        view.displayInfoMessage("Select a product subcategory:");
        ProductSubcategory subcategory = view.getSubcategoryInput(category);

        LocalDate expirationDate = view.getDateInput("Enter product expiration date (leave empty if not applicable): ");

        productController.addProduct(name, price, quantity, category, brand.trim(), subcategory, expirationDate);
        view.displaySuccessMessage("Product added successfully.");
        handleViewAllProducts();
    }

    /**
     * Updates an existing product's information.
     *
     * @param productId the ID of the product to update
     */
    private void updateExistingProduct(String productId) {
        Optional<Product> optionalProduct = productController.getProductById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            view.displayProduct(ProductDTO.fromModel(product));

            String name = view.getStringInput("Enter new product name (leave empty to keep current): ");
            name = (name == null || name.isEmpty()) ? product.getName() : name.trim();

            double price = view.getDoubleInput("Enter new product price (current: " + product.getPrice() + "): ");
            productService.validatePrice(price);

            int quantity = view.getIntInput("Enter new product quantity (current: " + product.getQuantity() + "): ");
            productService.validateQuantity(quantity);

            view.displayInfoMessage("Select a new product category (current: " + product.getCategory() + "):");
            ProductCategory category = view.getCategoryInput();

            String brand = view.getStringInput("Enter new product brand (leave empty to keep current): ");
            brand = (brand == null || brand.isEmpty()) ? product.getBrand() : brand.trim();

            view.displayInfoMessage("Select a new product subcategory (current: " + product.getVariant() + "):");
            ProductSubcategory subcategory = view.getSubcategoryInput(category);

            LocalDate expirationDate = view.getDateInput("Enter new expiration date (leave empty to keep current): ");

            productController.updateProduct(productId, name, price, quantity, category, brand, subcategory, expirationDate);
            view.displaySuccessMessage("Product updated successfully.");
            handleViewAllProducts();
        } else {
            view.displayErrorMessage("Product not found.");
        }
    }

    /**
     * Removes an existing product from the inventory after confirmation.
     *
     * @param productId the ID of the product to remove
     */
    private void removeExistingProduct(String productId) {
        if (view.getBooleanInput("Are you sure you want to remove this product?")) {
            productController.removeProduct(productId);
            view.displaySuccessMessage("Product removed successfully.");
            handleViewAllProducts();
        } else {
            view.displayInfoMessage("Product removal cancelled.");
        }
    }

    /**
     * Restocks an existing product by adding to its current quantity.
     *
     * @param productId the ID of the product to restock
     */
    private void restockExistingProduct(String productId) {
        int quantity = view.getIntInput("Enter quantity to add: ");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than 0");
        }

        productController.restockProduct(productId, quantity);
        view.displaySuccessMessage("Product restocked successfully.");
        handleViewAllProducts();
    }

    /**
     * Initializes sample products for demonstration and testing purposes.
     * Adds products across all categories with realistic data.
     */
    public void initializeSampleProducts() {
        try {
            System.out.println("=== INITIALIZING SAMPLE PRODUCTS ===");
            int successCount = 0;
            int totalProducts = 25;

            // Food category products
            try {
                productController.addProduct("Sandwich", 75.0, 10, ProductCategory.FOOD, "Konbini",
                        ProductSubcategory.READY_TO_EAT, LocalDate.now().plusDays(2));
                successCount++;
                System.out.println("✓ Added Sandwich");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Sandwich: " + e.getMessage());
            }

            try {
                productController.addProduct("Potato Chips", 45.0, 20, ProductCategory.FOOD, "Lays",
                        ProductSubcategory.SNACK, LocalDate.now().plusMonths(6));
                successCount++;
                System.out.println("✓ Added Potato Chips");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Potato Chips: " + e.getMessage());
            }

            // ... (similar documentation for other sample products)

            System.out.println("=== SAMPLE PRODUCTS INITIALIZATION COMPLETED ===");
            System.out.println("Successfully added: " + successCount + "/" + totalProducts + " products");

            if (successCount > 0) {
                view.displaySuccessMessage(successCount + " sample products initialized successfully.");
            } else {
                view.displayErrorMessage("Failed to initialize any sample products.");
            }

        } catch (Exception e) {
            System.err.println("Fatal error in sample product initialization: " + e.getMessage());
            view.displayErrorMessage("Critical error during sample data initialization.");
        }
    }

    // ==================== ERROR HANDLING HELPERS ====================

    /**
     * Handles IllegalArgumentException by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     */
    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    /**
     * Handles generic exceptions by logging and displaying user-friendly error message.
     *
     * @param e the exception that occurred
     * @param context the context where the exception occurred
     * @param userMessage the message to display to the user
     */
    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}
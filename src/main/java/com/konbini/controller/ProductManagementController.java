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

public class ProductManagementController {
    private final ProductView view;
    private final ProductController productController;
    private final ProductService productService;

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

    public void handleViewLowStock() {
        try {
            List<Product> products = productController.getLowStockProducts();
            view.displayLowStockProducts(ProductDTO.fromModelList(products));
        } catch (Exception e) {
            handleGenericException(e, "viewing low stock products", "Failed to load low stock products. Please try again.");
        }
    }

    public void handleViewExpired() {
        try {
            List<Product> products = productController.getExpiredProducts();
            view.displayExpiredProducts(ProductDTO.fromModelList(products));
        } catch (Exception e) {
            handleGenericException(e, "viewing expired products", "Failed to load expired products. Please try again.");
        }
    }

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

    private void removeExistingProduct(String productId) {
        if (view.getBooleanInput("Are you sure you want to remove this product?")) {
            productController.removeProduct(productId);
            view.displaySuccessMessage("Product removed successfully.");
            handleViewAllProducts();
        } else {
            view.displayInfoMessage("Product removal cancelled.");
        }
    }

    private void restockExistingProduct(String productId) {
        int quantity = view.getIntInput("Enter quantity to add: ");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than 0");
        }

        productController.restockProduct(productId, quantity);
        view.displaySuccessMessage("Product restocked successfully.");
        handleViewAllProducts();
    }

    public void initializeSampleProducts() {
        try {
            System.out.println("=== INITIALIZING SAMPLE PRODUCTS ===");
            int successCount = 0;
            int totalProducts = 25;

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

            try {
                productController.addProduct("Chocolate Bar", 35.0, 15, ProductCategory.FOOD, "Hershey's",
                        ProductSubcategory.SNACK, LocalDate.now().plusMonths(8));
                successCount++;
                System.out.println("✓ Added Chocolate Bar");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Chocolate Bar: " + e.getMessage());
            }

            try {
                productController.addProduct("Instant Ramen", 25.0, 30, ProductCategory.FOOD, "Nissin",
                        ProductSubcategory.READY_TO_EAT, LocalDate.now().plusYears(1));
                successCount++;
                System.out.println("✓ Added Instant Ramen");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Instant Ramen: " + e.getMessage());
            }

            try {
                productController.addProduct("Cookies", 40.0, 15, ProductCategory.FOOD, "Oreo",
                        ProductSubcategory.SNACK, LocalDate.now().plusMonths(10));
                successCount++;
                System.out.println("✓ Added Cookies");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Cookies: " + e.getMessage());
            }

            // Beverage Category
            try {
                productController.addProduct("Coffee", 30.0, 10, ProductCategory.BEVERAGE, "Nescafe",
                        ProductSubcategory.HOT, LocalDate.now().plusMonths(12));
                successCount++;
                System.out.println("✓ Added Coffee");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Coffee: " + e.getMessage());
            }

            try {
                productController.addProduct("Bottled Water", 20.0, 50, ProductCategory.BEVERAGE, "Nature's Spring",
                        ProductSubcategory.COLD, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Bottled Water");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Bottled Water: " + e.getMessage());
            }

            try {
                productController.addProduct("Soda", 35.0, 25, ProductCategory.BEVERAGE, "Coca-Cola",
                        ProductSubcategory.COLD, LocalDate.now().plusMonths(6));
                successCount++;
                System.out.println("✓ Added Soda");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Soda: " + e.getMessage());
            }

            try {
                productController.addProduct("Beer", 60.0, 15, ProductCategory.BEVERAGE, "San Miguel",
                        ProductSubcategory.ALCOHOLIC, LocalDate.now().plusYears(1));
                successCount++;
                System.out.println("✓ Added Beer");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Beer: " + e.getMessage());
            }

            try {
                productController.addProduct("Tea", 25.0, 20, ProductCategory.BEVERAGE, "Lipton",
                        ProductSubcategory.HOT, LocalDate.now().plusMonths(18));
                successCount++;
                System.out.println("✓ Added Tea");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Tea: " + e.getMessage());
            }

            // Toiletries Category
            try {
                productController.addProduct("Bath Soap", 25.0, 30, ProductCategory.TOILETRIES, "Dove",
                        ProductSubcategory.SOAP, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Bath Soap");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Bath Soap: " + e.getMessage());
            }

            try {
                productController.addProduct("Shampoo", 120.0, 15, ProductCategory.TOILETRIES, "Pantene",
                        ProductSubcategory.SHAMPOO, LocalDate.now().plusYears(3));
                successCount++;
                System.out.println("✓ Added Shampoo");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Shampoo: " + e.getMessage());
            }

            try {
                productController.addProduct("Toothpaste", 80.0, 20, ProductCategory.TOILETRIES, "Colgate",
                        ProductSubcategory.BEAUTY, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Toothpaste");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Toothpaste: " + e.getMessage());
            }

            try {
                productController.addProduct("Facial Wash", 150.0, 10, ProductCategory.TOILETRIES, "Nivea",
                        ProductSubcategory.BEAUTY, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Facial Wash");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Facial Wash: " + e.getMessage());
            }

            try {
                productController.addProduct("Hand Lotion", 90.0, 12, ProductCategory.TOILETRIES, "Jergens",
                        ProductSubcategory.BEAUTY, LocalDate.now().plusYears(1));
                successCount++;
                System.out.println("✓ Added Hand Lotion");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Hand Lotion: " + e.getMessage());
            }

            // Cleaning Category
            try {
                productController.addProduct("Dishwashing Liquid", 50.0, 20, ProductCategory.CLEANING, "Joy",
                        ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Dishwashing Liquid");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Dishwashing Liquid: " + e.getMessage());
            }

            try {
                productController.addProduct("Bathroom Tissue", 75.0, 30, ProductCategory.CLEANING, "Tissue",
                        ProductSubcategory.TISSUE, LocalDate.now().plusYears(5));
                successCount++;
                System.out.println("✓ Added Bathroom Tissue");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Bathroom Tissue: " + e.getMessage());
            }

            try {
                productController.addProduct("Hand Sanitizer", 45.0, 25, ProductCategory.CLEANING, "Safeguard",
                        ProductSubcategory.SANITIZER, LocalDate.now().plusYears(3));
                successCount++;
                System.out.println("✓ Added Hand Sanitizer");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Hand Sanitizer: " + e.getMessage());
            }

            try {
                productController.addProduct("Laundry Detergent", 120.0, 15, ProductCategory.CLEANING, "Tide",
                        ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Laundry Detergent");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Laundry Detergent: " + e.getMessage());
            }

            try {
                productController.addProduct("Floor Cleaner", 100.0, 10, ProductCategory.CLEANING, "Mr. Clean",
                        ProductSubcategory.DETERGENT, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Floor Cleaner");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Floor Cleaner: " + e.getMessage());
            }

            // Medication Category
            try {
                productController.addProduct("Paracetamol", 50.0, 40, ProductCategory.MEDICATION, "Biogesic",
                        ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Paracetamol");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Paracetamol: " + e.getMessage());
            }

            try {
                productController.addProduct("Ibuprofen", 75.0, 30, ProductCategory.MEDICATION, "Advil",
                        ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(3));
                successCount++;
                System.out.println("✓ Added Ibuprofen");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Ibuprofen: " + e.getMessage());
            }

            try {
                productController.addProduct("Cold Medicine", 120.0, 20, ProductCategory.MEDICATION, "Neozep",
                        ProductSubcategory.COLD_FLU, LocalDate.now().plusYears(1));
                successCount++;
                System.out.println("✓ Added Cold Medicine");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Cold Medicine: " + e.getMessage());
            }

            try {
                productController.addProduct("Antacid", 60.0, 25, ProductCategory.MEDICATION, "Kremil-S",
                        ProductSubcategory.PAIN_RELIEF, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Antacid");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Antacid: " + e.getMessage());
            }

            try {
                productController.addProduct("Antihistamine", 80.0, 15, ProductCategory.MEDICATION, "Claritin",
                        ProductSubcategory.ALLERGY, LocalDate.now().plusYears(2));
                successCount++;
                System.out.println("✓ Added Antihistamine");
            } catch (Exception e) {
                System.err.println("✗ Failed to add Antihistamine: " + e.getMessage());
            }

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

    private void handleArgumentException(IllegalArgumentException e, String context) {
        System.err.println("Invalid argument " + context + ": " +
                (e.getMessage() != null ? e.getMessage() : "Unknown"));
        view.displayErrorMessage("Invalid input: " +
                (e.getMessage() != null ? e.getMessage() : "Please check your input and try again."));
    }

    private void handleGenericException(Exception e, String context, String userMessage) {
        System.err.println("Error " + context + ": " + e.getMessage());
        view.displayErrorMessage(userMessage);
    }
}
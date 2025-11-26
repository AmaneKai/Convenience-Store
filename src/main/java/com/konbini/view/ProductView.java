package com.konbini.view;

import java.util.List;

import com.konbini.dto.CategoryDTO;
import com.konbini.dto.ProductDTO;
import com.konbini.dto.SubcategoryDTO;

/**
 * View interface for product inventory management operations and display.
 * Extends BaseView to provide product-specific user interactions
 * including product menu display, product information visualization,
 * and category/subcategory selection.
 */
public interface ProductView extends BaseView {

    /**
     * Displays the product management menu to the user.
     * Typically includes options for viewing, adding, updating,
     * removing products, searching, and inventory management.
     */
    void displayProductMenu();

    /**
     * Gets the user's selection from the product management menu.
     *
     * @return the user's menu choice as an integer
     */
    int getProductMenuChoice();

    /**
     * Displays a list of products to the user.
     * Shows product information in a list format, typically with
     * summary details for each product.
     *
     * @param products the list of ProductDTO objects to display
     */
    void displayProducts(List<ProductDTO> products);

    /**
     * Displays detailed information for a single product.
     * Shows comprehensive product details including pricing,
     * inventory, category, and expiration information.
     *
     * @param product the ProductDTO containing detailed product information to display
     */
    void displayProduct(ProductDTO product);

    /**
     * Displays products that are low in stock.
     * Typically highlights products with quantities below threshold
     * to alert for restocking needs.
     *
     * @param products the list of low stock ProductDTO objects to display
     */
    void displayLowStockProducts(List<ProductDTO> products);

    /**
     * Displays products that have expired or are nearing expiration.
     * Typically shows products that need attention due to expiration dates.
     *
     * @param products the list of expired ProductDTO objects to display
     */
    void displayExpiredProducts(List<ProductDTO> products);


    /**
     * Prompts the user to select a product category from available options.
     *
     * This method should present all available product categories to the user
     * and return the selected category as a DTO. The implementation should handle
     * user cancellation gracefully, typically by returning a default category.
     *
     * @return the selected CategoryDTO, or a default category if user cancels
     */
    CategoryDTO getCategoryInput();

    /**
     * Prompts the user to select a product subcategory based on the selected category.
     *
     * This method presents only the subcategories that belong to the specified
     * parent category. The implementation should filter subcategories appropriately
     * and allow the user to select from the filtered list.
     *
     * @param category the parent CategoryDTO to filter subcategories by
     * @return the selected SubcategoryDTO, or null if no subcategories exist
     *         or user cancels selection
     * @throws IllegalArgumentException if category is null
     */
    SubcategoryDTO getSubcategoryInput(CategoryDTO category);
}
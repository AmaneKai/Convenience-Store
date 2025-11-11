package com.konbini.view;

import java.util.List;

import com.konbini.dto.ProductDTO;
import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;

/**
 * Defines the user interface contract specifically for managing and displaying product inventory information.
 * It extends BaseView to inherit fundamental display and input capabilities.
 * 
 * IMPORTANT: This interface uses ProductDTO exclusively - no model imports.
 * Controllers are responsible for converting Product models to ProductDTOs.
 */
public interface ProductView extends BaseView {
    /**
     * Displays the primary menu options available within the product management section
     * (e.g., add product, view all, view low stock).
     */
    void displayProductMenu();

    /**
     * Prompts the user for and retrieves the selection from the product management menu.
     * The implementation must ensure the input is a valid menu option.
     *
     * @return The integer corresponding to the user's selected menu item.
     */
    int getProductMenuChoice();

    /**
     * Displays a formatted list of multiple products.
     * This typically includes key summary details like ID, name, price, and quantity.
     *
     * @param products The list of ProductDTO objects to be displayed.
     */
    void displayProducts(List<ProductDTO> products);

    /**
     * Displays the full, detailed information for a single product.
     *
     * @param product The ProductDTO object whose details are to be displayed.
     */
    void displayProduct(ProductDTO product);

    /**
     * Displays a formatted list containing only products that are currently flagged as having low stock.
     *
     * @param products The list of low stock ProductDTO objects.
     */
    void displayLowStockProducts(List<ProductDTO> products);

    /**
     * Displays a formatted list containing only products that have passed their expiration date.
     *
     * @param products The list of expired ProductDTO objects.
     */
    void displayExpiredProducts(List<ProductDTO> products);

    /**
     * Prompts the user to select a product category from a presented list.
     *
     * @return The selected ProductCategory enum value.
     */
    ProductCategory getCategoryInput();

    /**
     * Prompts the user to select a product subcategory, filtering the available options
     * based on a provided parent category.
     *
     * @param category The ProductCategory used to filter the relevant subcategories.
     * @return The selected ProductSubcategory enum value, or null if no subcategory is chosen.
     */
    ProductSubcategory getSubcategoryInput(ProductCategory category);
}
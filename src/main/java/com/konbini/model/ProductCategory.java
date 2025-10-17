package com.konbini.model;

/**
 * Enumeration representing the high-level categories for products sold in the store.
 * Each category has a defined name for internal use and a more descriptive
 * display name for presentation to users.
 */
public enum ProductCategory {
    /**
     * Category for ready-to-eat meals, snacks, and raw ingredients.
     */
    FOOD("Food"),
    /**
     * Category for all types of drinks, including cold and hot beverages.
     */
    BEVERAGE("Beverage"),
    /**
     * Category for personal hygiene and care products.
     */
    TOILETRIES("Toiletries"),
    /**
     * Category for items used for cleaning and maintenance.
     */
    CLEANING("Cleaning Products"),
    /**
     * Category for over-the-counter health and wellness products.
     */
    MEDICATION("Medications");

    /**
     * A more readable, user-friendly name for the category.
     */
    private final String displayName;

    /**
     * Constructs a ProductCategory enum constant with a specific display name.
     *
     * @param displayName The user-facing name for the category.
     */
    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retrieves the user-friendly display name of the product category.
     *
     * @return The display name string.
     */
    public String getDisplayName() {
        return displayName;
    }
}

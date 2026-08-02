package com.konbini.domain.product;

import java.util.Arrays;
import java.util.Optional;

/**
 * Reference dictionary of high-level product categories.
 */
public enum ProductCategory {
    /**
     * Ready-to-eat meals, snacks and raw ingredients.
     */
    FOOD("Food"),
    /**
     * All types of drinks, hot and cold.
     */
    BEVERAGE("Beverage"),
    /**
     * Personal hygiene and care products.
     */
    TOILETRIES("Toiletries"),
    /**
     * Items used for cleaning and maintenance.
     */
    CLEANING("Cleaning Products"),
    /**
     * Over-the-counter health and wellness products.
     */
    MEDICATION("Medications");

    private final String displayName;

    /**
     * Constructs a category constant.
     *
     * @param displayName the user-facing name
     */
    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-facing display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Finds a category by its display name.
     *
     * @param displayName the display name to match
     * @return an Optional containing the category if matched
     */
    public static Optional<ProductCategory> fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(category -> category.displayName.equals(displayName))
                .findFirst();
    }
}

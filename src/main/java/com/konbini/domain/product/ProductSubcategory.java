package com.konbini.domain.product;

import java.util.Arrays;

/**
 * Reference dictionary of granular product subcategories, each mapped to a parent
 * {@link ProductCategory}.
 */
public enum ProductSubcategory {

    // FOOD
    /**
     * Chips, candies and other ready-to-eat packaged dry goods.
     */
    SNACK("Snack", ProductCategory.FOOD),
    /**
     * Meals requiring minimal or no preparation.
     */
    READY_TO_EAT("Ready to Eat", ProductCategory.FOOD),

    // BEVERAGE
    /**
     * Beverages typically served warm.
     */
    HOT("Hot", ProductCategory.BEVERAGE),
    /**
     * Chilled non-alcoholic drinks.
     */
    COLD("Cold", ProductCategory.BEVERAGE),
    /**
     * Beer, wine and spirits.
     */
    ALCOHOLIC("Alcoholic", ProductCategory.BEVERAGE),

    // TOILETRIES
    /**
     * Bar and liquid soaps.
     */
    SOAP("Soap", ProductCategory.TOILETRIES),
    /**
     * Hair cleansing products.
     */
    SHAMPOO("Shampoo", ProductCategory.TOILETRIES),
    /**
     * Cosmetics and personal beauty products.
     */
    BEAUTY("Beauty Products", ProductCategory.TOILETRIES),

    // CLEANING
    /**
     * Laundry and dishwashing products.
     */
    DETERGENT("Detergent", ProductCategory.CLEANING),
    /**
     * Paper-based cleaning products.
     */
    TISSUE("Tissue", ProductCategory.CLEANING),
    /**
     * Hand hygiene products.
     */
    SANITIZER("Hand Sanitizers", ProductCategory.CLEANING),

    // MEDICATION
    /**
     * Pain-relief medications.
     */
    PAIN_RELIEF("Pain Relief", ProductCategory.MEDICATION),
    /**
     * Cold and flu remedies.
     */
    COLD_FLU("Cold & Flu", ProductCategory.MEDICATION),
    /**
     * Allergy medications.
     */
    ALLERGY("Allergy", ProductCategory.MEDICATION);

    private final String displayName;
    private final ProductCategory category;

    /**
     * Constructs a subcategory constant.
     *
     * @param displayName the user-facing name
     * @param category the parent category
     */
    ProductSubcategory(String displayName, ProductCategory category) {
        this.displayName = displayName;
        this.category = category;
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
     * Returns the parent category.
     *
     * @return the parent category
     */
    public ProductCategory getCategory() {
        return category;
    }

    /**
     * Returns all subcategories belonging to a category.
     *
     * @param category the parent category to filter by
     * @return an array of subcategories for that category
     */
    public static ProductSubcategory[] getSubcategoriesFor(ProductCategory category) {
        return Arrays.stream(values())
                .filter(subcategory -> subcategory.category == category)
                .toArray(ProductSubcategory[]::new);
    }
}

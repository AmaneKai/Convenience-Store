package com.konbini.model;

/**
 * Enumeration representing detailed subcategories for products, each mapped
 * to a higher-level product category. This provides granular organization
 * and filtering capability for products in the store.
 */
public enum ProductSubcategory {

    // FOOD Subcategories
    /**
     * Subcategory for chips, candies, crackers, and other ready-to-eat packaged dry goods.
     */
    SNACK("Snack", ProductCategory.FOOD),
    /**
     * Subcategory for meals that require minimal or no preparation, such as sandwiches or cup noodles.
     */
    READY_TO_EAT("Ready to Eat", ProductCategory.FOOD),

    // BEVERAGE Subcategories
    /**
     * Subcategory for beverages typically served warm (e.g., coffee, tea).
     */
    HOT("Hot", ProductCategory.BEVERAGE),
    /**
     * Subcategory for chilled non-alcoholic drinks (e.g., sodas, juices, water).
     */
    COLD("Cold", ProductCategory.BEVERAGE),
    /**
     * Subcategory for beer, wine, and spirits.
     */
    ALCOHOLIC("Alcoholic", ProductCategory.BEVERAGE),

    // TOILETRIES Subcategories
    /**
     * Subcategory for bar and liquid soaps.
     */
    SOAP("Soap", ProductCategory.TOILETRIES),
    /**
     * Subcategory for hair cleansing products.
     */
    SHAMPOO("Shampoo", ProductCategory.TOILETRIES),
    /**
     * Subcategory for cosmetics, makeup, and other personal beauty enhancements.
     */
    BEAUTY("Beauty Products", ProductCategory.TOILETRIES),

    // CLEANING Subcategories
    /**
     * Subcategory for laundry and dishwashing products.
     */
    DETERGENT("Detergent", ProductCategory.CLEANING),
    /**
     * Subcategory for paper-based cleaning and personal use products.
     */
    TISSUE("Tissue", ProductCategory.CLEANING),
    /**
     * Subcategory for products designed to kill germs on hands.
     */
    SANITIZER("Hand Sanitizers", ProductCategory.CLEANING),

    // MEDICATION Subcategories
    /**
     * Subcategory for products intended to alleviate pain.
     */
    PAIN_RELIEF("Pain Relief", ProductCategory.MEDICATION),
    /**
     * Subcategory for non-prescription remedies for cold and flu symptoms.
     */
    COLD_FLU("Cold & Flu", ProductCategory.MEDICATION),
    /**
     * Subcategory for non-prescription medications to treat allergies.
     */
    ALLERGY("Allergy", ProductCategory.MEDICATION);

    /**
     * A more readable, user-friendly name for the subcategory.
     */
    private final String displayName;
    /**
     * The high-level product category this subcategory belongs to.
     */
    private final ProductCategory category;

    /**
     * Constructs a ProductSubcategory constant.
     *
     * @param displayName The user-facing name for the subcategory.
     * @param category The parent ProductCategory for this subcategory.
     */
    ProductSubcategory(String displayName, ProductCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    /**
    * Gets all subcategories belonging to a specific category.
    * @param category The parent ProductCategory to filter by.
    * @return An array of ProductSubcategory values for that category.
    */

    public static ProductSubcategory[] getSubcategoriesFor(ProductCategory category) {
        return java.util.Arrays.stream(ProductSubcategory.values())
        .filter(sub -> sub.getCategory() == category)
        .toArray(ProductSubcategory[]::new);
    }

    /**
     * Retrieves the user-friendly display name of the product subcategory.
     *
     * @return The display name string.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Retrieves the high-level category that this subcategory belongs to.
     *
     * @return The parent ProductCategory.
     */
    public ProductCategory getCategory() {
        return category;
    }

}

package com.konbini.model;

public enum ProductSubcategory {

    SNACK("Snack", ProductCategory.FOOD),
    READY_TO_EAT("Ready to Eat", ProductCategory.FOOD),

    HOT("Hot", ProductCategory.BEVERAGE),
    COLD("Cold", ProductCategory.BEVERAGE),
    ALCOHOLIC("Alcoholic", ProductCategory.BEVERAGE),

    SOAP("Soap", ProductCategory.TOILETRIES),
    SHAMPOO("Shampoo", ProductCategory.TOILETRIES),
    BEAUTY("Beauty Products", ProductCategory.TOILETRIES),
    
    DETERGENT("Detergent", ProductCategory.CLEANING),
    TISSUE("Tissue", ProductCategory.CLEANING),
    SANITIZER("Hand Sanitizers", ProductCategory.CLEANING),

    PAIN_RELIEF("Pain Relief", ProductCategory.MEDICATION),
    COLD_FLU("Cold & Flu", ProductCategory.MEDICATION),
    ALLERGY("Allergy", ProductCategory.MEDICATION);

    private final String displayName;
    private final ProductCategory category;
    
    ProductSubcategory(String displayName, ProductCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ProductCategory getCategory() {
        return category;
    }

}



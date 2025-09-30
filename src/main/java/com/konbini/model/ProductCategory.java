package com.konbini.model;

public enum ProductCategory {
    FOOD("Food"),
    BEVERAGE("Beverage"),
    TOILETRIES("Toiletries"),
    CLEANING("Cleaning Products"),
    MEDICATION("Medications");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

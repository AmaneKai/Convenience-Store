package com.konbini.dto;

import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SubcategoryDTO {
    private final String displayName;

    public SubcategoryDTO(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Convert from model to DTO
    public static SubcategoryDTO fromModel(ProductSubcategory subcategory) {
        return new SubcategoryDTO(subcategory.getDisplayName());
    }

    // Convert DTO back to model
    public ProductSubcategory toModel() {
        return Arrays.stream(ProductSubcategory.values())
            .filter(s -> s.getDisplayName().equals(displayName))
            .findFirst()
            .orElse(null);
    }

    // Get subcategories for a specific category
    public static List<SubcategoryDTO> getSubcategoriesFor(CategoryDTO category) {
        ProductCategory productCategory = category.toModel();
        return Arrays.stream(ProductSubcategory.getSubcategoriesFor(productCategory))
            .map(SubcategoryDTO::fromModel)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return displayName;
    }
}

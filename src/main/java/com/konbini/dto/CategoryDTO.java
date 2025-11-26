package com.konbini.dto;

import com.konbini.model.ProductCategory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryDTO {
    private final String displayName;

    public CategoryDTO(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Convert from model to DTO
    public static CategoryDTO fromModel(ProductCategory category) {
        return new CategoryDTO(category.getDisplayName());
    }

    // Convert DTO back to model
    public ProductCategory toModel() {
        return Arrays.stream(ProductCategory.values())
            .filter(c -> c.getDisplayName().equals(displayName))
            .findFirst()
            .orElse(ProductCategory.FOOD);
    }

    // Get all categories as DTOs
    public static List<CategoryDTO> getAllCategories() {
        return Arrays.stream(ProductCategory.values())
            .map(CategoryDTO::fromModel)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return displayName;
    }
}

package com.konbini.dto;

import com.konbini.model.ProductCategory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for ProductCategory enum.
 * Provides a view-layer representation of product categories, decoupling
 * the view from domain models while maintaining type safety and conversion capabilities.
 *
 * <p>This DTO encapsulates category information and provides bidirectional
 * conversion between the domain model (ProductCategory enum) and the presentation layer.</p>
 */
public class CategoryDTO {
    /**
     * The display name of the category, suitable for presentation to users.
     */
    private final String displayName;

    /**
     * Constructs a CategoryDTO with the specified display name.
     *
     * @param displayName the user-friendly display name for the category
     */
    public CategoryDTO(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of this category.
     *
     * @return the user-friendly display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a ProductCategory domain model to a CategoryDTO.
     *
     * <p>This factory method creates a DTO representation from the domain model,
     * extracting the display name for presentation purposes.</p>
     *
     * @param category the ProductCategory enum to convert
     * @return a new CategoryDTO representing the given category
     * @throws NullPointerException if category is null
     */
    public static CategoryDTO fromModel(ProductCategory category) {
        return new CategoryDTO(category.getDisplayName());
    }

    /**
     * Converts this DTO back to the corresponding ProductCategory domain model.
     *
     * <p>This method performs a reverse lookup to find the ProductCategory enum
     * that matches this DTO's display name. If no match is found, it defaults
     * to ProductCategory.FOOD.</p>
     *
     * @return the corresponding ProductCategory enum value
     */
    public ProductCategory toModel() {
        return Arrays.stream(ProductCategory.values())
            .filter(c -> c.getDisplayName().equals(displayName))
            .findFirst()
            .orElse(ProductCategory.FOOD);
    }

    /**
     * Retrieves all available product categories as DTOs.
     *
     * <p>This method converts all ProductCategory enum values into their
     * corresponding DTO representations, suitable for populating UI selection
     * controls such as dropdowns or list boxes.</p>
     *
     * @return a list of all CategoryDTOs representing all available categories
     */
    public static List<CategoryDTO> getAllCategories() {
        return Arrays.stream(ProductCategory.values())
            .map(CategoryDTO::fromModel)
            .collect(Collectors.toList());
    }

    /**
     * Returns the display name as the string representation of this DTO.
     *
     * <p>This allows the DTO to be directly used in UI components that
     * call toString() for display purposes (e.g., JComboBox).</p>
     *
     * @return the display name of this category
     */
    @Override
    public String toString() {
        return displayName;
    }
}

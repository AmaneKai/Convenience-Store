package com.konbini.dto;

import com.konbini.model.ProductCategory;
import com.konbini.model.ProductSubcategory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for ProductSubcategory enum.
 * Provides a view-layer representation of product subcategories, maintaining
 * separation between the presentation layer and domain models.
 *
 * <p>This DTO handles subcategory information and provides conversion utilities
 * for moving data between the view and domain layers, including support for
 * filtering subcategories by their parent category.</p>
 */
public class SubcategoryDTO {
    /**
     * The display name of the subcategory, suitable for presentation to users.
     */
    private final String displayName;

    /**
     * Constructs a SubcategoryDTO with the specified display name.
     *
     * @param displayName the user-friendly display name for the subcategory
     */
    public SubcategoryDTO(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of this subcategory.
     *
     * @return the user-friendly display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a ProductSubcategory domain model to a SubcategoryDTO.
     *
     * <p>This factory method creates a DTO representation from the domain model,
     * extracting only the presentation-relevant information (display name).</p>
     *
     * @param subcategory the ProductSubcategory enum to convert
     * @return a new SubcategoryDTO representing the given subcategory
     * @throws NullPointerException if subcategory is null
     */
    public static SubcategoryDTO fromModel(ProductSubcategory subcategory) {
        return new SubcategoryDTO(subcategory.getDisplayName());
    }

    /**
     * Converts this DTO back to the corresponding ProductSubcategory domain model.
     *
     * <p>This method performs a reverse lookup to find the ProductSubcategory enum
     * that matches this DTO's display name. If no match is found, returns null.</p>
     *
     * @return the corresponding ProductSubcategory enum value, or null if not found
     */
    public ProductSubcategory toModel() {
        return Arrays.stream(ProductSubcategory.values())
            .filter(s -> s.getDisplayName().equals(displayName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Retrieves all subcategories that belong to a specific category.
     *
     * <p>This method filters the available subcategories to only those that
     * belong to the specified parent category, returning them as DTOs suitable
     * for presentation in the view layer. This is useful for populating
     * dependent selection controls where subcategory options depend on the
     * selected category.</p>
     *
     * @param category the parent CategoryDTO to filter subcategories by
     * @return a list of SubcategoryDTOs belonging to the specified category
     * @throws NullPointerException if category is null
     */
    public static List<SubcategoryDTO> getSubcategoriesFor(CategoryDTO category) {
        ProductCategory productCategory = category.toModel();
        return Arrays.stream(ProductSubcategory.getSubcategoriesFor(productCategory))
            .map(SubcategoryDTO::fromModel)
            .collect(Collectors.toList());
    }

    /**
     * Returns the display name as the string representation of this DTO.
     *
     * <p>This allows the DTO to be directly used in UI components that
     * call toString() for display purposes (e.g., JComboBox, JList).</p>
     *
     * @return the display name of this subcategory
     */
    @Override
    public String toString() {
        return displayName;
    }
}

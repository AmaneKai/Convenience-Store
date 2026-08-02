package com.konbini.application.validation;

import com.konbini.application.command.AddProductCommand;
import com.konbini.application.command.RestockProductCommand;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.product.ProductCategory;
import com.konbini.domain.product.ProductSubcategory;
import io.vavr.control.Option;
import java.util.Arrays;

/**
 * Validator for product-related commands, including domain invariant rules such
 * as non-negative stock and valid category membership.
 */
public class ProductValidator {

    /**
     * Validates an add-product command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validateAdd(AddProductCommand command) {
        return new ValidationRule<>(command)
                .requiredText(command.name(), "Product name cannot be empty")
                .greaterThanZero(command.price(), "Product price must be greater than 0")
                .notNegative(command.quantity(), "Product quantity cannot be negative")
                .requiredText(command.category(), "Product category cannot be empty")
                .check(() -> matchesCategory(command.category(), command.variant()),
                        "Subcategory must belong to the specified category")
                .notInPast(command.expirationDate(), "Expiration date cannot be in the past")
                .result();
    }

    /**
     * Validates a restock command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validateRestock(RestockProductCommand command) {
        return new ValidationRule<>(command)
                .requiredText(command.productId(), "Product ID cannot be empty")
                .greaterThanZero(command.quantity(), "Restock quantity must be positive")
                .result();
    }

    /**
     * Verifies that a variant display name, when present, belongs to the given category.
     *
     * @param categoryDisplayName the category display name
     * @param variantDisplayName the variant display name (may be null)
     * @return true if the variant is valid for the category
     */
    private boolean matchesCategory(String categoryDisplayName, String variantDisplayName) {
        if (variantDisplayName == null || variantDisplayName.trim().isEmpty()) {
            return true;
        }
        return ProductCategory.fromDisplayName(categoryDisplayName)
                .map(category -> Arrays.stream(ProductSubcategory.getSubcategoriesFor(category))
                        .anyMatch(subcategory -> subcategory.getDisplayName().equals(variantDisplayName)))
                .orElse(false);
    }
}

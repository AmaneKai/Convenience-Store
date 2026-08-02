package com.konbini.view.checkout;

import com.konbini.application.dto.ProductDTO;
import javafx.util.StringConverter;

/**
 * Converts a product DTO to its display string for the combo box.
 */
public final class ProductStringConverter extends StringConverter<ProductDTO> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(ProductDTO product) {
        return product == null ? "" : product.name() + " — " + product.id();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductDTO fromString(String name) {
        return null;
    }
}

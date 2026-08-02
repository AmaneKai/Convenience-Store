package com.konbini.view.checkout;

import com.konbini.application.dto.CustomerDTO;
import javafx.util.StringConverter;

/**
 * Converts a customer DTO to its display string for the combo box.
 */
public final class CustomerStringConverter extends StringConverter<CustomerDTO> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString(CustomerDTO customer) {
        return customer == null ? "" : customer.name();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomerDTO fromString(String name) {
        return null;
    }
}

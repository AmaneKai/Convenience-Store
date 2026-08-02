package com.konbini.application.command;

import java.time.LocalDate;

/**
 * Request to register a new customer.
 */
public record RegisterCustomerCommand(String name, boolean seniorCitizen,
                                      String cardNumber, LocalDate cardExpiryDate) {

    /**
     * Constructs a request without a membership card.
     *
     * @param name the customer name
     * @param seniorCitizen the senior-citizen status
     */
    public RegisterCustomerCommand(String name, boolean seniorCitizen) {
        this(name, seniorCitizen, null, null);
    }
}

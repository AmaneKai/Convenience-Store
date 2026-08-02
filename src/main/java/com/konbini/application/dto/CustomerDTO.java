package com.konbini.application.dto;

import com.konbini.domain.customer.Customer;
import java.time.LocalDate;

/**
 * Immutable presentation snapshot of a {@link Customer}.
 */
public record CustomerDTO(String id, String name, boolean seniorCitizen,
                          boolean hasMembershipCard, String cardNumber,
                          int points, LocalDate cardExpiryDate, boolean hasPassword) {

    /**
     * Creates a DTO from a domain customer.
     *
     * @param customer the domain customer
     * @return the DTO snapshot
     */
    public static CustomerDTO fromDomain(Customer customer) {
        boolean hasCard = customer.hasMembershipCard();
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.isSeniorCitizen(),
                hasCard,
                hasCard ? customer.getMembershipCard().getCardNumber() : null,
                hasCard ? customer.getMembershipCard().getPoints() : 0,
                hasCard ? customer.getMembershipCard().getExpiryDate() : null,
                customer.hasPassword());
    }
}

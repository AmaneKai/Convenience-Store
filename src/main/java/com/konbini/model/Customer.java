package com.konbini.model;

import com.konbini.util.IdGenerator;
import java.io.Serializable;

/**
 * Represents a customer of the convenience store.
 * This domain model stores essential customer information, including their
 * unique identifier, name, senior citizen status, and optional loyalty membership.
 * The customer ID is generated automatically upon creation.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique, auto-generated identifier for the customer. This field is immutable.
     */
    private final String id;
    /**
     * The name of the customer.
     */
    private String name;
    /**
     * The optional loyalty membership card associated with the customer.
     * This field is null if the customer does not have a card.
     */
    private MembershipCard membershipCard;
    /**
     * Flag indicating if the customer has senior citizen status, which may qualify
     * them for specific discounts.
     */
    private boolean isSeniorCitizen;

    /**
     * Constructs a new Customer instance, automatically generating a unique ID.
     *
     * @param name The name of the customer.
     * @param isSeniorCitizen The initial senior citizen status of the customer.
     */
    public Customer(String name, boolean isSeniorCitizen) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException
                ("Customer name cannot be null or empty");
        }

        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException
                ("Name must contain only letters and spaces");
        }

        this.id = IdGenerator.getInstance().generateId("customer");
        this.name = name;
        this.isSeniorCitizen = isSeniorCitizen;
    }


    /**
     * Retrieves the unique identifier of the customer.
     *
     * @return The customer ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the name of the customer.
     *
     * @return The customer's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the customer.
     *
     * @param name The new name to set.
     */
    public void setName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException
                ("Customer name cannot be null or empty");
        }

        if (!name.matches("^[a-zA-Z\\s]+$")) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }

        this.name = name;
    }

    /**
     * Retrieves the customer's membership card.
     *
     * @return The MembershipCard object, or null if the customer does not have one.
     */
    public MembershipCard getMembershipCard() {
        return membershipCard;
    }

    /**
     * Sets or updates the customer's membership card.
     *
     * @param membershipCard The MembershipCard object to associate with the customer.
     */
    public void setMembershipCard(MembershipCard membershipCard) {
        if (membershipCard == null) {
            throw new IllegalArgumentException
            ("Membership card cannot be null");
        }
        this.membershipCard = membershipCard;
    }

    /**
     * Checks if the customer currently has a membership card associated with their profile.
     *
     * @return True if a MembershipCard exists (is not null), false otherwise.
     */
    public boolean hasMembershipCard() {
        return membershipCard != null;
    }

    /**
     * Checks if the customer is registered as a senior citizen.
     *
     * @return True if the customer is a senior citizen, false otherwise.
     */
    public boolean isSeniorCitizen() {
        return isSeniorCitizen;
    }

    /**
     * Sets the senior citizen status for the customer.
     *
     * @param seniorCitizen The new senior citizen status to set.
     */
    public void setSeniorCitizen(boolean seniorCitizen) {
        isSeniorCitizen = seniorCitizen;
    }
}

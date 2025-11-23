package com.konbini.model;

import java.io.Serializable;

import com.konbini.util.IdGenerator;

/**
 * Represents a customer in the store system with personal information and membership details.
 * Customers can have membership cards and may be classified as senior citizens for discounts.
 * Implements Serializable to support persistence.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Regular expression pattern for validating customer names.
     * Names must contain only letters and spaces.
     */
    private static final String NAME_PATTERN = "^[a-zA-Z\\s]+$";

    private final String id;
    private String name;
    private MembershipCard membershipCard;
    private boolean isSeniorCitizen;

    /**
     * Constructs a new Customer with the specified name and senior citizen status.
     * Automatically generates a unique ID for the customer.
     *
     * @param name the customer's name (must contain only letters and spaces)
     * @param isSeniorCitizen whether the customer is a senior citizen
     * @throws IllegalArgumentException if name is null, empty, or contains invalid characters
     */
    public Customer(String name, boolean isSeniorCitizen) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }

        this.id = IdGenerator.getInstance().generateId("customer");
        this.name = name;
        this.isSeniorCitizen = isSeniorCitizen;
    }

    /**
     * Gets the customer's unique identifier.
     *
     * @return the customer ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the customer's name.
     *
     * @return the customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's name.
     *
     * @param name the new name for the customer
     * @throws IllegalArgumentException if name is null, empty, or contains invalid characters
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }
        this.name = name;
    }

    /**
     * Gets the customer's membership card, if any.
     *
     * @return the membership card, or null if the customer doesn't have one
     */
    public MembershipCard getMembershipCard() {
        return membershipCard;
    }

    /**
     * Sets a membership card for the customer.
     *
     * @param membershipCard the membership card to assign to the customer
     * @throws IllegalArgumentException if membershipCard is null
     */
    public void setMembershipCard(MembershipCard membershipCard) {
        if (membershipCard == null) {
            throw new IllegalArgumentException("Membership card cannot be null");
        }
        this.membershipCard = membershipCard;
    }

    /**
     * Removes the membership card from the customer.
     * After calling this method, hasMembershipCard() will return false.
     */
    public void removeMembershipCard() {
        this.membershipCard = null;
    }

    /**
     * Checks whether the customer has a membership card.
     *
     * @return true if the customer has a membership card, false otherwise
     */
    public boolean hasMembershipCard() {
        return membershipCard != null;
    }

    /**
     * Checks whether the customer is a senior citizen.
     * Senior citizens may be eligible for special discounts.
     *
     * @return true if the customer is a senior citizen, false otherwise
     */
    public boolean isSeniorCitizen() {
        return isSeniorCitizen;
    }

    /**
     * Sets the senior citizen status of the customer.
     *
     * @param seniorCitizen true to mark the customer as a senior citizen, false otherwise
     */
    public void setSeniorCitizen(boolean seniorCitizen) {
        isSeniorCitizen = seniorCitizen;
    }
}
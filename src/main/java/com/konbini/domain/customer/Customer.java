package com.konbini.domain.customer;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Aggregate root representing a store customer, including optional membership
 * and senior-citizen status used for discount eligibility.
 */
@Getter
public class Customer {

    /**
     * Regular expression for validating customer names (letters and spaces only).
     */
    public static final String NAME_PATTERN = "^[a-zA-Z\\s]+$";

    private final String id;
    private String name;
    private boolean seniorCitizen;
    private MembershipCard membershipCard;
    private String passwordHash;

    /**
     * Constructs a fully specified Customer.
     *
     * @param id the customer identifier
     * @param name the customer name
     * @param seniorCitizen whether the customer is a senior citizen
     * @param membershipCard the customer's membership card (may be null)
     * @param passwordHash the salted hash of the customer's self-service login
     *     password, or null if the customer has no login enabled
     * @throws IllegalArgumentException if id or name are invalid
     */
    @Builder
    public Customer(String id, String name, boolean seniorCitizen, MembershipCard membershipCard,
                     String passwordHash) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }

        this.id = id;
        this.name = name;
        this.seniorCitizen = seniorCitizen;
        this.membershipCard = membershipCard;
        this.passwordHash = passwordHash;
    }

    /**
     * Changes the customer name.
     *
     * @param newName the new name
     * @throws IllegalArgumentException if the new name is invalid
     */
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (!newName.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }
        this.name = newName;
    }

    /**
     * Updates the senior-citizen status.
     *
     * @param seniorCitizen the new status
     */
    public void updateSeniorCitizenStatus(boolean seniorCitizen) {
        this.seniorCitizen = seniorCitizen;
    }

    /**
     * Assigns a membership card to this customer.
     *
     * @param newMembershipCard the card to assign
     * @throws IllegalArgumentException if the card is null
     */
    public void assignMembershipCard(MembershipCard newMembershipCard) {
        if (newMembershipCard == null) {
            throw new IllegalArgumentException("Membership card cannot be null");
        }
        this.membershipCard = newMembershipCard;
    }

    /**
     * Removes the membership card, if any.
     */
    public void removeMembershipCard() {
        this.membershipCard = null;
    }

    /**
     * Returns whether this customer holds a membership card.
     *
     * @return true if a card is assigned
     */
    public boolean hasMembershipCard() {
        return membershipCard != null;
    }

    /**
     * Returns whether this customer has self-service login enabled.
     *
     * @return true if a password hash is set
     */
    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isEmpty();
    }

    /**
     * Sets or replaces the stored password hash, enabling self-service login.
     *
     * @param newPasswordHash the new salted hash
     * @throws IllegalArgumentException if the hash is null or empty
     */
    public void updatePasswordHash(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
        this.passwordHash = newPasswordHash;
    }
}

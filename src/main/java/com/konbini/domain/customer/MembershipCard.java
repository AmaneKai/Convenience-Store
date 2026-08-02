package com.konbini.domain.customer;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * Value-like entity tracking loyalty points and expiration for a customer's
 * membership card. Points accumulate at one point per fifty pesos spent and
 * may be redeemed at one peso per point.
 */
@Getter
public class MembershipCard {

    private final String id;
    private final String cardNumber;
    private LocalDate expiryDate;
    private int points;

    /**
     * Constructs a membership card.
     *
     * @param id the internal card identifier
     * @param cardNumber the user-facing card number
     * @param expiryDate the card expiry date
     * @param points the initial points balance
     * @throws IllegalArgumentException if any required value is invalid
     */
    @Builder
    public MembershipCard(String id, String cardNumber, LocalDate expiryDate, Integer points) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Card ID cannot be null or empty");
        }
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null");
        }

        this.id = id;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.points = points != null ? points : 0;
    }

    /**
     * Adds points to the balance.
     *
     * @param pointsToAdd the points to add
     * @throws IllegalArgumentException if the amount is negative
     * @throws IllegalStateException if the card has expired
     */
    public void addPoints(int pointsToAdd) {
        if (pointsToAdd < 0) {
            throw new IllegalArgumentException("Points to add must be non-negative");
        }
        if (isExpired()) {
            throw new IllegalStateException("Cannot add points to an expired membership card");
        }
        this.points += pointsToAdd;
    }

    /**
     * Deducts points from the balance.
     *
     * @param pointsToDeduct the points to deduct
     * @throws IllegalArgumentException if the amount is negative or exceeds balance
     */
    public void deductPoints(int pointsToDeduct) {
        if (pointsToDeduct < 0) {
            throw new IllegalArgumentException("Points to deduct must be non-negative");
        }
        if (pointsToDeduct > this.points) {
            throw new IllegalArgumentException(
                    "Cannot deduct more points than available. "
                            + "Available: " + this.points + ", Requested: " + pointsToDeduct);
        }
        this.points -= pointsToDeduct;
    }

    /**
     * Returns whether the card has expired relative to today.
     *
     * @return true if the card is expired
     */
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    /**
     * Updates the expiry date.
     *
     * @param newExpiryDate the new expiry date
     * @throws IllegalArgumentException if the new date is null
     */
    public void updateExpiryDate(LocalDate newExpiryDate) {
        if (newExpiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null");
        }
        this.expiryDate = newExpiryDate;
    }
}

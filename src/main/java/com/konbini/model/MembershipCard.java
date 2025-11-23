package com.konbini.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.konbini.util.IdGenerator;

/**
 * Represents a membership card for customer loyalty programs.
 * Tracks loyalty points and manages card expiration status.
 * Implements Serializable to support persistence.
 */
public class MembershipCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String cardNumber;
    private int points;
    private LocalDate expiryDate;

    /**
     * Constructs a new MembershipCard with the specified card number and expiry date.
     * Automatically generates a unique ID and initializes points to zero.
     *
     * @param cardNumber the card number (cannot be null or empty)
     * @param expiryDate the card expiry date (cannot be null)
     * @throws IllegalArgumentException if cardNumber is null/empty or expiryDate is null
     */
    public MembershipCard(String cardNumber, LocalDate expiryDate) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null");
        }

        this.id = IdGenerator.getInstance().generateId("card");
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.points = 0;
    }

    /**
     * Gets the membership card's unique identifier.
     * The ID is automatically generated during construction and cannot be changed.
     *
     * @return the card ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the card number.
     *
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the card number.
     *
     * @param cardNumber the new card number
     * @throws IllegalArgumentException if cardNumber is null or empty
     */
    public void setCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the current loyalty points balance.
     *
     * @return the number of loyalty points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Adds points to the membership card.
     *
     * @param pointsToAdd the number of points to add (must be non-negative)
     * @throws IllegalArgumentException if pointsToAdd is negative
     * @throws IllegalStateException if the membership card is expired
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
     * Deducts points from the membership card.
     *
     * @param pointsToDeduct the number of points to deduct (must be non-negative)
     * @throws IllegalArgumentException if pointsToDeduct is negative or exceeds available points
     */
    public void deductPoints(int pointsToDeduct) {
        if (pointsToDeduct < 0) {
            throw new IllegalArgumentException("Points to deduct must be non-negative");
        }
        if (pointsToDeduct > this.points) {
            throw new IllegalArgumentException(
                "Cannot deduct more points than available. " +
                "Available: " + this.points + ", Requested: " + pointsToDeduct);
        }
        this.points -= pointsToDeduct;
    }

    /**
     * Gets the card expiry date.
     *
     * @return the expiry date
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Checks if the membership card has expired.
     *
     * @return true if the card has expired, false otherwise
     * @throws IllegalStateException if expiry date is null (should never occur)
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            throw new IllegalStateException("Expiry data should never be null");
        }
        return expiryDate.isBefore(LocalDate.now());
    }
}
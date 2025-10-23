package com.konbini.model;

import com.konbini.util.IdGenerator;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a customer's loyalty membership card.
 * This domain model tracks the card details, including its unique number,
 * expiration date, and current loyalty points balance. It provides methods
 * for managing points and checking card validity.
 */
public class MembershipCard implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The unique, auto-generated internal identifier for the card record.
     */
    private final String id;
    /**
     * The external, physical number printed on the card.
     */
    private String cardNumber;
    /**
     * The current loyalty points balance.
     */
    private int points;
    /**
     * The date on which the card is no longer valid.
     */
    private LocalDate expiryDate;

    /**
     * Constructs a new MembershipCard instance, automatically generating an ID and
     * initializing the points balance to zero.
     *
     * @param cardNumber The unique external number of the card.
     * @param expiryDate The expiration date of the card.
     */
    public MembershipCard(String cardNumber, LocalDate expiryDate) {
        this.id = IdGenerator.getInstance().generateId("card");
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.points = 0;
    }

    /**
     * Retrieves the unique internal ID of the card record.
     *
     * @return The internal card ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the external card number.
     *
     * @return The card number string.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets a new external card number.
     *
     * @param cardNumber The new card number to set.
     */
    public void setCardNumber(String cardNumber) {

        if (!cardNumber.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Card number must be numeric");
        }

        this.cardNumber = cardNumber;
    }

    /**
     * Retrieves the current loyalty points balance.
     *
     * @return The current number of points.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Adds a specified amount of points to the current balance.
     *
     * @param pointsToAdd The number of points to add.
     */
    public void addPoints(int pointsToAdd) {
        if (isExpired()) {
            throw new IllegalStateException("Cannot add points to an expired membership card");
        }

        if (pointsToAdd < 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        }

        if (pointsToAdd == 0) {
            return;
        }
         
        this.points += pointsToAdd;
    }

    /**
     * Deducts a specified amount of points from the current balance.
     *
     * @param pointsToDeduct The number of points to deduct.
     * @throws IllegalArgumentException if the deduction amount exceeds the available points.
     */
    public void deductPoints(int pointsToDeduct) {
        if (pointsToDeduct > this.points) {
            throw new IllegalArgumentException
            ("Cannot deduct more points than available");
        }

        this.points -= pointsToDeduct;
    }

    /**
     * Retrieves the expiration date of the membership card.
     *
     * @return The card's expiry date.
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
   }

    /**
     * Checks if the membership card has expired.
     * The card is expired if its expiration date is before the current date.
     *
     * @return True if the card is expired, false otherwise.
     */
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }
}

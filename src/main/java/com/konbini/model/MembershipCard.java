package com.konbini.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.konbini.util.IdGenerator;

public class MembershipCard implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String cardNumber;
    private int points;
    private LocalDate expiryDate;

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

    public String getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        this.cardNumber = cardNumber;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pointsToAdd) {
        if (pointsToAdd < 0) {
            throw new IllegalArgumentException("Points to add must be non-negative");
        }
        if (isExpired()) {
            throw new IllegalStateException("Cannot add points to an expired membership card");
        }
        
        this.points += pointsToAdd;
    }

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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        if (expiryDate == null) {
            throw new IllegalStateException("Expiry data should never be null");
        }
        return expiryDate.isBefore(LocalDate.now());
    }
}
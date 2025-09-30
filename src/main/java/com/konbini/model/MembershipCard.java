package com.konbini.model;

import com.konbini.util.IdGenerator;
import java.io.Serializable;
import java.time.LocalDate;

public class MembershipCard implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String id;
    private String cardNumber;
    private int points;
    private LocalDate expiryDate;
    
    public MembershipCard(String cardNumber, LocalDate expiryDate) {
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
        this.cardNumber = cardNumber;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    public void deductPoints(int pointsToDeduct) {
        if (pointsToDeduct > this.points) {
            throw new IllegalArgumentException
            ("Cannot defuct more points than available");
        }

        this.points -= pointsToDeduct;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }
}

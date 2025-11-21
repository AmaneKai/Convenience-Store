package com.konbini.model;

import java.io.Serializable;

import com.konbini.util.IdGenerator;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String NAME_PATTERN = "^[a-zA-Z\\s]+$";

    private final String id;
    private String name;
    private MembershipCard membershipCard;
    private boolean isSeniorCitizen;

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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException("Name must contain only letters and spaces");
        }
        this.name = name;
    }

    public MembershipCard getMembershipCard() {
        return membershipCard;
    }

    public void setMembershipCard(MembershipCard membershipCard) {
        if (membershipCard == null) {
            throw new IllegalArgumentException("Membership card cannot be null");
        }
        this.membershipCard = membershipCard;
    }

    public void removeMembershipCard() {
        this.membershipCard = null;
    }

    public boolean hasMembershipCard() {
        return membershipCard != null;
    }

    public boolean isSeniorCitizen() {
        return isSeniorCitizen;
    }

    public void setSeniorCitizen(boolean seniorCitizen) {
        isSeniorCitizen = seniorCitizen;
    }
}
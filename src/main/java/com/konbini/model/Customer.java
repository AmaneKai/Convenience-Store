package com.konbini.model;

import com.konbini.util.IdGenerator;
import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private MembershipCard membershipCard;
    private boolean isSeniorCitizen;

    public Customer(String name, boolean isSeniorCitizen) {
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
        this.name = name;
    }

    public MembershipCard getMembershipCard() {
        return membershipCard;
    }

    public void setMembershipCard(MembershipCard membershipCard) {
        this.membershipCard = membershipCard;
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

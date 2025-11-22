package com.konbini.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;

public class CustomerDTO {
    private String id;
    private String name;
    private boolean seniorCitizen;
    private boolean hasMembershipCard;
    private String cardNumber;
    private int points;
    private LocalDate cardExpiryDate;
    private boolean cardExpired;

    public CustomerDTO() {
    }

    public CustomerDTO(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        
        this.id = customer.getId();
        this.name = customer.getName();
        this.seniorCitizen = customer.isSeniorCitizen();
        this.hasMembershipCard = customer.hasMembershipCard();

        if (hasMembershipCard && customer.getMembershipCard() != null) {
            MembershipCard card = customer.getMembershipCard();
            this.cardNumber = card.getCardNumber();
            this.points = card.getPoints();
            this.cardExpiryDate = card.getExpiryDate();
            this.cardExpired = card.isExpired();
        }
    }

    public static CustomerDTO fromModel(Customer customer) {
        return new CustomerDTO(customer);
    }

    public static List<CustomerDTO> fromModelList(List<Customer> customers) {
        List<CustomerDTO> dtos = new ArrayList<>();

        if (customers != null) {
            for (Customer customer : customers) {
                if (customer != null) {
                    dtos.add(fromModel(customer));
                }
            }
        }

        return dtos;
    }

    public Customer toModel() {
        Customer customer = new Customer(name, seniorCitizen);

        if (hasMembershipCard && cardNumber != null && cardExpiryDate != null) {
            MembershipCard card = new MembershipCard(cardNumber, cardExpiryDate);
            if (points > 0) {
                card.addPoints(points);
            }
            customer.setMembershipCard(card);
        }

        return customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSeniorCitizen() {
        return seniorCitizen;
    }

    public void setSeniorCitizen(boolean seniorCitizen) {
        this.seniorCitizen = seniorCitizen;
    }

    public boolean isHasMembershipCard() {
        return hasMembershipCard;
    }

    public void setHasMembershipCard(boolean hasMembershipCard) {
        this.hasMembershipCard = hasMembershipCard;
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

    public void setPoints(int points) {
        this.points = points;
    }

    public LocalDate getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(LocalDate cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public boolean isCardExpired() {
        return cardExpired;
    }

    public void setCardExpired(boolean cardExpired) {
        this.cardExpired = cardExpired;
    }

    public String getCardId() {
        String temp = null;

        if (hasMembershipCard && cardNumber != null) {
            temp = cardNumber;
        }

        return temp;
    }
    @Override
    public String toString() {
        return "CustomerDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", seniorCitizen=" + seniorCitizen +
                ", hasMembershipCard=" + hasMembershipCard +
                ", cardNumber='" + cardNumber + '\'' +
                ", points=" + points +
                ", cardExpiryDate=" + cardExpiryDate +
                ", cardExpired=" + cardExpired +
                '}';
    }
}
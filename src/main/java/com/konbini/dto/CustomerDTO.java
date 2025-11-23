package com.konbini.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;

/**
 * Data Transfer Object (DTO) for representing customer information.
 * Used to transfer customer data between layers without exposing the domain model.
 * Includes customer details and membership card information.
 */
public class CustomerDTO {
    private String id;
    private String name;
    private boolean seniorCitizen;
    private boolean hasMembershipCard;
    private String cardNumber;
    private int points;
    private LocalDate cardExpiryDate;
    private boolean cardExpired;

    /**
     * Default constructor for creating an empty CustomerDTO.
     */
    public CustomerDTO() {
    }

    /**
     * Constructs a CustomerDTO from a Customer domain model object.
     * Extracts customer information and membership card details if available.
     *
     * @param customer the Customer domain model to convert to DTO
     * @throws IllegalArgumentException if customer is null
     */
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

    /**
     * Static factory method to create a CustomerDTO from a Customer domain model.
     *
     * @param customer the Customer domain model to convert
     * @return a new CustomerDTO instance representing the customer
     */
    public static CustomerDTO fromModel(Customer customer) {
        return new CustomerDTO(customer);
    }

    /**
     * Converts a list of Customer domain models to a list of CustomerDTOs.
     * Safely handles null lists and null customer objects.
     *
     * @param customers the list of Customer domain models to convert
     * @return a list of CustomerDTO objects, empty if input is null
     */
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

    /**
     * Converts this DTO back to a Customer domain model.
     * Creates a new Customer instance with the DTO's data, including membership card if applicable.
     *
     * @return a new Customer domain model with the DTO's data
     */
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

    /**
     * Gets the customer's unique identifier.
     *
     * @return the customer ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the customer's unique identifier.
     *
     * @param id the customer ID to set
     */
    public void setId(String id) {
        this.id = id;
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
     * @param name the customer name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if the customer is a senior citizen.
     *
     * @return true if the customer is a senior citizen, false otherwise
     */
    public boolean isSeniorCitizen() {
        return seniorCitizen;
    }

    /**
     * Sets whether the customer is a senior citizen.
     *
     * @param seniorCitizen true if senior citizen, false otherwise
     */
    public void setSeniorCitizen(boolean seniorCitizen) {
        this.seniorCitizen = seniorCitizen;
    }

    /**
     * Checks if the customer has a membership card.
     *
     * @return true if the customer has a membership card, false otherwise
     */
    public boolean isHasMembershipCard() {
        return hasMembershipCard;
    }

    /**
     * Sets whether the customer has a membership card.
     *
     * @param hasMembershipCard true if has membership card, false otherwise
     */
    public void setHasMembershipCard(boolean hasMembershipCard) {
        this.hasMembershipCard = hasMembershipCard;
    }

    /**
     * Gets the membership card number.
     *
     * @return the card number, or null if no membership card
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the membership card number.
     *
     * @param cardNumber the card number to set
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the loyalty points on the membership card.
     *
     * @return the number of loyalty points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets the loyalty points on the membership card.
     *
     * @param points the number of loyalty points to set
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Gets the membership card expiry date.
     *
     * @return the card expiry date, or null if no membership card
     */
    public LocalDate getCardExpiryDate() {
        return cardExpiryDate;
    }

    /**
     * Sets the membership card expiry date.
     *
     * @param cardExpiryDate the card expiry date to set
     */
    public void setCardExpiryDate(LocalDate cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    /**
     * Checks if the membership card has expired.
     *
     * @return true if the card has expired, false otherwise
     */
    public boolean isCardExpired() {
        return cardExpired;
    }

    /**
     * Sets whether the membership card has expired.
     *
     * @param cardExpired true if card expired, false otherwise
     */
    public void setCardExpired(boolean cardExpired) {
        this.cardExpired = cardExpired;
    }

    /**
     * Gets the membership card ID (card number) if available.
     * Convenience method that returns the card number when the customer has a membership card.
     *
     * @return the card number if available, null otherwise
     */
    public String getCardId() {
        String temp = null;

        if (hasMembershipCard && cardNumber != null) {
            temp = cardNumber;
        }

        return temp;
    }

    /**
     * Returns a string representation of the CustomerDTO.
     *
     * @return a string containing all customer information
     */
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
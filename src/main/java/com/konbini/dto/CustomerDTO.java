package com.konbini.dto;

import com.konbini.model.Customer;
import com.konbini.model.MembershipCard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Customer.
 * This class is used to serialize or transfer the essential details of a customer
 * between application layers (e.g., to the View or persistence layer) without
 * exposing the full complexity of the domain model. It flattens the details
 * of the associated MembershipCard into its own properties.
 */
public class CustomerDTO {
    /**
     * The unique identifier of the customer.
     */
    private String id;
    /**
     * The full name of the customer.
     */
    private String name;
    /**
     * Flag indicating if the customer is a senior citizen.
     */
    private boolean seniorCitizen;
    /**
     * Flag indicating if the customer has an active membership card.
     */
    private boolean hasMembershipCard;

    // Membership card details
    /**
     * The unique card number of the customer's membership card.
     */
    private String cardNumber;
    /**
     * The current loyalty points balance.
     */
    private int points;
    /**
     * The expiration date of the membership card.
     */
    private LocalDate cardExpiryDate;
    /**
     * Flag indicating if the membership card is expired based on the current date.
     */
    private boolean cardExpired;

    /**
     * Empty constructor for serialization purposes (e.g., JSON mappers).
     */
    public CustomerDTO() {
    }

    /**
     * Constructor that creates a CustomerDTO by copying data from the domain Customer model.
     * It handles the logic for extracting and flattening MembershipCard details.
     *
     * @param customer The domain model Customer object to convert.
     */
    public CustomerDTO(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.seniorCitizen = customer.isSeniorCitizen();
        this.hasMembershipCard = customer.hasMembershipCard();

        if (hasMembershipCard) {
            MembershipCard card = customer.getMembershipCard();
            this.cardNumber = card.getCardNumber();
            this.points = card.getPoints();
            this.cardExpiryDate = card.getExpiryDate();
            this.cardExpired = card.isExpired();
        }
    }

    /**
     * Static factory method to convert a domain Customer object to a CustomerDTO.
     *
     * @param customer The domain model Customer object.
     * @return A new CustomerDTO instance.
     */
    public static CustomerDTO fromModel(Customer customer) {
        return new CustomerDTO(customer);
    }

    /**
     * Static utility method to convert a list of domain Customer objects to a
     * list of CustomerDTOs.
     *
     * @param customers A List of domain model Customer objects.
     * @return A List of CustomerDTO instances.
     */
    public static List<CustomerDTO> fromModelList(List<Customer> customers) {
        List<CustomerDTO> dtos = new ArrayList<>();
        for (Customer customer : customers) {
            dtos.add(fromModel(customer));
        }
        return dtos;
    }

    /**
     * Converts the DTO back into a domain Customer model, recreating the
     * MembershipCard if the relevant details are present.
     * Note: This method does not set the original ID of the customer.
     *
     * @return A new Customer domain object.
     */
    public Customer toModel() {
        // ID is often handled by the service/persistence layer upon creation
        Customer customer = new Customer(name, seniorCitizen);

        if (hasMembershipCard && cardNumber != null && cardExpiryDate != null) {
            MembershipCard card = new MembershipCard(cardNumber, cardExpiryDate);
            if (points > 0) {
                // Add points to the new card
                card.addPoints(points);
            }
            customer.setMembershipCard(card);
        }

        return customer;
    }

    // Getters and Setters

    /**
     * Gets the customer's unique ID.
     * @return The customer ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the customer's unique ID.
     * @param id The ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the customer's name.
     * @return The customer name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's name.
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if the customer is a senior citizen.
     * @return True if the customer is a senior citizen, false otherwise.
     */
    public boolean isSeniorCitizen() {
        return seniorCitizen;
    }

    /**
     * Sets the senior citizen status.
     * @param seniorCitizen The status to set.
     */
    public void setSeniorCitizen(boolean seniorCitizen) {
        this.seniorCitizen = seniorCitizen;
    }

    /**
     * Checks if the customer has a membership card.
     * @return True if the customer has a membership card, false otherwise.
     */
    public boolean isHasMembershipCard() {
        return hasMembershipCard;
    }

    /**
     * Sets the membership card status.
     * @param hasMembershipCard The status to set.
     */
    public void setHasMembershipCard(boolean hasMembershipCard) {
        this.hasMembershipCard = hasMembershipCard;
    }

    /**
     * Gets the membership card number.
     * @return The card number.
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the membership card number.
     * @param cardNumber The card number to set.
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Gets the current loyalty points balance.
     * @return The points balance.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets the loyalty points balance.
     * @param points The points to set.
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Gets the card expiration date.
     * @return The expiration date.
     */
    public LocalDate getCardExpiryDate() {
        return cardExpiryDate;
    }

    /**
     * Sets the card expiration date.
     * @param cardExpiryDate The expiration date to set.
     */
    public void setCardExpiryDate(LocalDate cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    /**
     * Checks if the card is expired.
     * @return True if the card is expired, false otherwise.
     */
    public boolean isCardExpired() {
        return cardExpired;
    }

    /**
     * Sets the card expired status.
     * @param cardExpired The status to set.
     */
    public void setCardExpired(boolean cardExpired) {
        this.cardExpired = cardExpired;
    }

    /**
     * Provides a string representation of the CustomerDTO for logging and debugging.
     *
     * @return A string containing key customer and card summary data.
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

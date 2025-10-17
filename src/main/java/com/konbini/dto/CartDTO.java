package com.konbini.dto;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Cart.
 * This class is used to serialize or transfer the essential details of a shopping
 * cart and its associated customer, often for display, logging, or passing data
 * between application layers (e.g., to the View or to a persistence layer).
 * It flattens customer details into the cart structure.
 */
public class CartDTO {
    /**
     * The unique identifier of the customer associated with the cart.
     */
    private String customerId;
    /**
     * The name of the customer associated with the cart.
     */
    private String customerName;
    /**
     * Flag indicating if the customer is a senior citizen.
     */
    private boolean customerIsSeniorCitizen;
    /**
     * Flag indicating if the customer has a loyalty membership card.
     */
    private boolean customerHasMembershipCard;
    /**
     * The current loyalty points balance of the customer.
     */
    private int customerPoints;
    /**
     * A list of items in the cart, represented by their simplified DTOs.
     */
    private List<TransactionItemDTO> items = new ArrayList<>();
    /**
     * The total price of items in the cart before any discounts or taxes.
     */
    private double subtotal;
    /**
     * The total count of distinct items in the cart.
     */
    private int totalItems;

    /**
     * Empty constructor for serialization purposes.
     */
    public CartDTO() {
    }

    /**
     * Constructor that creates a CartDTO by copying data from the domain Cart model.
     * This method is responsible for mapping complex domain objects to a simple DTO structure.
     *
     * @param cart The domain model Cart object to convert.
     */
    public CartDTO(Cart cart) {
        Customer customer = cart.getCustomer();
        this.customerId = customer.getId();
        this.customerName = customer.getName();
        this.customerIsSeniorCitizen = customer.isSeniorCitizen();
        this.customerHasMembershipCard = customer.hasMembershipCard();

        if (customerHasMembershipCard) {
            // Safely retrieve points only if a card exists
            this.customerPoints = customer.getMembershipCard().getPoints();
        }

        for (CartItem item : cart.getItems()) {
            // Convert CartItem to its corresponding DTO
            this.items.add(new TransactionItemDTO(item));
        }

        this.subtotal = cart.getSubtotal();
        this.totalItems = cart.getTotalItems();
    }

    /**
     * Static factory method to convert a domain Cart object to a CartDTO.
     *
     * @param cart The domain model Cart object.
     * @return A new CartDTO instance.
     */
    public static CartDTO fromModel(Cart cart) {
        return new CartDTO(cart);
    }

    // Getters and Setters

    /**
     * Gets the customer ID.
     * @return The customer ID.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID.
     * @param customerId The customer ID to set.
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the customer name.
     * @return The customer name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer name.
     * @param customerName The customer name to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Checks if the customer is a senior citizen.
     * @return True if the customer is a senior citizen, false otherwise.
     */
    public boolean isCustomerIsSeniorCitizen() {
        return customerIsSeniorCitizen;
    }

    /**
     * Sets the senior citizen status.
     * @param customerIsSeniorCitizen The status to set.
     */
    public void setCustomerIsSeniorCitizen(boolean customerIsSeniorCitizen) {
        this.customerIsSeniorCitizen = customerIsSeniorCitizen;
    }

    /**
     * Checks if the customer has a membership card.
     * @return True if the customer has a membership card, false otherwise.
     */
    public boolean isCustomerHasMembershipCard() {
        return customerHasMembershipCard;
    }

    /**
     * Sets the membership card status.
     * @param customerHasMembershipCard The status to set.
     */
    public void setCustomerHasMembershipCard
        (boolean customerHasMembershipCard) {
        this.customerHasMembershipCard = customerHasMembershipCard;
    }

    /**
     * Gets the customer's current loyalty points.
     * @return The customer's loyalty points.
     */
    public int getCustomerPoints() {
        return customerPoints;
    }

    /**
     * Sets the customer's loyalty points.
     * @param customerPoints The loyalty points to set.
     */
    public void setCustomerPoints(int customerPoints) {
        this.customerPoints = customerPoints;
    }

    /**
     * Gets the list of TransactionItemDTOs in the cart.
     * @return The list of cart items (DTOs).
     */
    public List<TransactionItemDTO> getItems() {
        return items;
    }

    /**
     * Sets the list of items.
     * @param items The list of TransactionItemDTOs to set.
     */
    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }

    /**
     * Gets the subtotal of the cart.
     * @return The subtotal amount.
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal of the cart.
     * @param subtotal The subtotal amount to set.
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Gets the total number of distinct items in the cart.
     * @return The total item count.
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * Sets the total number of distinct items in the cart.
     * @param totalItems The total item count to set.
     */
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Provides a string representation of the CartDTO for logging and debugging.
     *
     * @return A string containing key cart and customer summary data.
     */
    @Override
    public String toString() {
        return "CartDTO{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", items=" + items.size() +
                ", subtotal=" + subtotal +
                ", totalItems=" + totalItems +
                '}';
    }
}

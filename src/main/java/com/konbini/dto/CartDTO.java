package com.konbini.dto;

import java.util.ArrayList;
import java.util.List;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Customer;

/**
 * Data Transfer Object (DTO) for representing shopping cart information.
 * Used to transfer cart data between layers without exposing the domain model.
 */
public class CartDTO {
    private String customerId;
    private String customerName;
    private boolean customerIsSeniorCitizen;
    private boolean customerHasMembershipCard;
    private int customerPoints;
    private List<TransactionItemDTO> items = new ArrayList<>();
    private double subtotal;
    private int totalItems;

    /**
     * Default constructor for creating an empty CartDTO.
     */
    public CartDTO() {
    }

    /**
     * Constructs a CartDTO from a Cart domain model object.
     * Extracts customer information and cart items for data transfer.
     *
     * @param cart the Cart domain model to convert to DTO
     * @throws IllegalArgumentException if cart or cart customer is null
     */
    public CartDTO(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        Customer customer = cart.getCustomer();
        if (customer == null) {
            throw new IllegalArgumentException("Cart customer cannot be null");
        }

        this.customerId = customer.getId();
        this.customerName = customer.getName();
        this.customerIsSeniorCitizen = customer.isSeniorCitizen();
        this.customerHasMembershipCard = customer.hasMembershipCard();

        if (customerHasMembershipCard && customer.getMembershipCard() != null) {
            this.customerPoints = customer.getMembershipCard().getPoints();
        }

        for (CartItem item : cart.getItems()) {
            this.items.add(new TransactionItemDTO(item));
        }

        this.subtotal = cart.getSubtotal();
        this.totalItems = cart.getTotalItems();
    }

    /**
     * Static factory method to create a CartDTO from a Cart domain model.
     *
     * @param cart the Cart domain model to convert
     * @return a new CartDTO instance representing the cart
     */
    public static CartDTO fromModel(Cart cart) {
        return new CartDTO(cart);
    }

    /**
     * Gets the customer ID associated with this cart.
     *
     * @return the customer ID
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID for this cart.
     *
     * @param customerId the customer ID to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the customer name associated with this cart.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer name for this cart.
     *
     * @param customerName the customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Checks if the customer associated with this cart is a senior citizen.
     *
     * @return true if the customer is a senior citizen, false otherwise
     */
    public boolean isCustomerIsSeniorCitizen() {
        return customerIsSeniorCitizen;
    }

    /**
     * Sets whether the customer associated with this cart is a senior citizen.
     *
     * @param customerIsSeniorCitizen true if senior citizen, false otherwise
     */
    public void setCustomerIsSeniorCitizen(boolean customerIsSeniorCitizen) {
        this.customerIsSeniorCitizen = customerIsSeniorCitizen;
    }

    /**
     * Checks if the customer associated with this cart has a membership card.
     *
     * @return true if the customer has a membership card, false otherwise
     */
    public boolean isCustomerHasMembershipCard() {
        return customerHasMembershipCard;
    }

    /**
     * Sets whether the customer associated with this cart has a membership card.
     *
     * @param customerHasMembershipCard true if has membership card, false otherwise
     */
    public void setCustomerHasMembershipCard(boolean customerHasMembershipCard) {
        this.customerHasMembershipCard = customerHasMembershipCard;
    }

    /**
     * Gets the loyalty points available to the customer.
     * Only applicable if the customer has a membership card.
     *
     * @return the number of customer loyalty points
     */
    public int getCustomerPoints() {
        return customerPoints;
    }

    /**
     * Sets the loyalty points for the customer.
     *
     * @param customerPoints the number of loyalty points to set
     */
    public void setCustomerPoints(int customerPoints) {
        this.customerPoints = customerPoints;
    }

    /**
     * Gets the list of items in the cart.
     *
     * @return a list of TransactionItemDTO objects representing cart items
     */
    public List<TransactionItemDTO> getItems() {
        return items;
    }

    /**
     * Sets the list of items in the cart.
     * If null is provided, an empty list will be set instead.
     *
     * @param items the list of TransactionItemDTO objects to set
     */
    public void setItems(List<TransactionItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    /**
     * Gets the subtotal of all items in the cart before discounts.
     *
     * @return the cart subtotal amount
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal for the cart.
     *
     * @param subtotal the subtotal amount to set
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Gets the total number of items in the cart (sum of quantities).
     *
     * @return the total number of items
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * Sets the total number of items in the cart.
     *
     * @param totalItems the total number of items to set
     */
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Returns a string representation of the CartDTO.
     *
     * @return a string containing cart summary information
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
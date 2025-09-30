package com.konbini.dto;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Cart
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
    
    // Empty constructor for serialization
    public CartDTO() {
    }
    
    // Constructor from domain model
    public CartDTO(Cart cart) {
        Customer customer = cart.getCustomer();
        this.customerId = customer.getId();
        this.customerName = customer.getName();
        this.customerIsSeniorCitizen = customer.isSeniorCitizen();
        this.customerHasMembershipCard = customer.hasMembershipCard();
        
        if (customerHasMembershipCard) {
            this.customerPoints = customer.getMembershipCard().getPoints();
        }
        
        for (CartItem item : cart.getItems()) {
            this.items.add(new TransactionItemDTO(item));
        }
        
        this.subtotal = cart.getSubtotal();
        this.totalItems = cart.getTotalItems();
    }
    
    // Convert domain model to DTO
    public static CartDTO fromModel(Cart cart) {
        return new CartDTO(cart);
    }
    
    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isCustomerIsSeniorCitizen() {
        return customerIsSeniorCitizen;
    }

    public void setCustomerIsSeniorCitizen(boolean customerIsSeniorCitizen) {
        this.customerIsSeniorCitizen = customerIsSeniorCitizen;
    }

    public boolean isCustomerHasMembershipCard() {
        return customerHasMembershipCard;
    }

    public void setCustomerHasMembershipCard
        (boolean customerHasMembershipCard) {
        this.customerHasMembershipCard = customerHasMembershipCard;
    }

    public int getCustomerPoints() {
        return customerPoints;
    }

    public void setCustomerPoints(int customerPoints) {
        this.customerPoints = customerPoints;
    }

    public List<TransactionItemDTO> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
    
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

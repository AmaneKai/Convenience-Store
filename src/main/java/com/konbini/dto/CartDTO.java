package com.konbini.dto;

import java.util.ArrayList;
import java.util.List;

import com.konbini.model.Cart;
import com.konbini.model.CartItem;
import com.konbini.model.Customer;

public class CartDTO {
    private String customerId;
    private String customerName;
    private boolean customerIsSeniorCitizen;
    private boolean customerHasMembershipCard;
    private int customerPoints;
    private List<TransactionItemDTO> items = new ArrayList<>();
    private double subtotal;
    private int totalItems;

    public CartDTO() {
    }

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

    public static CartDTO fromModel(Cart cart) {
        return new CartDTO(cart);
    }

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

    public void setCustomerHasMembershipCard(boolean customerHasMembershipCard) {
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
        this.items = items != null ? items : new ArrayList<>();
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
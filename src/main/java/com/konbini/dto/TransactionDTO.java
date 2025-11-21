package com.konbini.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.konbini.model.CartItem;
import com.konbini.model.Customer;
import com.konbini.model.Transaction;

public class TransactionDTO {
    private String id;
    private String customerId;
    private String customerName;
    private boolean customerIsSeniorCitizen;
    private boolean customerHasMembershipCard;
    private LocalDateTime timestamp;
    private double subtotal;
    private double tax;
    private double discount;
    private double total;
    private double amountPaid;
    private double change;
    private int pointsEarned;
    private int pointsRedeemed;
    private List<TransactionItemDTO> items = new ArrayList<>();

    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();

        Customer customer = transaction.getCustomer();
        this.customerId = customer.getId();
        this.customerName = customer.getName();
        this.customerIsSeniorCitizen = customer.isSeniorCitizen();
        this.customerHasMembershipCard = customer.hasMembershipCard();

        this.timestamp = transaction.getTimestamp();
        this.subtotal = transaction.getSubtotal();
        this.tax = transaction.getTax();
        this.discount = transaction.getDiscount();
        this.total = transaction.getTotal();
        this.amountPaid = transaction.getAmountPaid();
        this.change = transaction.getChange();
        this.pointsEarned = transaction.getPointsEarned();
        this.pointsRedeemed = transaction.getPointsRedeemed();

        for (CartItem item : transaction.getItems()) {
            this.items.add(new TransactionItemDTO(item));
        }
    }

    public static TransactionDTO fromModel(Transaction transaction) {
        return new TransactionDTO(transaction);
    }

    public static List<TransactionDTO> fromModelList(List<Transaction> transactions) {
        List<TransactionDTO> dtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            dtos.add(fromModel(transaction));
        }
        return dtos;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    public double getTax() {
        return tax;
    }
    public void setTax(double tax) {
        this.tax = tax;
    }
    public double getDiscount() {
        return discount;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public double getAmountPaid() {
        return amountPaid;
    }
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }
    public double getChange() {
        return change;
    }
    public void setChange(double change) {
        this.change = change;
    }
    public int getPointsEarned() {
        return pointsEarned;
    }
    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    public int getPointsRedeemed() {
        return pointsRedeemed;
    }
    public void setPointsRedeemed(int pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }
    public List<TransactionItemDTO> getItems() {
        return items;
    }
    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }
    public java.time.LocalDate getDate() {
        return timestamp != null ? timestamp.toLocalDate() : null;
    }
    public java.time.LocalDateTime getDatetime() {
        return timestamp;
    }
    public double getPaymentAmount() {
        return amountPaid;
    }
    public double getTotalAmount() { return total; }
    @Override
    public String toString() {
        return "TransactionDTO{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", timestamp=" + timestamp +
                ", subtotal=" + subtotal +
                ", tax=" + tax +
                ", discount=" + discount +
                ", total=" + total +
                ", amountPaid=" + amountPaid +
                ", change=" + change +
                ", pointsEarned=" + pointsEarned +
                ", pointsRedeemed=" + pointsRedeemed +
                ", items=" + items.size() +
                '}';
    }
}

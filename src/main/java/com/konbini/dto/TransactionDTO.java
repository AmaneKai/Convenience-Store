package com.konbini.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.konbini.model.CartItem;
import com.konbini.model.Customer;
import com.konbini.model.Transaction;

/**
 * Data Transfer Object (DTO) for representing transaction information.
 * Used to transfer transaction data between layers without exposing the domain model.
 * Contains complete transaction details including customer information, financial breakdown,
 * loyalty points, and purchased items.
 */
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

    /**
     * Default constructor for creating an empty TransactionDTO.
     */
    public TransactionDTO() {
    }

    /**
     * Constructs a TransactionDTO from a Transaction domain model object.
     * Extracts complete transaction details including customer information and purchased items.
     *
     * @param transaction the Transaction domain model to convert to DTO
     */
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

    /**
     * Static factory method to create a TransactionDTO from a Transaction domain model.
     *
     * @param transaction the Transaction domain model to convert
     * @return a new TransactionDTO instance representing the transaction
     */
    public static TransactionDTO fromModel(Transaction transaction) {
        return new TransactionDTO(transaction);
    }

    /**
     * Converts a list of Transaction domain models to a list of TransactionDTOs.
     *
     * @param transactions the list of Transaction domain models to convert
     * @return a list of TransactionDTO objects
     */
    public static List<TransactionDTO> fromModelList(List<Transaction> transactions) {
        List<TransactionDTO> dtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            dtos.add(fromModel(transaction));
        }
        return dtos;
    }

    // Getters and Setters

    /**
     * Gets the transaction's unique identifier.
     *
     * @return the transaction ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the transaction's unique identifier.
     *
     * @param id the transaction ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the customer ID associated with this transaction.
     *
     * @return the customer ID
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID for this transaction.
     *
     * @param customerId the customer ID to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the customer name associated with this transaction.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer name for this transaction.
     *
     * @param customerName the customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Checks if the customer associated with this transaction is a senior citizen.
     *
     * @return true if the customer is a senior citizen, false otherwise
     */
    public boolean isCustomerIsSeniorCitizen() {
        return customerIsSeniorCitizen;
    }

    /**
     * Sets whether the customer associated with this transaction is a senior citizen.
     *
     * @param customerIsSeniorCitizen true if senior citizen, false otherwise
     */
    public void setCustomerIsSeniorCitizen(boolean customerIsSeniorCitizen) {
        this.customerIsSeniorCitizen = customerIsSeniorCitizen;
    }

    /**
     * Checks if the customer associated with this transaction has a membership card.
     *
     * @return true if the customer has a membership card, false otherwise
     */
    public boolean isCustomerHasMembershipCard() {
        return customerHasMembershipCard;
    }

    /**
     * Sets whether the customer associated with this transaction has a membership card.
     *
     * @param customerHasMembershipCard true if has membership card, false otherwise
     */
    public void setCustomerHasMembershipCard(boolean customerHasMembershipCard) {
        this.customerHasMembershipCard = customerHasMembershipCard;
    }

    /**
     * Gets the date and time when the transaction occurred.
     *
     * @return the transaction timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the date and time when the transaction occurred.
     *
     * @param timestamp the transaction timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the subtotal amount before tax and discounts.
     *
     * @return the subtotal amount
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal amount before tax and discounts.
     *
     * @param subtotal the subtotal amount to set
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Gets the tax amount applied to the transaction.
     *
     * @return the tax amount
     */
    public double getTax() {
        return tax;
    }

    /**
     * Sets the tax amount applied to the transaction.
     *
     * @param tax the tax amount to set
     */
    public void setTax(double tax) {
        this.tax = tax;
    }

    /**
     * Gets the discount amount applied to the transaction.
     *
     * @return the discount amount
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Sets the discount amount applied to the transaction.
     *
     * @param discount the discount amount to set
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * Gets the total amount after tax and discounts.
     *
     * @return the total amount
     */
    public double getTotal() {
        return total;
    }

    /**
     * Sets the total amount after tax and discounts.
     *
     * @param total the total amount to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Gets the amount paid by the customer.
     *
     * @return the amount paid
     */
    public double getAmountPaid() {
        return amountPaid;
    }

    /**
     * Sets the amount paid by the customer.
     *
     * @param amountPaid the amount paid to set
     */
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    /**
     * Gets the change returned to the customer.
     *
     * @return the change amount
     */
    public double getChange() {
        return change;
    }

    /**
     * Sets the change returned to the customer.
     *
     * @param change the change amount to set
     */
    public void setChange(double change) {
        this.change = change;
    }

    /**
     * Gets the loyalty points earned from this transaction.
     *
     * @return the points earned
     */
    public int getPointsEarned() {
        return pointsEarned;
    }

    /**
     * Sets the loyalty points earned from this transaction.
     *
     * @param pointsEarned the points earned to set
     */
    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    /**
     * Gets the loyalty points redeemed in this transaction.
     *
     * @return the points redeemed
     */
    public int getPointsRedeemed() {
        return pointsRedeemed;
    }

    /**
     * Sets the loyalty points redeemed in this transaction.
     *
     * @param pointsRedeemed the points redeemed to set
     */
    public void setPointsRedeemed(int pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }

    /**
     * Gets the list of items purchased in this transaction.
     *
     * @return a list of TransactionItemDTO objects representing purchased items
     */
    public List<TransactionItemDTO> getItems() {
        return items;
    }

    /**
     * Sets the list of items purchased in this transaction.
     *
     * @param items the list of TransactionItemDTO objects to set
     */
    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }

    /**
     * Gets the date portion of the transaction timestamp.
     *
     * @return the transaction date, or null if timestamp is not set
     */
    public java.time.LocalDate getDate() {
        return timestamp != null ? timestamp.toLocalDate() : null;
    }

    /**
     * Gets the full date and time of the transaction.
     *
     * @return the transaction datetime
     */
    public java.time.LocalDateTime getDatetime() {
        return timestamp;
    }

    /**
     * Gets the payment amount (alias for getAmountPaid).
     *
     * @return the payment amount
     */
    public double getPaymentAmount() {
        return amountPaid;
    }

    /**
     * Gets the total amount (alias for getTotal).
     *
     * @return the total amount
     */
    public double getTotalAmount() {
        return total;
    }

    /**
     * Returns a string representation of the TransactionDTO.
     *
     * @return a string containing transaction summary information
     */
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
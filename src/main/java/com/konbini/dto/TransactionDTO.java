package com.konbini.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.konbini.model.CartItem;
import com.konbini.model.Customer;
import com.konbini.model.PaymentMethod;
import com.konbini.model.Transaction;

/**
 * Data Transfer Object for Transaction.
 * This class is used to serialize or transfer the complete record of a sale
 * between application layers (e.g., to the View, API, or persistence layer).
 * It flattens key customer and financial data for a simple, comprehensive record.
 */
public class TransactionDTO {
    /**
     * The unique identifier of the transaction.
     */
    private String id;
    /**
     * The unique identifier of the customer who made the purchase.
     */
    private String customerId;
    /**
     * The name of the customer.
     */
    private String customerName;
    /**
     * Flag indicating if the customer was registered as a senior citizen for this transaction.
     */
    private boolean customerIsSeniorCitizen;
    /**
     * Flag indicating if the customer used a membership card for this transaction.
     */
    private boolean customerHasMembershipCard;
    /**
     * The date and time the transaction was completed.
     */
    private LocalDateTime timestamp;
    /**
     * The total price of all items before tax and discount.
     */
    private double subtotal;
    /**
     * The amount of tax applied to the transaction.
     */
    private double tax;
    /**
     * The total amount of discount applied.
     */
    private double discount;
    /**
     * The final price due after all calculations (subtotal + tax - discount).
     */
    private double total;
    /**
     * The amount of money the customer paid.
     */
    private double amountPaid;
    /**
     * The change given back to the customer (amountPaid - total).
     */
    private double change;
    /**
     * The number of loyalty points earned by the customer in this transaction.
     */
    private int pointsEarned;
    /**
     * The number of loyalty points redeemed by the customer in this transaction.
     */
    private int pointsRedeemed;

    /**
     * A list of items purchased in this transaction, represented by their DTOs.
     */
    private List<TransactionItemDTO> items = new ArrayList<>();

    private PaymentMethod paymentMethod;

    /**
     * Empty constructor for serialization purposes (e.g., JSON mappers).
     */
    public TransactionDTO() {
    }

    /**
     * Constructor that creates a TransactionDTO by copying data from the domain Transaction model.
     * It maps customer and item details into a flattened DTO structure.
     *
     * @param transaction The domain model Transaction object to convert.
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
        this.paymentMethod = transaction.getPaymentMethod();

        for (CartItem item : transaction.getItems()) {
            // Convert CartItem to its corresponding DTO
            this.items.add(new TransactionItemDTO(item));
        }
    }

    /**
     * Static factory method to convert a domain Transaction object to a TransactionDTO.
     *
     * @param transaction The domain model Transaction object.
     * @return A new TransactionDTO instance.
     */
    public static TransactionDTO fromModel(Transaction transaction) {
        return new TransactionDTO(transaction);
    }

    /**
     * Static utility method to convert a list of domain Transaction objects to a
     * list of TransactionDTOs.
     *
     * @param transactions A List of domain model Transaction objects.
     * @return A List of TransactionDTO instances.
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
     * Gets the unique transaction ID.
     * @return The transaction ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique transaction ID.
     * @param id The transaction ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of the customer.
     * @return The customer ID.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the ID of the customer.
     * @param customerId The customer ID to set.
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the name of the customer.
     * @return The customer name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the name of the customer.
     * @param customerName The customer name to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Checks if the customer was a senior citizen.
     * @return True if senior citizen status was applied, false otherwise.
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
     * Checks if the customer had a membership card.
     * @return True if a membership card was used, false otherwise.
     */
    public boolean isCustomerHasMembershipCard() {
        return customerHasMembershipCard;
    }

    /**
     * Sets the membership card status.
     * @param customerHasMembershipCard The status to set.
     */
    public void setCustomerHasMembershipCard(boolean customerHasMembershipCard) {
        this.customerHasMembershipCard = customerHasMembershipCard;
    }

    /**
     * Gets the timestamp of the transaction.
     * @return The transaction timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the transaction.
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the subtotal (pre-tax, pre-discount).
     * @return The subtotal.
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal.
     * @param subtotal The subtotal to set.
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Gets the tax amount.
     * @return The tax amount.
     */
    public double getTax() {
        return tax;
    }

    /**
     * Sets the tax amount.
     * @param tax The tax amount to set.
     */
    public void setTax(double tax) {
        this.tax = tax;
    }

    /**
     * Gets the total discount amount.
     * @return The discount amount.
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * Sets the total discount amount.
     * @param discount The discount amount to set.
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * Gets the final total amount due.
     * @return The final total.
     */
    public double getTotal() {
        return total;
    }

    /**
     * Sets the final total amount due.
     * @param total The total amount to set.
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Gets the amount paid by the customer.
     * @return The amount paid.
     */
    public double getAmountPaid() {
        return amountPaid;
    }

    /**
     * Sets the amount paid by the customer.
     * @param amountPaid The amount paid to set.
     */
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    /**
     * Gets the change given to the customer.
     * @return The change amount.
     */

    public double getChange() {
        return change;
    }

    /**
     * Sets the change given to the customer.
     * @param change The change amount to set.
     */
    public void setChange(double change) {
        this.change = change;
    }

    /**
     * Gets the loyalty points earned.
     * @return The points earned.
     */
    public int getPointsEarned() {
        return pointsEarned;
    }

    /**
     * Sets the loyalty points earned.
     * @param pointsEarned The points earned to set.
     */
    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    /**
     * Gets the loyalty points redeemed.
     * @return The points redeemed.
     */
    public int getPointsRedeemed() {
        return pointsRedeemed;
    }

    /**
     * Sets the loyalty points redeemed.
     * @param pointsRedeemed The points redeemed to set.
     */
    public void setPointsRedeemed(int pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }

    /**
     * Gets the list of items purchased in this transaction.
     * @return The list of TransactionItemDTOs.
     */
    public List<TransactionItemDTO> getItems() {
        return items;
    }

    /**
     * Sets the list of items purchased.
     * @param items The list of TransactionItemDTOs to set.
     */
    public void setItems(List<TransactionItemDTO> items) {
        this.items = items;
    }

    /**
     * Gets the payment method used for the transaction.
     * 
     * @return The PaymentMethod used in this transaction.
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
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
    public double getTotalAmount() {
        return total;
    }
    /**
     * Provides a string representation of the TransactionDTO for logging and debugging.
     *
     * @return A string containing key transaction summary data.
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

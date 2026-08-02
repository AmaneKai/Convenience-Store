package com.konbini.domain.transaction;

import com.konbini.domain.customer.Customer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Immutable value object representing a completed sales transaction.
 * All financial figures are computed before construction; the object carries
 * no behavior that mutates inventory or loyalty points.
 */
@Getter
public class Transaction {

    private final String id;
    private final Customer customer;
    private final List<CartItem> items;
    private final LocalDateTime timestamp;
    private final BigDecimal subtotal;
    private final BigDecimal tax;
    private final BigDecimal discount;
    private final BigDecimal total;
    private final BigDecimal amountPaid;
    private final BigDecimal change;
    private final int pointsEarned;
    private final int pointsRedeemed;
    private final List<String> appliedDiscounts;
    private final String taxName;

    /**
     * Constructs a completed transaction.
     *
     * @param id the transaction identifier
     * @param customer the purchasing customer
     * @param items the purchased items
     * @param timestamp the completion timestamp
     * @param subtotal the pre-tax, pre-discount total
     * @param tax the applied tax amount
     * @param discount the applied discount amount
     * @param total the final payable amount
     * @param amountPaid the amount tendered
     * @param change the change returned
     * @param pointsEarned the loyalty points earned
     * @param pointsRedeemed the loyalty points redeemed
     * @param appliedDiscounts the names of applied discounts
     * @param taxName the display name of the applied tax
     */
    @Builder
    public Transaction(String id, Customer customer, List<CartItem> items, LocalDateTime timestamp,
                       BigDecimal subtotal, BigDecimal tax, BigDecimal discount, BigDecimal total,
                       BigDecimal amountPaid, BigDecimal change, int pointsEarned, int pointsRedeemed,
                       List<String> appliedDiscounts, String taxName) {
        this.id = id;
        this.customer = customer;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.total = total;
        this.amountPaid = amountPaid;
        this.change = change;
        this.pointsEarned = pointsEarned;
        this.pointsRedeemed = pointsRedeemed;
        this.appliedDiscounts = appliedDiscounts != null
                ? new ArrayList<>(appliedDiscounts) : new ArrayList<>();
        this.taxName = taxName != null ? taxName : "Tax";
    }

    /**
     * Returns a defensive copy of the purchased items.
     *
     * @return the items
     */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns a defensive copy of the applied discount names.
     *
     * @return the discount names
     */
    public List<String> getAppliedDiscounts() {
        return Collections.unmodifiableList(appliedDiscounts);
    }
}

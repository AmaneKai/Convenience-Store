package com.konbini.model;

/**
 * Represents the available payment methods for transactions in the system.
 * 
 * This enum defines the different ways a customer can pay for their purchase,
 * providing a standardized set of payment options across the application.
 */
public enum PaymentMethod {
    /**
     * Represents payment made using physical currency.
     * Used for transactions where the customer pays with paper money or coins.
     */
    CASH,

    /**
     * Represents payment made using a credit card.
     * Indicates a transaction processed through a credit card payment system,
     * where the payment is borrowed from a credit line.
     */
    CREDIT_CARD,

    /**
     * Represents payment made using a debit card.
     * Indicates a transaction directly linked to the customer's bank account,
     * where funds are immediately withdrawn from the account balance.
     */
    DEBIT_CARD
}
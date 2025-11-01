package com.konbini.service;

import com.konbini.model.PaymentMethod;

/**
 * Service interface for handling payment-related operations.
 * 
 * This interface defines the contract for processing and validating payments
 * across different payment methods, providing methods for payment validation,
 * processing, and card authentication.
 */
public interface PaymentService {
    /**
     * Validates a payment based on the amount and payment method.
     * 
     * Performs initial checks to ensure the payment can be processed,
     * such as verifying the amount is positive and the payment method
     * is supported.
     * 
     * @param amount The total amount to be paid
     * @param method The payment method being used
     * @return {@code true} if the payment is valid and can be processed,
     *         {@code false} otherwise
     */
    boolean validatePayment(double amount, PaymentMethod method);

    /**
     * Processes a payment for the given amount using the specified payment method.
     * 
     * Attempts to complete the payment transaction and returns a 
     * transaction or confirmation identifier.
     * 
     * @param amount The total amount to be paid
     * @param method The payment method being used
     * @return A string representing the payment transaction ID or confirmation code
     *         Returns null or an empty string if the payment fails
     */
    String processPayment(double amount, PaymentMethod method);

    /**
     * Authenticates a card-based payment by verifying card details.
     * 
     * Performs validation of card number, CVV, and expiry date for 
     * credit and debit card payments.
     * 
     * @param cardNumber The card number to authenticate
     * @param cvv The Card Verification Value (CVV)
     * @param expiryDate The card's expiration date
     * @return {@code true} if the card details are valid and authenticated,
     *         {@code false} otherwise
     */
    boolean authenticateCardPayment(String cardNumber, String cvv, 
        String expiryDate);
}
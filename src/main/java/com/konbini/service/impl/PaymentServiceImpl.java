package com.konbini.service.impl;

import com.konbini.model.PaymentMethod;
import com.konbini.service.PaymentService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentServiceImpl implements PaymentService {
    @Override
    public boolean validatePayment(double amount, PaymentMethod method) {
        return amount > 0;
    }

    @Override
    public String processPayment(double amount, PaymentMethod method) {
        return "Payment processed: " + method + " - Amount: " + amount;
    }

    @Override
    public boolean authenticateCardPayment(String cardNumber, String cvv, 
        String expiryDate) {
        return isValidCardNumber(cardNumber) 
               && isValidCVV(cvv) 
               && isCardNotExpired(expiryDate);
    }

    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber != null 
               && cardNumber.replaceAll("\\s", "").length() >= 12 
               && cardNumber.replaceAll("\\s", "").length() <= 19;
    }

    private boolean isValidCVV(String cvv) {
        return cvv != null 
               && cvv.matches("\\d{3,4}");
    }

    private boolean isCardNotExpired(String expiryDate) {
        try {
            LocalDate expiry = LocalDate.parse(expiryDate, DateTimeFormatter
                .ofPattern("MM/yy"));
            return !expiry.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}

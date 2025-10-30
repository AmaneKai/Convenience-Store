package com.konbini.service;

import com.konbini.model.PaymentMethod;

public interface PaymentService {
    boolean validatePayment(double amount, PaymentMethod method);
    String processPayment(double amount, PaymentMethod method);
    boolean authenticateCardPayment(String cardNumber, String cvv, 
        String expiryDate);
}

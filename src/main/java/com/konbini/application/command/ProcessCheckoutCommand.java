package com.konbini.application.command;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Request to process a checkout for a customer.
 * Items is a map of product ID to quantity to keep the record primitive.
 */
public record ProcessCheckoutCommand(String customerId, Map<String, Integer> items,
                                     BigDecimal paymentAmount, int pointsToRedeem) {
}

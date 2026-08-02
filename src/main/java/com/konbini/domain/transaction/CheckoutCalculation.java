package com.konbini.domain.transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Pure financial breakdown of a cart: subtotal, tax, applicable discounts,
 * the resulting total and the loyalty points that would be earned. Carries
 * no reference to inventory or persistence — {@link CheckoutCalculator}
 * never mutates anything to produce it.
 */
public record CheckoutCalculation(BigDecimal subtotal, BigDecimal tax, String taxName,
                                  BigDecimal discount, List<String> appliedDiscounts,
                                  int pointsRedeemed, BigDecimal total, int pointsEarned) {
}

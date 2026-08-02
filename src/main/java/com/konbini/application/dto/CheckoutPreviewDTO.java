package com.konbini.application.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Read-only financial breakdown of a prospective checkout, computed without
 * mutating any store state.
 */
public record CheckoutPreviewDTO(BigDecimal subtotal, BigDecimal tax, String taxName,
                                 BigDecimal discount, List<String> appliedDiscounts,
                                 BigDecimal total, int pointsToEarn) {
}

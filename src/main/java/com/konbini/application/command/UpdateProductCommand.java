package com.konbini.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request to update an existing product's details.
 */
public record UpdateProductCommand(String productId, String name, BigDecimal price, int quantity,
                                   String category, String brand, String variant,
                                   LocalDate expirationDate) {
}

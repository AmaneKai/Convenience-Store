package com.konbini.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request to add a new product to the inventory.
 */
public record AddProductCommand(String name, BigDecimal price, int quantity, String category,
                                String brand, String variant, LocalDate expirationDate) {
}

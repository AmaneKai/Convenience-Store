package com.konbini.application.dto;

import com.konbini.domain.product.Product;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Immutable presentation snapshot of a {@link Product}.
 */
public record ProductDTO(String id, String name, BigDecimal price, int quantity,
                         String category, String brand, String variant,
                         LocalDate expirationDate, boolean lowStock, boolean expired) {

    /**
     * Creates a DTO from a domain product.
     *
     * @param product the domain product
     * @return the DTO snapshot
     */
    public static ProductDTO fromDomain(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getBrand(),
                product.getVariant(),
                product.getExpirationDate(),
                product.isLowStock(),
                product.isExpired());
    }
}

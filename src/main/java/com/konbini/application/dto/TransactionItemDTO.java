package com.konbini.application.dto;

import com.konbini.domain.transaction.CartItem;
import java.math.BigDecimal;
import java.util.List;

/**
 * Immutable presentation snapshot of a single transaction line item.
 */
public record TransactionItemDTO(String productId, String productName, BigDecimal productPrice,
                                 String productCategory, String productBrand,
                                 String productVariant, int quantity, BigDecimal subtotal) {

    /**
     * Creates a DTO from a domain cart item.
     *
     * @param item the domain cart item
     * @return the DTO snapshot
     */
    public static TransactionItemDTO fromDomain(CartItem item) {
        return new TransactionItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getProduct().getCategory(),
                item.getProduct().getBrand(),
                item.getProduct().getVariant(),
                item.getQuantity(),
                item.getSubtotal());
    }

    /**
     * Maps a list of domain items to DTOs.
     *
     * @param items the domain items
     * @return the DTO list
     */
    public static List<TransactionItemDTO> fromDomainList(List<CartItem> items) {
        return items.stream().map(TransactionItemDTO::fromDomain).toList();
    }
}

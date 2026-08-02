package com.konbini.application.dto;

import com.konbini.domain.transaction.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable presentation snapshot of a {@link Transaction}.
 */
public record TransactionDTO(String id, String customerId, String customerName,
                             LocalDateTime timestamp, BigDecimal subtotal, BigDecimal tax,
                             BigDecimal discount, BigDecimal total, BigDecimal amountPaid,
                             BigDecimal change, int pointsEarned, int pointsRedeemed,
                             List<String> appliedDiscounts, String taxName,
                             List<TransactionItemDTO> items) {

    /**
     * Creates a DTO from a domain transaction.
     *
     * @param transaction the domain transaction
     * @return the DTO snapshot
     */
    public static TransactionDTO fromDomain(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getCustomer().getId(),
                transaction.getCustomer().getName(),
                transaction.getTimestamp(),
                transaction.getSubtotal(),
                transaction.getTax(),
                transaction.getDiscount(),
                transaction.getTotal(),
                transaction.getAmountPaid(),
                transaction.getChange(),
                transaction.getPointsEarned(),
                transaction.getPointsRedeemed(),
                List.copyOf(transaction.getAppliedDiscounts()),
                transaction.getTaxName(),
                TransactionItemDTO.fromDomainList(transaction.getItems()));
    }
}

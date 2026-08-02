package com.konbini.application.dto;

import java.math.BigDecimal;

/**
 * Immutable snapshot of dashboard summary statistics.
 */
public record DashboardDTO(long totalProducts, long lowStockCount, long expiredCount,
                           long totalCustomers, long totalTransactions, BigDecimal totalSales,
                           long totalItemsSold) {
}

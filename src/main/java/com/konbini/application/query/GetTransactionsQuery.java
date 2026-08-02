package com.konbini.application.query;

import java.time.LocalDate;

/**
 * Request to fetch transactions, optionally filtered by a date range.
 */
public record GetTransactionsQuery(LocalDate startDate, LocalDate endDate) {

    /**
     * Constructs an unfiltered query.
     */
    public GetTransactionsQuery() {
        this(null, null);
    }
}

package com.konbini.application.query;

/**
 * Request to fetch all products.
 */
public record GetProductsQuery(String searchTerm, String categoryDisplayName) {

    /**
     * Constructs an unfiltered query.
     */
    public GetProductsQuery() {
        this(null, null);
    }
}

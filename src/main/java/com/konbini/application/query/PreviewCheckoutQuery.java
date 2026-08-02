package com.konbini.application.query;

import java.util.Map;

/**
 * Request to preview the financial breakdown of a prospective checkout
 * without mutating inventory, loyalty points, or persisting anything.
 * Items is a map of product ID to quantity to mirror
 * {@link com.konbini.application.command.ProcessCheckoutCommand}.
 */
public record PreviewCheckoutQuery(String customerId, Map<String, Integer> items, int pointsToRedeem) {
}

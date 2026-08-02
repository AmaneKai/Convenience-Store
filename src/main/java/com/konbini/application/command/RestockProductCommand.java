package com.konbini.application.command;

/**
 * Request to increase a product's stock by a given amount.
 */
public record RestockProductCommand(String productId, int quantity) {
}

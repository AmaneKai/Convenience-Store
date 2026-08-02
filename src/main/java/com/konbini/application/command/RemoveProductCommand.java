package com.konbini.application.command;

/**
 * Request to remove a product from the inventory.
 */
public record RemoveProductCommand(String productId) {
}

package com.konbini.application.command;

/**
 * Request to authenticate a customer by ID and password.
 */
public record AuthenticateCustomerCommand(String customerId, String password) {
}

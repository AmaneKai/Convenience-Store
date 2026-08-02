package com.konbini.application.command;

/**
 * Staff request to set or reset a customer's self-service login password,
 * used to enable login for customers who were registered without one.
 */
public record SetCustomerPasswordCommand(String customerId, String newPassword) {
}

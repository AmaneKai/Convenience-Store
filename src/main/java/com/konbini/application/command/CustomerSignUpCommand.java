package com.konbini.application.command;

/**
 * Request for a customer to self-register a loyalty account with login
 * credentials (password is hashed before persistence).
 */
public record CustomerSignUpCommand(String name, boolean seniorCitizen, String password) {
}

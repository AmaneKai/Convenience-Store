package com.konbini.application.command;

/**
 * Request to add a new employee (password is hashed before persistence).
 */
public record AddEmployeeCommand(String name, String password) {
}

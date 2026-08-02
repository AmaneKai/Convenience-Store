package com.konbini.application.command;

/**
 * Request to authenticate an employee by ID and password.
 */
public record AuthenticateEmployeeCommand(String employeeId, String password) {
}

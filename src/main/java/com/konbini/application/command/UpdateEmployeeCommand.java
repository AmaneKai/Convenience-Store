package com.konbini.application.command;

/**
 * Request to update an employee's name.
 */
public record UpdateEmployeeCommand(String employeeId, String name) {
}

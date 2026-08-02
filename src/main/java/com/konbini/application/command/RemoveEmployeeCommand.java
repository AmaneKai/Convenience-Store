package com.konbini.application.command;

/**
 * Request to delete an employee by ID.
 */
public record RemoveEmployeeCommand(String employeeId) {
}

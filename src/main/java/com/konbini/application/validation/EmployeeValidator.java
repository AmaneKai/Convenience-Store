package com.konbini.application.validation;

import com.konbini.application.command.AuthenticateEmployeeCommand;
import com.konbini.application.command.AddEmployeeCommand;
import com.konbini.domain.common.DomainError;
import io.vavr.control.Option;

/**
 * Validator for employee-related commands.
 */
public class EmployeeValidator {

    /**
     * Validates an add-employee command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validateAdd(AddEmployeeCommand command) {
        return new ValidationRule<>(command)
                .requiredText(command.name(), "Employee name cannot be empty")
                .requiredText(command.password(), "Employee password cannot be empty")
                .result();
    }

    /**
     * Validates an authentication command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validateAuthenticate(AuthenticateEmployeeCommand command) {
        return new ValidationRule<>(command)
                .requiredText(command.employeeId(), "Employee ID cannot be empty")
                .required(command.password(), "Password cannot be null")
                .result();
    }
}

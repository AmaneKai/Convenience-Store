package com.konbini.application.validation;

import com.konbini.application.command.AuthenticateCustomerCommand;
import com.konbini.application.command.CustomerSignUpCommand;
import com.konbini.application.command.RegisterCustomerCommand;
import com.konbini.application.command.SetCustomerPasswordCommand;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.customer.Customer;
import io.vavr.control.Option;

/**
 * Validator for customer registration and self-service login commands.
 */
public class CustomerValidator {

    /**
     * Minimum length required for a customer's self-service login password.
     */
    public static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Validates a registration command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validate(RegisterCustomerCommand command) {
        ValidationRule<RegisterCustomerCommand> rules = new ValidationRule<>(command)
                .requiredText(command.name(), "Customer name cannot be empty")
                .check(() -> command.name() == null
                        || command.name().matches(Customer.NAME_PATTERN),
                        "Name must contain only letters and spaces");

        boolean cardRequested = command.cardNumber() != null
                && !command.cardNumber().trim().isEmpty();
        boolean expiryRequested = command.cardExpiryDate() != null;

        if (cardRequested && !expiryRequested) {
            rules.require(false, "Card expiry date is required when a card number is provided");
        }
        if (expiryRequested && !cardRequested) {
            rules.require(false, "Card number is required when an expiry date is provided");
        }
        if (expiryRequested) {
            rules.notInPast(command.cardExpiryDate(), "Card expiry date cannot be in the past");
        }

        return rules.result();
    }

    /**
     * Validates a customer self-registration (sign-up) command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validateSignUp(CustomerSignUpCommand command) {
        return new ValidationRule<>(command)
                .requiredText(command.name(), "Customer name cannot be empty")
                .check(() -> command.name() == null
                        || command.name().matches(Customer.NAME_PATTERN),
                        "Name must contain only letters and spaces")
                .check(() -> command.password() != null
                        && command.password().length() >= MIN_PASSWORD_LENGTH,
                        "Password must be at least " + MIN_PASSWORD_LENGTH + " characters")
                .result();
    }

    /**
     * Validates a customer authentication command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validateAuthenticate(AuthenticateCustomerCommand command) {
        return new ValidationRule<>(command)
                .requiredText(command.customerId(), "Customer ID cannot be empty")
                .required(command.password(), "Password cannot be null")
                .result();
    }

    /**
     * Validates a staff set-password command.
     *
     * @param command the command to validate
     * @return None if valid, otherwise the first validation error
     */
    public Option<DomainError> validateSetPassword(SetCustomerPasswordCommand command) {
        return new ValidationRule<>(command)
                .requiredText(command.customerId(), "Customer ID cannot be empty")
                .check(() -> command.newPassword() != null
                        && command.newPassword().length() >= MIN_PASSWORD_LENGTH,
                        "Password must be at least " + MIN_PASSWORD_LENGTH + " characters")
                .result();
    }
}

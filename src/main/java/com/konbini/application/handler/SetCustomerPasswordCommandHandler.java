package com.konbini.application.handler;

import com.konbini.application.command.SetCustomerPasswordCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.validation.CustomerValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.employee.PasswordHasher;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.Optional;

/**
 * Single-purpose handler that lets staff set or reset a customer's
 * self-service login password, enabling login for customers who were
 * registered without one.
 */
public class SetCustomerPasswordCommandHandler implements RequestHandler<SetCustomerPasswordCommand, CustomerDTO> {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;
    private final UnitOfWork unitOfWork;
    private final CustomerValidator validator;

    /**
     * Constructs the set-customer-password handler.
     *
     * @param customerRepository the customer repository
     * @param passwordHasher the password hasher
     * @param unitOfWork the atomic persistence unit
     * @param validator the customer validator
     */
    public SetCustomerPasswordCommandHandler(CustomerRepository customerRepository,
                                             PasswordHasher passwordHasher,
                                             UnitOfWork unitOfWork,
                                             CustomerValidator validator) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
        this.unitOfWork = unitOfWork;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, CustomerDTO> handle(SetCustomerPasswordCommand command) {
        Option<DomainError> validationError = validator.validateSetPassword(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        Optional<Customer> customerOption = customerRepository.findById(command.customerId());
        if (customerOption.isEmpty()) {
            return Either.left(DomainError.notFound("Customer not found"));
        }

        try {
            Customer customer = customerOption.get();
            customer.updatePasswordHash(passwordHasher.hash(command.newPassword()));
            customerRepository.update(customer);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist customer"));
            }
            return Either.right(CustomerDTO.fromDomain(customer));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to set password: " + exception.getMessage()));
        }
    }
}

package com.konbini.application.handler;

import com.konbini.application.command.AuthenticateCustomerCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.session.CustomerSessionContext;
import com.konbini.application.validation.CustomerValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.employee.PasswordHasher;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.Optional;

/**
 * Single-purpose handler that authenticates a customer by ID and password.
 * On success it establishes the session via the {@link CustomerSessionContext}.
 */
public class AuthenticateCustomerCommandHandler implements RequestHandler<AuthenticateCustomerCommand, CustomerDTO> {

    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;
    private final CustomerSessionContext sessionContext;
    private final CustomerValidator validator;

    /**
     * Constructs the authentication handler.
     *
     * @param customerRepository the customer repository
     * @param passwordHasher the password hasher
     * @param sessionContext the customer session context
     * @param validator the customer validator
     */
    public AuthenticateCustomerCommandHandler(CustomerRepository customerRepository,
                                              PasswordHasher passwordHasher,
                                              CustomerSessionContext sessionContext,
                                              CustomerValidator validator) {
        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
        this.sessionContext = sessionContext;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, CustomerDTO> handle(AuthenticateCustomerCommand command) {
        Option<DomainError> validationError = validator.validateAuthenticate(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        Optional<Customer> customerOption = customerRepository.findById(command.customerId());
        if (customerOption.isEmpty() || !customerOption.get().hasPassword()) {
            return Either.left(DomainError.unauthorized("Invalid customer ID or password"));
        }

        Customer customer = customerOption.get();
        if (!passwordHasher.verify(command.password(), customer.getPasswordHash())) {
            return Either.left(DomainError.unauthorized("Invalid customer ID or password"));
        }

        CustomerDTO dto = CustomerDTO.fromDomain(customer);
        sessionContext.setCurrentCustomer(dto);
        return Either.right(dto);
    }
}

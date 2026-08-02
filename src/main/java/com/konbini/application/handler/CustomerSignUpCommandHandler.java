package com.konbini.application.handler;

import com.konbini.application.command.CustomerSignUpCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.validation.CustomerValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.employee.PasswordHasher;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * Single-purpose handler that self-registers a customer with login
 * credentials, hashing the password before persistence so plaintext is
 * never stored.
 */
public class CustomerSignUpCommandHandler implements RequestHandler<CustomerSignUpCommand, CustomerDTO> {

    private final CustomerRepository customerRepository;
    private final IdentifierGenerator identifierGenerator;
    private final PasswordHasher passwordHasher;
    private final UnitOfWork unitOfWork;
    private final CustomerValidator validator;

    /**
     * Constructs the customer sign-up handler.
     *
     * @param customerRepository the customer repository
     * @param identifierGenerator the ID generator
     * @param passwordHasher the password hasher
     * @param unitOfWork the atomic persistence unit
     * @param validator the customer validator
     */
    public CustomerSignUpCommandHandler(CustomerRepository customerRepository,
                                        IdentifierGenerator identifierGenerator,
                                        PasswordHasher passwordHasher,
                                        UnitOfWork unitOfWork,
                                        CustomerValidator validator) {
        this.customerRepository = customerRepository;
        this.identifierGenerator = identifierGenerator;
        this.passwordHasher = passwordHasher;
        this.unitOfWork = unitOfWork;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, CustomerDTO> handle(CustomerSignUpCommand command) {
        Option<DomainError> validationError = validator.validateSignUp(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        try {
            String passwordHash = passwordHasher.hash(command.password());
            Customer customer = Customer.builder()
                    .id(identifierGenerator.generate("customer"))
                    .name(command.name())
                    .seniorCitizen(command.seniorCitizen())
                    .passwordHash(passwordHash)
                    .build();

            customerRepository.add(customer);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist customer"));
            }
            return Either.right(CustomerDTO.fromDomain(customer));
        } catch (IllegalArgumentException exception) {
            return Either.left(DomainError.validation(exception.getMessage()));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to create account: " + exception.getMessage()));
        }
    }
}

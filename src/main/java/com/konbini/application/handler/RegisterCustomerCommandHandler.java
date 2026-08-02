package com.konbini.application.handler;

import com.konbini.application.command.RegisterCustomerCommand;
import com.konbini.application.dto.CustomerDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.validation.CustomerValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.customer.Customer;
import com.konbini.domain.customer.CustomerRepository;
import com.konbini.domain.customer.MembershipCard;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * Single-purpose handler that registers a new customer, optionally with a
 * membership card.
 */
public class RegisterCustomerCommandHandler implements RequestHandler<RegisterCustomerCommand, CustomerDTO> {

    private final CustomerRepository customerRepository;
    private final IdentifierGenerator identifierGenerator;
    private final UnitOfWork unitOfWork;
    private final CustomerValidator validator;

    /**
     * Constructs the register-customer handler.
     *
     * @param customerRepository the customer repository
     * @param identifierGenerator the ID generator
     * @param unitOfWork the atomic persistence unit
     * @param validator the customer validator
     */
    public RegisterCustomerCommandHandler(CustomerRepository customerRepository,
                                          IdentifierGenerator identifierGenerator,
                                          UnitOfWork unitOfWork,
                                          CustomerValidator validator) {
        this.customerRepository = customerRepository;
        this.identifierGenerator = identifierGenerator;
        this.unitOfWork = unitOfWork;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, CustomerDTO> handle(RegisterCustomerCommand command) {
        Option<DomainError> validationError = validator.validate(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        try {
            MembershipCard membershipCard = buildMembershipCard(command);
            Customer customer = Customer.builder()
                    .id(identifierGenerator.generate("customer"))
                    .name(command.name())
                    .seniorCitizen(command.seniorCitizen())
                    .membershipCard(membershipCard)
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
                    "Failed to register customer: " + exception.getMessage()));
        }
    }

    /**
     * Builds a membership card from the command, or null when none was requested.
     *
     * @param command the registration command
     * @return a membership card, or null
     */
    private MembershipCard buildMembershipCard(RegisterCustomerCommand command) {
        if (command.cardNumber() == null || command.cardNumber().trim().isEmpty()) {
            return null;
        }
        return MembershipCard.builder()
                .id(identifierGenerator.generate("card"))
                .cardNumber(command.cardNumber())
                .expiryDate(command.cardExpiryDate())
                .build();
    }
}

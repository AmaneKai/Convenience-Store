package com.konbini.application.handler;

import com.konbini.application.command.AddEmployeeCommand;
import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.validation.EmployeeValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.common.IdentifierGenerator;
import com.konbini.domain.employee.Employee;
import com.konbini.domain.employee.EmployeeRepository;
import com.konbini.domain.employee.PasswordHasher;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * Single-purpose handler that registers a new employee, hashing the password
 * before persistence so plaintext is never stored.
 */
public class AddEmployeeCommandHandler implements RequestHandler<AddEmployeeCommand, EmployeeDTO> {

    private final EmployeeRepository employeeRepository;
    private final IdentifierGenerator identifierGenerator;
    private final PasswordHasher passwordHasher;
    private final UnitOfWork unitOfWork;
    private final EmployeeValidator validator;

    /**
     * Constructs the add-employee handler.
     *
     * @param employeeRepository the employee repository
     * @param identifierGenerator the ID generator
     * @param passwordHasher the password hasher
     * @param unitOfWork the atomic persistence unit
     * @param validator the employee validator
     */
    public AddEmployeeCommandHandler(EmployeeRepository employeeRepository,
                                     IdentifierGenerator identifierGenerator,
                                     PasswordHasher passwordHasher,
                                     UnitOfWork unitOfWork,
                                     EmployeeValidator validator) {
        this.employeeRepository = employeeRepository;
        this.identifierGenerator = identifierGenerator;
        this.passwordHasher = passwordHasher;
        this.unitOfWork = unitOfWork;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, EmployeeDTO> handle(AddEmployeeCommand command) {
        Option<DomainError> validationError = validator.validateAdd(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        try {
            String passwordHash = passwordHasher.hash(command.password());
            Employee employee = Employee.builder()
                    .id(identifierGenerator.generate("employee"))
                    .name(command.name())
                    .passwordHash(passwordHash)
                    .build();

            employeeRepository.add(employee);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist employee"));
            }
            return Either.right(EmployeeDTO.fromDomain(employee));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to add employee: " + exception.getMessage()));
        }
    }
}

package com.konbini.application.handler;

import com.konbini.application.command.AuthenticateEmployeeCommand;
import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.application.session.SessionContext;
import com.konbini.application.validation.EmployeeValidator;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.employee.Employee;
import com.konbini.domain.employee.EmployeeRepository;
import com.konbini.domain.employee.PasswordHasher;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.Optional;

/**
 * Single-purpose handler that authenticates an employee by ID and password.
 * On success it establishes the session via the {@link SessionContext}.
 */
public class AuthenticateEmployeeCommandHandler implements RequestHandler<AuthenticateEmployeeCommand, EmployeeDTO> {

    private final EmployeeRepository employeeRepository;
    private final PasswordHasher passwordHasher;
    private final SessionContext sessionContext;
    private final EmployeeValidator validator;

    /**
     * Constructs the authentication handler.
     *
     * @param employeeRepository the employee repository
     * @param passwordHasher the password hasher
     * @param sessionContext the session context
     * @param validator the employee validator
     */
    public AuthenticateEmployeeCommandHandler(EmployeeRepository employeeRepository,
                                              PasswordHasher passwordHasher,
                                              SessionContext sessionContext,
                                              EmployeeValidator validator) {
        this.employeeRepository = employeeRepository;
        this.passwordHasher = passwordHasher;
        this.sessionContext = sessionContext;
        this.validator = validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, EmployeeDTO> handle(AuthenticateEmployeeCommand command) {
        Option<DomainError> validationError = validator.validateAuthenticate(command);
        if (validationError.isDefined()) {
            return Either.left(validationError.get());
        }

        Optional<Employee> employeeOption = employeeRepository.findById(command.employeeId());
        if (employeeOption.isEmpty()) {
            return Either.left(DomainError.unauthorized("Invalid employee ID or password"));
        }

        Employee employee = employeeOption.get();
        if (!passwordHasher.verify(command.password(), employee.getPasswordHash())) {
            return Either.left(DomainError.unauthorized("Invalid employee ID or password"));
        }

        EmployeeDTO dto = EmployeeDTO.fromDomain(employee);
        sessionContext.setCurrentEmployee(dto);
        return Either.right(dto);
    }
}

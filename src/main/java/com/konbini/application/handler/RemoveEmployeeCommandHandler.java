package com.konbini.application.handler;

import com.konbini.application.command.RemoveEmployeeCommand;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.employee.EmployeeRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import java.util.Optional;

/**
 * Single-purpose handler that removes an employee.
 */
public class RemoveEmployeeCommandHandler implements RequestHandler<RemoveEmployeeCommand, Boolean> {

    private final EmployeeRepository employeeRepository;
    private final UnitOfWork unitOfWork;

    /**
     * Constructs the remove-employee handler.
     *
     * @param employeeRepository the employee repository
     * @param unitOfWork the atomic persistence unit
     */
    public RemoveEmployeeCommandHandler(EmployeeRepository employeeRepository, UnitOfWork unitOfWork) {
        this.employeeRepository = employeeRepository;
        this.unitOfWork = unitOfWork;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, Boolean> handle(RemoveEmployeeCommand command) {
        if (command.employeeId() == null || command.employeeId().trim().isEmpty()) {
            return Either.left(DomainError.validation("Employee ID cannot be empty"));
        }

        Optional<?> existing = employeeRepository.findById(command.employeeId());
        if (existing.isEmpty()) {
            return Either.left(DomainError.notFound(
                    "Employee not found: " + command.employeeId()));
        }

        try {
            employeeRepository.remove(command.employeeId());
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist employee removal"));
            }
            return Either.right(true);
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to remove employee: " + exception.getMessage()));
        }
    }
}

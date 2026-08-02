package com.konbini.application.handler;

import com.konbini.application.command.UpdateEmployeeCommand;
import com.konbini.application.dto.EmployeeDTO;
import com.konbini.application.mediator.RequestHandler;
import com.konbini.domain.common.DomainError;
import com.konbini.domain.employee.Employee;
import com.konbini.domain.employee.EmployeeRepository;
import com.konbini.domain.unitofwork.UnitOfWork;
import io.vavr.control.Either;
import java.util.Optional;

/**
 * Single-purpose handler that updates an employee's name.
 */
public class UpdateEmployeeCommandHandler implements RequestHandler<UpdateEmployeeCommand, EmployeeDTO> {

    private final EmployeeRepository employeeRepository;
    private final UnitOfWork unitOfWork;

    /**
     * Constructs the update-employee handler.
     *
     * @param employeeRepository the employee repository
     * @param unitOfWork the atomic persistence unit
     */
    public UpdateEmployeeCommandHandler(EmployeeRepository employeeRepository, UnitOfWork unitOfWork) {
        this.employeeRepository = employeeRepository;
        this.unitOfWork = unitOfWork;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Either<DomainError, EmployeeDTO> handle(UpdateEmployeeCommand command) {
        if (command.employeeId() == null || command.employeeId().trim().isEmpty()) {
            return Either.left(DomainError.validation("Employee ID cannot be empty"));
        }
        if (command.name() == null || command.name().trim().isEmpty()) {
            return Either.left(DomainError.validation("Employee name cannot be empty"));
        }

        Optional<Employee> employeeOption = employeeRepository.findById(command.employeeId());
        if (employeeOption.isEmpty()) {
            return Either.left(DomainError.notFound(
                    "Employee not found: " + command.employeeId()));
        }

        try {
            Employee employee = employeeOption.get();
            employee.updateName(command.name());

            employeeRepository.update(employee);
            boolean committed = unitOfWork.commit();
            if (!committed) {
                return Either.left(DomainError.persistence("Failed to persist employee"));
            }
            return Either.right(EmployeeDTO.fromDomain(employee));
        } catch (Exception exception) {
            return Either.left(DomainError.persistence(
                    "Failed to update employee: " + exception.getMessage()));
        }
    }
}
